package com.chengyi.eagleeye.patrol.http;

import java.util.Date;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.message.http.HttpMessage;
import com.chengyi.eagleeye.network.http.HttpClientUtil;
import com.chengyi.eagleeye.network.http.HttpParam;
import com.chengyi.eagleeye.network.http.HttpResult;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.DateUtil;
import com.chengyi.eagleeye.util.NetUtil;
import com.chengyi.eagleeye.util.ServerStatus;

public class HttpThread implements Runnable {
	private static final Logger logger = Logger.getLogger(HttpThread.class);

	private Item item;
	private HttpParam httpParam;

	public HttpThread(Item item, HttpParam httpParam) {
		this.item = item;
		this.httpParam = httpParam;
	}

	public void saveMessageSummary(HttpParam httpParam, HttpResult httpResult) {
		HttpMessage message = new HttpMessage();
		message.setType(Item.TYPE_HTTP);
		
		message.setUserId(item.getUserId());
		message.setItemId(item.getId());
		message.setServerIp(httpParam.getBindAddress() == null ? "" : httpParam.getBindAddress());
		
		message.setDnsLookupTime(httpResult.getDnsLookupCost());
		message.setConnectingTime(httpResult.getConnectionCreationCost());
		message.setWaitingTime(httpResult.getWaitingCost());
		message.setReceivingTime(httpResult.getReceivingCost());
		message.setTotalTime(httpResult.getCostTime());
		
		message.setStatus(httpResult.getStatus());
		message.setWorkerIp(NetUtil.getLocalIp());
		message.setCreateTime(System.currentTimeMillis());

		RedisUtil.pushMessage2PendingSet(message);

		int status = message.getStatus();
		Long itemId = message.getItemId();
		long costTime = httpResult.getCostTime();
		String datestr = DateUtil.format8chars(new Date());
		RedisUtil.incr(ApplicaRuntime.globalFlag + itemId + "_" + datestr + "_totalAccessCount"); // 总访问次数
		if (status == HttpResult.STATUS_OK) {
			RedisUtil.incr(ApplicaRuntime.globalFlag + itemId + "_" + datestr + "_succAccessCount"); // 成功访问次数
			RedisUtil.incr(ApplicaRuntime.globalFlag + itemId + "_" + datestr + "_succAccessCostTime", costTime); // 成功访问消耗的总时间
		}
	}

	
	public void run() {
		Thread retryThread = null;
		while (true) {
			HttpResult httpResult = HttpClientUtil.getHttpResult(httpParam);
			boolean isMatch = httpResult.isMatch();
			logger.info(httpParam.getWorkUri() + ", httpResult.status:" + (httpResult == null ? null : httpResult.getStatus()) + ", isMatch:" + isMatch);

			saveMessageSummary(httpParam, httpResult);

			if (httpResult.getStatus() != HttpResult.STATUS_OK || !isMatch) {
				httpParam.setFailTimes(httpParam.getFailTimes() + 1);
				httpParam.setLastErrorResult(httpResult);
				logger.info(httpParam.getWorkUri() + ", httpParam.failTimes:" + httpParam.getFailTimes() + ", succTimes:" + httpParam.getSuccTimes());

				if (retryThread == null || !retryThread.isAlive()) {
					retryThread = new Thread(new RetryHttpThread(item, httpParam));
					retryThread.start();
				}
			} else if (httpResult.getStatus() == HttpResult.STATUS_OK && isMatch) {
				if (httpParam.getServerStatus() == ServerStatus.DOWN) {
					httpParam.setSuccTimes(httpParam.getSuccTimes() + 1);
					logger.info(httpParam.getWorkUri() + ", httpParam.succTimes:" + httpParam.getSuccTimes() + ", failTimes:" + httpParam.getFailTimes());

					if (retryThread == null || !retryThread.isAlive()) {
						retryThread = new Thread(new RetryHttpThread(item, httpParam));
						retryThread.start();
					}
				}
				httpParam.add2Queue(1);
			}

			// 设置监控频率
			ApplicaRuntime.sleep(item.getMonitorFreq() * 1000);
		}

	}

}
