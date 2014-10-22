package com.chengyi.eagleeye.patrol.ping;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.network.ping.PingParam;
import com.chengyi.eagleeye.network.ping.PingResult;
import com.chengyi.eagleeye.network.ping.PingUtil;
import com.chengyi.eagleeye.patrol.Monitor;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public class RetryPingThread implements Runnable {
	private static final Logger logger = Logger.getLogger(RetryPingThread.class);

	private PingParam pingParam;
	private Item item;
	
	public RetryPingThread(Item item, PingParam pingParam) {
		logger.info("new retryPingThread for item:" + item.getId() + ", url:" + pingParam.getUri());

		pingParam.setRetryTimes(0);
		this.item = item;
		this.pingParam = pingParam;
	}

	
	public void run() {
		PingResult pingResult = null;
		while (pingParam.getRetryTimes() < item.getRetryTimes()) {
			pingParam.setRetryTimes(pingParam.getRetryTimes() + 1);

			String ip = pingParam.getBindAddress();
			if (ip == null || ip.equals("")) {
				ip = pingParam.getUri();
			}
			pingResult = PingUtil.doPingCmd(ip);

			logger.info(pingParam.getWorkUri() + ", pingResult.status:" + (pingResult == null ? null : pingResult.getStatus()) + ", retryTimes:" + pingParam.getRetryTimes());
			
			if (pingResult.getStatus() != PingResult.STATUS_OK) {
				pingParam.setFailTimes(pingParam.getFailTimes() + 1);
				pingParam.setLastErrorPingResult(pingResult);
			} else if (pingResult.getStatus() == PingResult.STATUS_OK) {
				if (pingParam.getServerStatus() == ServerStatus.DOWN) {
					pingParam.setSuccTimes(pingParam.getSuccTimes() + 1);
				}
			}

			ApplicaRuntime.sleep(Math.min(item.getRetryInterval(), item.getMonitorFreq()) * 1000); // 重试时间不大于监控频率
		}

		Monitor.getInstance().alarm(item, pingParam);
		logger.info(pingParam.getWorkUri() + ", pingParam.failTimes:" + pingParam.getFailTimes() + ", succTimes:" + pingParam.getSuccTimes() + " :: retryThread ended.");
	}

}
