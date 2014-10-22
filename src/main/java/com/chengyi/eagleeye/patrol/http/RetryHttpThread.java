package com.chengyi.eagleeye.patrol.http;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.network.http.HttpClientUtil;
import com.chengyi.eagleeye.network.http.HttpParam;
import com.chengyi.eagleeye.network.http.HttpResult;
import com.chengyi.eagleeye.patrol.Monitor;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public class RetryHttpThread implements Runnable {
	private static final Logger logger = Logger.getLogger(RetryHttpThread.class);

	private HttpParam httpParam;
	private Item item;
	
	public RetryHttpThread(Item item, HttpParam httpParam) {
		logger.info("new retryThread for item:" + item.getId() + ", url:" + httpParam.getUri());

		httpParam.setRetryTimes(0);
		this.item = item;
		this.httpParam = httpParam;
	}

	
	public void run() {
		HttpResult httpResult = null;
		while (httpParam.getRetryTimes() < item.getRetryTimes()) {
			httpParam.setRetryTimes(httpParam.getRetryTimes() + 1);

			httpResult = HttpClientUtil.getHttpResult(httpParam);
			boolean isMatch = httpResult.isMatch();
			logger.info(httpParam.getWorkUri() + ", httpResult.status:" + (httpResult == null ? null : httpResult.getStatus()) + ", isMatch:" + isMatch + ", retryTimes:" + httpParam.getRetryTimes());
			
			if (httpResult.getStatus() != HttpResult.STATUS_OK || !isMatch) {
				httpParam.setFailTimes(httpParam.getFailTimes() + 1);
				httpParam.setLastErrorResult(httpResult);
			} else if (httpResult.getStatus() == HttpResult.STATUS_OK && isMatch) {
				if (httpParam.getServerStatus() == ServerStatus.DOWN) {
					httpParam.setSuccTimes(httpParam.getSuccTimes() + 1);
				}
			}

			ApplicaRuntime.sleep(Math.min(item.getRetryInterval(), item.getMonitorFreq()) * 1000); // 重试时间不大于监控频率
		}

		Monitor.getInstance().alarm(item, httpParam);
		logger.info(httpParam.getWorkUri() + ", httpParam.failTimes:" + httpParam.getFailTimes() + ", succTimes:" + httpParam.getSuccTimes() + " :: retryThread ended.");
	}

}
