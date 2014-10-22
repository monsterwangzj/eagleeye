package com.chengyi.eagleeye.patrol.redis;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.network.redis.RedisParam;
import com.chengyi.eagleeye.network.redis.RedisResult;
import com.chengyi.eagleeye.patrol.Monitor;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public class RetryRedisThread implements Runnable {
	private static final Logger logger = Logger.getLogger(RetryRedisThread.class);

	private RedisParam redisParam;
	private Item item;

	public RetryRedisThread(Item item, RedisParam redisParam) {
		logger.info("new RetryRedisThread for item:" + item.getId() + ", url:" + redisParam.getUri() + ", redisOption:" + redisParam.getRedisOption());

		redisParam.setRetryTimes(0);
		this.item = item;
		this.redisParam = redisParam;
	}

	
	public void run() {
		RedisResult redisResult = null;
		while (redisParam.getRetryTimes() < item.getRetryTimes()) {
			redisParam.setRetryTimes(redisParam.getRetryTimes() + 1);
			
			redisResult = com.chengyi.eagleeye.network.redis.RedisUtil.getRedisStatusPage(redisParam.getUri(), redisParam.getRedisOption(), redisParam.isUseSecure());

			logger.info(redisParam.getWorkUri() + ", redisResult.status:" + (redisResult == null ? null : redisResult.getStatus()) + ", retryTimes:" + redisParam.getRetryTimes());

			if (redisResult.getStatus() != RedisResult.STATUS_OK) {
				redisParam.setFailTimes(redisParam.getFailTimes() + 1);
				redisParam.setLastErrorRedisResult(redisResult);

			} else if (redisResult.getStatus() == RedisResult.STATUS_OK) {
				if (redisParam.getServerStatus() == ServerStatus.DOWN) {
					redisParam.setSuccTimes(redisParam.getSuccTimes() + 1);
				}
			}

			ApplicaRuntime.sleep(Math.min(item.getRetryInterval(), item.getMonitorFreq()) * 1000); // 重试时间不大于监控频率
		}

		Monitor.getInstance().alarm(item, redisParam);
		logger.info(redisParam.getWorkUri() + ", redisParam.failTimes:" + redisParam.getFailTimes() + ", succTimes:" + redisParam.getSuccTimes() + " :: retryThread ended.");
	}

}
