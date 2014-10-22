package com.chengyi.eagleeye.patrol.redis;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.message.redis.RedisMessage;
import com.chengyi.eagleeye.network.redis.RedisParam;
import com.chengyi.eagleeye.network.redis.RedisResult;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.NetUtil;
import com.chengyi.eagleeye.util.ServerStatus;

public class RedisThread implements Runnable {
	private static final Logger logger = Logger.getLogger(RedisThread.class);

	private static final int sleepInterval = 5000; // in ms

	private Item item;
	private RedisParam redisParam;

	public RedisThread(Item item, RedisParam redisParam) {
		this.item = item;
		this.redisParam = redisParam;
	}

	public void saveMessageSummary(RedisParam redisParam, RedisResult redisResult1, RedisResult redisResult2) {
		RedisMessage message = new RedisMessage();

		message.setUserId(item.getUserId());
		message.setItemId(item.getId());
		message.setServerIp(redisParam.getBindAddress() == null ? "" : redisParam.getBindAddress());
		message.setType(Item.TYPE_REDIS);
		message.setStatus(redisResult2.getStatus());
		message.setWorkerIp(NetUtil.getLocalIp());
		message.setCreateTime(System.currentTimeMillis());

		message.setVersion(redisResult2.getVersion());
		message.setMode(redisResult2.getMode());
		message.setTotalTime(redisResult2.getTotalTime());
		message.setMaxUsedMemory(redisResult2.getMaxUsedMemory());
		message.setCurrentUsedMemory((redisResult1.getCurrentUsedMemory() + redisResult2.getCurrentUsedMemory()) / 2);

		message.setCurrentCPS((redisResult1.getCurrentCPS() + redisResult2.getCurrentCPS()) / 2);
		message.setHitRate((float) (redisResult2.getHitCount() * 1. / (redisResult2.getHitCount() + redisResult2.getMissCount())));
		message.setClients((redisResult1.getCurrentClients() + redisResult2.getCurrentClients()) / 2);

		com.chengyi.eagleeye.patrol.RedisUtil.pushMessage2PendingSet(message);
	}

	
	public void run() {
		Thread retryThread = null;
		while (true) {
			RedisResult redisResult1 = com.chengyi.eagleeye.network.redis.RedisUtil.getRedisStatusPage(redisParam.getUri(), redisParam.getRedisOption(), redisParam.isUseSecure());
			ApplicaRuntime.sleep(sleepInterval); // sleep for 5 seconds
			RedisResult redisResult2 = com.chengyi.eagleeye.network.redis.RedisUtil.getRedisStatusPage(redisParam.getUri(), redisParam.getRedisOption(), redisParam.isUseSecure());

			logger.info(redisParam.getWorkUri() + ", redisResult2.status:" + (redisParam == null ? null : redisResult2.getStatus()));
			saveMessageSummary(redisParam, redisResult1, redisResult2);

			if (redisResult2.getStatus() != RedisResult.STATUS_OK) {
				redisParam.setFailTimes(redisParam.getFailTimes() + 1);
				redisParam.setLastErrorRedisResult(redisResult2);
				logger.info(redisParam.getWorkUri() + ", redisParam.failTimes:" + redisParam.getFailTimes() + ", succTimes:" + redisParam.getSuccTimes());

				if (retryThread == null || !retryThread.isAlive()) {
					retryThread = new Thread(new RetryRedisThread(item, redisParam));
					retryThread.start();
				}
			} else if (redisResult2.getStatus() == RedisResult.STATUS_OK) {
				if (redisParam.getServerStatus() == ServerStatus.DOWN) {
					redisParam.setSuccTimes(redisParam.getSuccTimes() + 1);
					logger.info(redisParam.getWorkUri() + ", redisParam.succTimes:" + redisParam.getSuccTimes() + ", failTimes:" + redisParam.getFailTimes());

					if (retryThread == null || !retryThread.isAlive()) {
						retryThread = new Thread(new RetryRedisThread(item, redisParam));
						retryThread.start();
					}
				}
				redisParam.add2Queue(1);
			}

			// 设置监控频率
			ApplicaRuntime.sleep(item.getMonitorFreq() * 1000);
		}

	}

}
