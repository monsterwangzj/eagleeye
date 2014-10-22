package com.chengyi.eagleeye.patrol.ping;

import java.util.Date;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.message.ping.PingMessage;
import com.chengyi.eagleeye.network.ping.PingParam;
import com.chengyi.eagleeye.network.ping.PingResult;
import com.chengyi.eagleeye.network.ping.PingUtil;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.DateUtil;
import com.chengyi.eagleeye.util.NetUtil;
import com.chengyi.eagleeye.util.ServerStatus;

public class PingThread implements Runnable {
	private static final Logger logger = Logger.getLogger(PingThread.class);

	private Item item;
	private PingParam pingParam;

	public PingThread(Item item, PingParam pingParam) {
		this.item = item;
		this.pingParam = pingParam;
	}

	public void saveMessageSummary(PingParam pingParam, PingResult pingResult) {
		PingMessage message = new PingMessage();
		
		message.setUserId(item.getUserId());
		message.setItemId(item.getId());
		message.setServerIp(pingParam.getBindAddress() == null ? "" : pingParam.getBindAddress());
		message.setType(Item.TYPE_PING);
		
		message.setLossPercent(pingResult.getLossPercent());
		message.setMinimum(pingResult.getMinimum());
		message.setMaximum(pingResult.getMaximum());
		message.setAverage(pingResult.getAverage());
		
		message.setStatus(pingResult.getStatus());
		message.setWorkerIp(NetUtil.getLocalIp());
		message.setCreateTime(System.currentTimeMillis());
		
		RedisUtil.pushMessage2PendingSet(message);
		
		Long itemId = message.getItemId();
		String datestr = DateUtil.format8chars(new Date());
		
		RedisUtil.incr(ApplicaRuntime.globalFlag + itemId + "_" + datestr + "_totalAccessCount");
		RedisUtil.incr(ApplicaRuntime.globalFlag + itemId + "_" + datestr + "_totalUsablity", (long) (100 - pingResult.getLossPercent()));
		RedisUtil.incr(ApplicaRuntime.globalFlag + itemId + "_" + datestr + "_succAccessCostTime", (long) (pingResult.getAverage() * 1000)); // timeunit in us
	}

	
	public void run() {
		Thread retryThread = null;
		while (true) {
			PingResult pingResult = PingUtil.doPingCmd(pingParam.getUri());
			logger.info(pingParam.getWorkUri() + ", pingResult.status:" + (pingResult == null ? null : pingResult.getStatus()));

			saveMessageSummary(pingParam, pingResult);

			if (pingResult.getStatus() != PingResult.STATUS_OK) {
				pingParam.setFailTimes(pingParam.getFailTimes() + 1);
				pingParam.setLastErrorPingResult(pingResult);
				logger.info(pingParam.getWorkUri() + ", pingParam.failTimes:" + pingParam.getFailTimes() + ", succTimes:" + pingParam.getSuccTimes());

				if (retryThread == null || !retryThread.isAlive()) {
					retryThread = new Thread(new RetryPingThread(item, pingParam));
					retryThread.start();
				}
			} else if (pingResult.getStatus() == PingResult.STATUS_OK) {
				if (pingParam.getServerStatus() == ServerStatus.DOWN) {
					pingParam.setSuccTimes(pingParam.getSuccTimes() + 1);
					logger.info(pingParam.getWorkUri() + ", pingParam.succTimes:" + pingParam.getSuccTimes() + ", failTimes:" + pingParam.getFailTimes());

					if (retryThread == null || !retryThread.isAlive()) {
						retryThread = new Thread(new RetryPingThread(item, pingParam));
						retryThread.start();
					}
				}
				pingParam.add2Queue(1);
			}

			// 设置监控频率
			ApplicaRuntime.sleep(item.getMonitorFreq() * 1000);
		}

	}

}
