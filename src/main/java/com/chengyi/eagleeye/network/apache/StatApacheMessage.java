package com.chengyi.eagleeye.network.apache;

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
import com.chengyi.eagleeye.model.message.nginx.NginxMessage;
import com.chengyi.eagleeye.model.message.nginx.NginxMessageStat;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.service.MessageMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.DateUtil;

// TODO:
public class StatApacheMessage {
	private static Logger logger = Logger.getLogger(StatApacheMessage.class);

	public static int randMinute = 1 + new Random().nextInt(10); // [1, 10]

	@Autowired
	private MessageMgr messageMgr;

	private static final String nginxMessageStartTimeKey = "statkey_nginx_message_starttime";

	public static final long msInAnHour = 3600 * 1000;
	private int interval = 1 * 60 * 1000;

	public static final SimpleDateFormat mFormatDateTime = new SimpleDateFormat("yyyyMMddHH");

	public static final SimpleDateFormat dayFormatDateTime = new SimpleDateFormat("yyyyMMdd");

	public void execute() {
		new Thread() {
			public void run() {
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
		}.start();
	}

	public void schedule() {
		try {
			long currentTime = DateUtil.parse(DateUtil.format(new Date(), mFormatDateTime), mFormatDateTime).getTime();

			Long startTime = getNginxMessageStartTime();
			while (startTime < currentTime) {
				Date date = new Date(startTime);
				String dateHourStr = DateUtil.format(date, mFormatDateTime);

				long formatStartTime = DateUtil.parse(dateHourStr, mFormatDateTime).getTime();
				long formatEndTime = formatStartTime + msInAnHour;

				dojob(formatStartTime, formatEndTime, dateHourStr);
				deleteExpiredData(); // 删除1天前的数据

				setNginxMessageStartTime(formatEndTime);
				startTime = getNginxMessageStartTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Long getNginxMessageStartTime() {
		Long startTime = RedisUtil.getLong(nginxMessageStartTimeKey);
		if (startTime == null) {
			startTime = 1392213600000L; // 2014-02-12 22:00:00
		}
		return startTime;
	}

	private void setNginxMessageStartTime(Long formatEndTime) {
		RedisUtil.setLong(nginxMessageStartTimeKey, formatEndTime);
	}

	public void deleteExpiredData() {
		try {
			long endTime = DateUtil.parse(DateUtil.format(new Date(), dayFormatDateTime), dayFormatDateTime).getTime();

			messageMgr.deleteNginxMessageByTime(endTime);

			logger.info("delete from endTime:" + endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void dojob(long formatStartTime, long formatEndTime, String datestr) {
		List<NginxMessage> hmList = messageMgr.getNginxMessageByTimeDiff(formatStartTime, formatEndTime);
		Map<String, List<NginxMessage>> hmMap = new HashMap<String, List<NginxMessage>>();
		if (CollectionUtils.isNotEmpty(hmList)) {
			for (NginxMessage nm : hmList) {
				String key = nm.getItemId() + "-" + nm.getServerIp();
				List<NginxMessage> hms = hmMap.get(key);
				if (hms == null) {
					hms = new ArrayList<NginxMessage>();
				}
				hms.add(nm);

				hmMap.put(key, hms);
			}
		}

		// query hmMap
		Set<String> keySet = hmMap.keySet();
		Iterator<String> keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			List<NginxMessage> hms = hmMap.get(key);
			NginxMessageStat hmStat = new NginxMessageStat();
			
			if (CollectionUtils.isNotEmpty(hms)) {
				int statusOkCount = 0, activeConnCount = 0, readingConnCount = 0, writingConnCount = 0, waitingConnCount = 0;
				float throughputRate = 0.0f;
				
				for (NginxMessage hm : hms) {
					hmStat.setUserId(hm.getUserId());
					hmStat.setItemId(hm.getItemId());
					hmStat.setServerIp(hm.getServerIp());
					hmStat.setType(hm.getType());
					hmStat.setWorkerIp(hm.getWorkerIp());
					hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
					hmStat.setCreateTime(CommonUtil.getLong(datestr));

					if (hm.getStatus() == ApacheResult.STATUS_OK) {
						statusOkCount++;
						if (hmStat.getMinThroughputRate() == 0.0f) {
							hmStat.setMinThroughputRate(hm.getThroughputRate());
						} else {
							if (hmStat.getMinThroughputRate() > hm.getThroughputRate()) {
								hmStat.setMinThroughputRate(hm.getThroughputRate());
							}
						}
						
						if (hmStat.getMaxThroughputRate() == 0.0f) {
							hmStat.setMaxThroughputRate(hm.getThroughputRate());
						} else {
							if (hmStat.getMaxThroughputRate() < hm.getThroughputRate()) {
								hmStat.setMaxThroughputRate(hm.getThroughputRate());
							}
						}
						
						throughputRate += hm.getThroughputRate();
						
						activeConnCount += hm.getActiveConn();
						readingConnCount += hm.getReadingConn();
						writingConnCount += hm.getWritingConn();
						waitingConnCount += hm.getWaitingConn();
						
						hmStat.setTotalConn(hmStat.getTotalConn() + hm.getTotalConn());
						hmStat.setTotalHandshake(hmStat.getTotalHandshake() + hm.getTotalHandshake());
						hmStat.setTotalRequest(hmStat.getTotalRequest() + hm.getTotalRequest());
					}
					hmStat.setAverageThroughputRate(throughputRate /  statusOkCount);
					
					hmStat.setActiveConn((float)(activeConnCount * 1.0 /  statusOkCount));
					hmStat.setReadingConn((float)(readingConnCount * 1.0 /  statusOkCount));
					hmStat.setWritingConn((float)(writingConnCount * 1.0 /  statusOkCount));
					hmStat.setWaitingConn((float)(waitingConnCount * 1.0 /  statusOkCount));
				}
			}

			messageMgr.saveNginxMessageStat(hmStat);
		}

	}

}
