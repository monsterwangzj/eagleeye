package com.chengyi.eagleeye.patrol.nginx;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.network.nginx.NginxParam;
import com.chengyi.eagleeye.network.nginx.NginxResult;
import com.chengyi.eagleeye.network.nginx.NginxUtil;
import com.chengyi.eagleeye.patrol.Monitor;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public class RetryNginxThread implements Runnable {
	private static final Logger logger = Logger.getLogger(RetryNginxThread.class);

	private NginxParam nginxParam;
	private Item item;

	public RetryNginxThread(Item item, NginxParam nginxParam) {
		logger.info("new retryPingThread for item:" + item.getId() + ", url:" + nginxParam.getUri());

		nginxParam.setRetryTimes(0);
		this.item = item;
		this.nginxParam = nginxParam;
	}

	
	public void run() {
		NginxResult nginxResult = null;
		while (nginxParam.getRetryTimes() < item.getRetryTimes()) {
			nginxParam.setRetryTimes(nginxParam.getRetryTimes() + 1);

			nginxResult = NginxUtil.getNginxStatusPage(nginxParam.getUri());
			logger.info(nginxParam.getWorkUri() + ", nginxResult.status:" + (nginxResult == null ? null : nginxResult.getStatus()) + ", retryTimes:" + nginxParam.getRetryTimes());

			if (nginxResult.getStatus() != NginxResult.STATUS_OK) {
				nginxParam.setFailTimes(nginxParam.getFailTimes() + 1);
				nginxParam.setLastErrorNginxResult(nginxResult);

			} else if (nginxResult.getStatus() == NginxResult.STATUS_OK) {
				if (nginxParam.getServerStatus() == ServerStatus.DOWN) {
					nginxParam.setSuccTimes(nginxParam.getSuccTimes() + 1);
				}
			}

			ApplicaRuntime.sleep(Math.min(item.getRetryInterval(), item.getMonitorFreq()) * 1000); // 重试时间不大于监控频率
		}

		Monitor.getInstance().alarm(item, nginxParam);
		logger.info(nginxParam.getWorkUri() + ", nginxParam.failTimes:" + nginxParam.getFailTimes() + ", succTimes:" + nginxParam.getSuccTimes() + " :: retryThread ended.");
	}

}
