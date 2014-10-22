package com.chengyi.eagleeye.network.redis;

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
import com.chengyi.eagleeye.model.message.redis.RedisMessage;
import com.chengyi.eagleeye.model.message.redis.RedisMessageStat;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.service.MessageMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.DateUtil;
import com.chengyi.eagleeye.util.NetUtil;

public class StatRedisMessage {
	private static Logger logger = Logger.getLogger(StatRedisMessage.class);

	public static int randMinute = 1 + new Random().nextInt(10); // [1, 10]

	@Autowired
	private MessageMgr messageMgr;

	private static final String redisMessageStartTimeKey = ApplicaRuntime.globalFlag + "statkey_redis_message_starttime";

	public static final long msInAnHour = 3600 * 1000;
	private int interval = 1 * 60 * 1000;

	public static final SimpleDateFormat mFormatDateTime = new SimpleDateFormat("yyyyMMddHH");

	public static final SimpleDateFormat dayFormatDateTime = new SimpleDateFormat("yyyyMMdd");

	public void execute() {
		new Thread() {
			public void run() {
				String localIp = NetUtil.getLocalIp();
				if (localIp != null && (localIp.contains("32.119") || localIp.contains("32.67"))) {
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

			Long startTime = getRedisMessageStartTime();
			while (startTime < currentTime) {
				Date date = new Date(startTime);
				String dateHourStr = DateUtil.format(date, mFormatDateTime);

				long formatStartTime = DateUtil.parse(dateHourStr, mFormatDateTime).getTime();
				long formatEndTime = formatStartTime + msInAnHour;

				dojob(formatStartTime, formatEndTime, dateHourStr);
				deleteExpiredData(); // 删除1天前的数据

				setRedisMessageStartTime(formatEndTime);
				startTime = getRedisMessageStartTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Long getRedisMessageStartTime() {
		Long startTime = RedisUtil.getLong(redisMessageStartTimeKey);
		if (startTime == null) {
			startTime = 1392213600000L; // 2014-02-12 22:00:00
		}
		return startTime;
	}

	private void setRedisMessageStartTime(Long formatEndTime) {
		RedisUtil.setLong(redisMessageStartTimeKey, formatEndTime);
	}

	public void deleteExpiredData() {
		try {
			long endTime = DateUtil.parse(DateUtil.format(new Date(), dayFormatDateTime), dayFormatDateTime).getTime();

			messageMgr.deleteRedisMessageByTime(endTime);

			logger.info("delete from endTime:" + endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void dojob(long formatStartTime, long formatEndTime, String datestr) {
		List<RedisMessage> hmList = messageMgr.getRedisMessageByTimeDiff(formatStartTime, formatEndTime);
		Map<String, List<RedisMessage>> hmMap = new HashMap<String, List<RedisMessage>>();
		if (CollectionUtils.isNotEmpty(hmList)) {
			for (RedisMessage nm : hmList) {
				String key = nm.getItemId() + "-" + nm.getServerIp();
				List<RedisMessage> hms = hmMap.get(key);
				if (hms == null) {
					hms = new ArrayList<RedisMessage>();
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
			List<RedisMessage> hms = hmMap.get(key);
			RedisMessageStat hmStat = new RedisMessageStat();

			if (CollectionUtils.isNotEmpty(hms)) {
				int statusOkCount = 0, clients = 0;
				float usedMemory = 0.0f, cps = 0.0f, hitRate = 0.0f;

				for (RedisMessage hm : hms) {
					hmStat.setUserId(hm.getUserId());
					hmStat.setItemId(hm.getItemId());
					hmStat.setServerIp(hm.getServerIp());
					hmStat.setType(hm.getType());
					hmStat.setWorkerIp(hm.getWorkerIp());
					hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
					hmStat.setCreateTime(CommonUtil.getLong(datestr));

					if (hm.getStatus() == RedisResult.STATUS_OK) {
						statusOkCount++;

						if (hmStat.getMinUsedMemory() == 0.0f) {
							hmStat.setMinUsedMemory(hm.getCurrentUsedMemory());
						} else {
							if (hmStat.getMinUsedMemory() > hm.getCurrentUsedMemory()) {
								hmStat.setMinUsedMemory(hm.getCurrentUsedMemory());
							}
						}

						if (hmStat.getMaxUsedMemory() == 0.0f) {
							hmStat.setMaxUsedMemory(hm.getCurrentUsedMemory());
						} else {
							if (hmStat.getMaxUsedMemory() < hm.getCurrentUsedMemory()) {
								hmStat.setMaxUsedMemory(hm.getCurrentUsedMemory());
							}
						}

						
						if (hmStat.getMinCPS() == 0.0f) {
							hmStat.setMinCPS(hm.getCurrentCPS());
						} else {
							if (hmStat.getMinCPS() > hm.getCurrentCPS()) {
								hmStat.setMinCPS(hm.getCurrentCPS());
							}
						}

						if (hmStat.getMaxCPS() == 0.0f) {
							hmStat.setMaxCPS(hm.getCurrentCPS());
						} else {
							if (hmStat.getMaxCPS() < hm.getCurrentCPS()) {
								hmStat.setMaxCPS(hm.getCurrentCPS());
							}
						}


						if (hmStat.getMinHitRate() == 0.0f) {
							hmStat.setMinHitRate(hm.getHitRate());
						} else {
							if (hmStat.getMinHitRate() > hm.getHitRate()) {
								hmStat.setMinHitRate(hm.getHitRate());
							}
						}

						if (hmStat.getMaxHitRate() == 0.0f) {
							hmStat.setMaxHitRate(hm.getHitRate());
						} else {
							if (hmStat.getMaxHitRate() < hm.getHitRate()) {
								hmStat.setMaxHitRate(hm.getHitRate());
							}
						}

						usedMemory += hm.getCurrentUsedMemory();
						cps += hm.getCurrentCPS();
						hitRate += hm.getHitRate();
						clients += hm.getClients();

						hmStat.setVersion(hm.getVersion());
						hmStat.setMode(hm.getMode());
						hmStat.setTotalTime(hm.getTotalTime());
						
						
						hmStat.setAvgUsedMemory((float) (usedMemory * 1.0 / statusOkCount));
						hmStat.setAvgCPS((float) (cps * 1.0 / statusOkCount));
						hmStat.setAvgHitRate((float) (hitRate * 1.0 / statusOkCount));
						hmStat.setClients((int) (clients * 1.0 / statusOkCount));
					}
				}
			}

			messageMgr.saveRedisMessageStat(hmStat);
		}

	}

}
