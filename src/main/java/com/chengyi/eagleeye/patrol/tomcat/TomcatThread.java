package com.chengyi.eagleeye.patrol.tomcat;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.message.nginx.NginxMessage;
import com.chengyi.eagleeye.network.apache.ApacheParam;
import com.chengyi.eagleeye.network.apache.ApacheResult;
import com.chengyi.eagleeye.network.apache.ApacheUtil;
import com.chengyi.eagleeye.network.nginx.NginxParam;
import com.chengyi.eagleeye.network.nginx.NginxResult;
import com.chengyi.eagleeye.network.nginx.NginxUtil;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.NetUtil;
import com.chengyi.eagleeye.util.ServerStatus;

public class TomcatThread implements Runnable {
	private static final Logger logger = Logger.getLogger(TomcatThread.class);

	private static final int sleepInterval = 5000; // in ms
	
	private Item item;
	private ApacheParam apacheParam;
	
	public TomcatThread(Item item, ApacheParam apacheParam) {
		this.item = item;
		this.apacheParam = apacheParam;
	}

	public void saveMessageSummary(ApacheParam nginxParam, ApacheResult nginxResult1, ApacheResult nginxResult2) {
		NginxMessage message = new NginxMessage();

		message.setUserId(item.getUserId());
		message.setItemId(item.getId());
		message.setServerIp(nginxParam.getBindAddress() == null ? "" : nginxParam.getBindAddress());
		message.setType(Item.TYPE_NGINX);
		message.setStatus(nginxResult2.getStatus());
		message.setWorkerIp(NetUtil.getLocalIp());
		message.setCreateTime(System.currentTimeMillis());
		
		message.setActiveConn((nginxResult1.getActiveConn() + nginxResult2.getActiveConn()) / 2);
		message.setReadingConn((nginxResult1.getReadingConn() + nginxResult2.getReadingConn()) / 2);
		message.setWritingConn((nginxResult1.getWritingConn() + nginxResult2.getWritingConn()) / 2);
		message.setWaitingConn((nginxResult1.getWaitingConn() + nginxResult2.getWaitingConn()) / 2);
		
		message.setTotalConn(nginxResult2.getTotalConn() - nginxResult1.getTotalConn());
		message.setTotalHandshake(nginxResult2.getTotalHandshake() - nginxResult1.getTotalHandshake());
		message.setTotalRequest(nginxResult2.getTotalRequest() - nginxResult1.getTotalRequest());
		
		message.setThroughputRate((float)(message.getTotalRequest() * 1.0 / (sleepInterval / 1000)));
		
		RedisUtil.pushMessage2PendingSet(message);
		// TODO
	}

	
	public void run() {
		Thread retryThread = null;
		while (true) {
			ApacheResult nginxResult1 = ApacheUtil.getApacheStatusPage(apacheParam.getUri());
			ApplicaRuntime.sleep(sleepInterval); // sleep for 5 seconds
			ApacheResult nginxResult2 = ApacheUtil.getApacheStatusPage(apacheParam.getUri());

			logger.info(apacheParam.getWorkUri() + ", nginxResult2.status:" + (apacheParam == null ? null : nginxResult2.getStatus()));
			saveMessageSummary(apacheParam, nginxResult1, nginxResult2);

			if (nginxResult2.getStatus() != NginxResult.STATUS_OK) {
				apacheParam.setFailTimes(apacheParam.getFailTimes() + 1);
				apacheParam.setLastErrorNginxResult(nginxResult2);
				logger.info(apacheParam.getWorkUri() + ", apacheParam.failTimes:" + apacheParam.getFailTimes() + ", succTimes:" + apacheParam.getSuccTimes());

				if (retryThread == null || !retryThread.isAlive()) {
					retryThread = new Thread(new RetryTomcatThread(item, apacheParam));
					retryThread.start();
				}
			} else if (nginxResult2.getStatus() == NginxResult.STATUS_OK) {
				if (apacheParam.getServerStatus() == ServerStatus.DOWN) {
					apacheParam.setSuccTimes(apacheParam.getSuccTimes() + 1);
					logger.info(apacheParam.getWorkUri() + ", apacheParam.succTimes:" + apacheParam.getSuccTimes() + ", failTimes:" + apacheParam.getFailTimes());

					if (retryThread == null || !retryThread.isAlive()) {
						retryThread = new Thread(new RetryTomcatThread(item, apacheParam));
						retryThread.start();
					}
				}
				apacheParam.add2Queue(1);
			}

			// 设置监控频率
			ApplicaRuntime.sleep(item.getMonitorFreq() * 1000);
		}

	}

}
