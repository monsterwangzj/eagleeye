package com.chengyi.eagleeye.patrol.nginx;

import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.message.nginx.NginxMessage;
import com.chengyi.eagleeye.network.nginx.NginxParam;
import com.chengyi.eagleeye.network.nginx.NginxResult;
import com.chengyi.eagleeye.network.nginx.NginxUtil;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.NetUtil;
import com.chengyi.eagleeye.util.ServerStatus;

public class NginxThread implements Runnable {
	private static final Logger logger = Logger.getLogger(NginxThread.class);

	private static final int sleepInterval = 5000; // in ms
	
	private Item item;
	private NginxParam nginxParam;
	
	public NginxThread(Item item, NginxParam nginxParam) {
		this.item = item;
		this.nginxParam = nginxParam;
	}

	public void saveMessageSummary(NginxParam nginxParam, NginxResult nginxResult1, NginxResult nginxResult2) {
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
		
		message.setThroughputRate((float) (message.getTotalRequest() * 1.0 / (sleepInterval / 1000)));
		
		RedisUtil.pushMessage2PendingSet(message);
	}

	
	public void run() {
		Thread retryThread = null;
		while (true) {
			NginxResult nginxResult1 = NginxUtil.getNginxStatusPage(nginxParam.getUri());
			ApplicaRuntime.sleep(sleepInterval); // sleep for 5 seconds
			NginxResult nginxResult2 = NginxUtil.getNginxStatusPage(nginxParam.getUri());

			logger.info(nginxParam.getWorkUri() + ", nginxResult2.status:" + (nginxParam == null ? null : nginxResult2.getStatus()));
			saveMessageSummary(nginxParam, nginxResult1, nginxResult2);

			if (nginxResult2.getStatus() != NginxResult.STATUS_OK) {
				nginxParam.setFailTimes(nginxParam.getFailTimes() + 1);
				nginxParam.setLastErrorNginxResult(nginxResult2);
				logger.info(nginxParam.getWorkUri() + ", nginxParam.failTimes:" + nginxParam.getFailTimes() + ", succTimes:" + nginxParam.getSuccTimes());

				if (retryThread == null || !retryThread.isAlive()) {
					retryThread = new Thread(new RetryNginxThread(item, nginxParam));
					retryThread.start();
				}
			} else if (nginxResult2.getStatus() == NginxResult.STATUS_OK) {
				if (nginxParam.getServerStatus() == ServerStatus.DOWN) {
					nginxParam.setSuccTimes(nginxParam.getSuccTimes() + 1);
					logger.info(nginxParam.getWorkUri() + ", nginxParam.succTimes:" + nginxParam.getSuccTimes() + ", failTimes:" + nginxParam.getFailTimes());

					if (retryThread == null || !retryThread.isAlive()) {
						retryThread = new Thread(new RetryNginxThread(item, nginxParam));
						retryThread.start();
					}
				}
				nginxParam.add2Queue(1);
			}

			// 设置监控频率
			ApplicaRuntime.sleep(item.getMonitorFreq() * 1000);
		}

	}

}
