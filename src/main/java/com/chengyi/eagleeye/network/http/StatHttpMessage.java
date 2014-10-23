package com.chengyi.eagleeye.network.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.message.MessageStat;
import com.chengyi.eagleeye.model.message.http.HttpMessage;
import com.chengyi.eagleeye.model.message.http.HttpMessageStat;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.service.MessageMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.DateUtil;
import com.chengyi.eagleeye.util.NetUtil;

public class StatHttpMessage {
	private static Logger logger = Logger.getLogger(StatHttpMessage.class);

	public static int randMinute = 1 + new Random().nextInt(10); // [1, 10]

	@Autowired
	private MessageMgr messageMgr;

	private static final String httpMessageStartTimeKey = ApplicaRuntime.globalFlag + "statkey_http_message_starttime";

	public static final long msInAnHour = 3600 * 1000;
	private int interval = 1 * 60 * 1000;

	public static final SimpleDateFormat mFormatDateTime = new SimpleDateFormat("yyyyMMddHH");

	public static final SimpleDateFormat dayFormatDateTime = new SimpleDateFormat("yyyyMMdd");

	public void execute() {
		new Thread() {
			public void run() {
				String localIp = NetUtil.getLocalIp();
				if (localIp != null && (localIp.contains("163.157") || localIp.contains("32.67"))) {
					while (true) {
						final SimpleDateFormat mFormatDateTime = new SimpleDateFormat("mm");
						String datestr = DateUtil.format(new Date(), mFormatDateTime);
	
						if (Integer.parseInt(datestr) % 2 == 0) {
							schedule();
							ApplicaRuntime.sleep(interval);
						} else {
							ApplicaRuntime.sleep(10000);
						}
					}
				}
			}
		}.start();
	}

	public void schedule() {
		try {
			long currentTime = DateUtil.parse(DateUtil.format(new Date(), mFormatDateTime), mFormatDateTime).getTime();

			Long startTime = getHttpMessageStartTime();
			while (startTime < currentTime) {
				Date date = new Date(startTime);
				String dateHourStr = DateUtil.format(date, mFormatDateTime);

				long formatStartTime = DateUtil.parse(dateHourStr, mFormatDateTime).getTime();
				long formatEndTime = formatStartTime + msInAnHour;

				dojob(formatStartTime, formatEndTime, dateHourStr);
				deleteExpiredData(); // 删除1天前的数据

				setHttpMessageStartTime(formatEndTime);
				startTime = getHttpMessageStartTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Long getHttpMessageStartTime() {
		Long startTime = RedisUtil.getLong(httpMessageStartTimeKey);
		if (startTime == null) {
			startTime = 1413993600000L; // 2014-10-23 00:00:00 
		}
		return startTime;
	}

	private void setHttpMessageStartTime(Long formatEndTime) {
		RedisUtil.setLong(httpMessageStartTimeKey, formatEndTime);
	}

	public void deleteExpiredData() {
		try {
			long endTime = DateUtil.parse(DateUtil.format(new Date(), dayFormatDateTime), dayFormatDateTime).getTime();

			messageMgr.deleteHttpMessageByTime(endTime);

			logger.info("delete from endTime:" + endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void dojob(long formatStartTime, long formatEndTime, String datestr) {
		List<HttpMessage> hmList = messageMgr.getHttpMessageByTimeDiff(formatStartTime, formatEndTime);
		Map<String, List<HttpMessage>> hmMap = new HashMap<String, List<HttpMessage>>();
		if (CollectionUtils.isNotEmpty(hmList)) {
			for (HttpMessage hm : hmList) {
				String key = hm.getItemId() + "-" + hm.getServerIp();
				List<HttpMessage> hms = hmMap.get(key);
				if (hms == null) {
					hms = new ArrayList<HttpMessage>();
				}
				hms.add(hm);

				hmMap.put(key, hms);
			}
		}

		// query hmMap
		Set<String> keySet = hmMap.keySet();
		Iterator<String> keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			List<HttpMessage> hms = hmMap.get(key);
			HttpMessageStat hmStat = new HttpMessageStat();
			if (CollectionUtils.isNotEmpty(hms)) {
				for (HttpMessage hm : hms) {
					hmStat.setUserId(hm.getUserId());
					hmStat.setItemId(hm.getItemId());
					hmStat.setServerIp(hm.getServerIp());
					hmStat.setType(hm.getType());
					hmStat.setWorkerIp(hm.getWorkerIp());
					hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
					hmStat.setCreateTime(CommonUtil.getLong(datestr));

					hmStat.setTotalAccessCount(hmStat.getTotalAccessCount() + 1);
					if (hm.getStatus() == HttpResult.STATUS_OK) {
						hmStat.setSuccAccessCount(hmStat.getSuccAccessCount() + 1);
						hmStat.setSuccAccessCostTime(hmStat.getSuccAccessCostTime() + hm.getTotalTime());

						hmStat.setSuccDnsLookupTime(hmStat.getSuccDnsLookupTime() + hm.getDnsLookupTime());
						hmStat.setSuccConnectingTime(hmStat.getSuccConnectingTime() + hm.getConnectingTime());
						hmStat.setSuccWaitingTime(hmStat.getSuccWaitingTime() + hm.getWaitingTime());
						hmStat.setSuccReceivingTime(hmStat.getSuccReceivingTime() + hm.getReceivingTime());

						if (hmStat.getMinResponseTime() == 0L) {
							hmStat.setMinResponseTime(hm.getTotalTime());
						} else {
							if (hmStat.getMinResponseTime() > hm.getTotalTime()) {
								hmStat.setMinResponseTime(hm.getTotalTime());
							}
						}
						if (hmStat.getMaxResponseTime() == 0L) {
							hmStat.setMaxResponseTime(hm.getTotalTime());
						} else {
							if (hmStat.getMaxResponseTime() < hm.getTotalTime()) {
								hmStat.setMaxResponseTime(hm.getTotalTime());
							}
						}

						int classLevel = HttpMessageStat.getClassByResponseTime(hm.getTotalTime());
						switch (classLevel) {
						case 1:
							hmStat.setClass1Count(hmStat.getClass1Count() + 1);
							break;
						case 2:
							hmStat.setClass2Count(hmStat.getClass2Count() + 1);
							break;
						case 3:
							hmStat.setClass3Count(hmStat.getClass3Count() + 1);
							break;
						case 4:
							hmStat.setClass4Count(hmStat.getClass4Count() + 1);
							break;
						case 5:
							hmStat.setClass5Count(hmStat.getClass5Count() + 1);
							break;
						case 6:
							hmStat.setClass6Count(hmStat.getClass6Count() + 1);
							break;
						default:
							hmStat.setClass6Count(hmStat.getClass6Count() + 1);
							break;
						}
					}
				}
			}

			messageMgr.saveHttpMessageStat(hmStat);
		}

	}

}
