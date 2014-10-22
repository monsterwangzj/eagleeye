package com.chengyi.eagleeye.patrol.apache;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.network.apache.ApacheParam;
import com.chengyi.eagleeye.network.apache.ApacheResult;
import com.chengyi.eagleeye.network.apache.ApacheUtil;
import com.chengyi.eagleeye.network.nginx.NginxResult;
import com.chengyi.eagleeye.patrol.Monitor;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public class RetryApacheThread implements Runnable {
	private static final Logger logger = Logger.getLogger(RetryApacheThread.class);

	private ApacheParam apacheParam;
	private Item item;

	public RetryApacheThread(Item item, ApacheParam apacheParam) {
		logger.info("new retryApacheThread for item:" + item.getId() + ", url:" + apacheParam.getUri());

		apacheParam.setRetryTimes(0);
		this.item = item;
		this.apacheParam = apacheParam;
	}

	
	public void run() {
		ApacheResult nginxResult = null;
		while (apacheParam.getRetryTimes() < item.getRetryTimes()) {
			apacheParam.setRetryTimes(apacheParam.getRetryTimes() + 1);

			nginxResult = ApacheUtil.getApacheStatusPage(apacheParam.getBindAddress());

			logger.info(apacheParam.getWorkUri() + ", nginxResult.status:" + (nginxResult == null ? null : nginxResult.getStatus()) + ", retryTimes:" + apacheParam.getRetryTimes());

			if (nginxResult.getStatus() != NginxResult.STATUS_OK) {
				apacheParam.setFailTimes(apacheParam.getFailTimes() + 1);
				apacheParam.setLastErrorNginxResult(nginxResult);

			} else if (nginxResult.getStatus() == NginxResult.STATUS_OK) {
				if (apacheParam.getServerStatus() == ServerStatus.DOWN) {
					apacheParam.setSuccTimes(apacheParam.getSuccTimes() + 1);
				}
			}

			ApplicaRuntime.sleep(Math.min(item.getRetryInterval(), item.getMonitorFreq()) * 1000); // 重试时间不大于监控频率
		}

		Monitor.getInstance().alarm(item, apacheParam);
		logger.info(apacheParam.getWorkUri() + ", apacheParam.failTimes:" + apacheParam.getFailTimes() + ", succTimes:" + apacheParam.getSuccTimes() + " :: retryThread ended.");
	}

}
