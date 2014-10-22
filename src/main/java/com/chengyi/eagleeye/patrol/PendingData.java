package com.chengyi.eagleeye.patrol;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.message.Message;
import com.chengyi.eagleeye.model.message.http.HttpMessage;
import com.chengyi.eagleeye.model.message.nginx.NginxMessage;
import com.chengyi.eagleeye.model.message.ping.PingMessage;
import com.chengyi.eagleeye.model.message.redis.RedisMessage;
import com.chengyi.eagleeye.service.MessageMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.DateUtil;
import com.chengyi.eagleeye.util.NetUtil;

public class PendingData {
	private static transient Logger logger = Logger.getLogger(PendingData.class);

	@Autowired
	public MessageMgr messageMgr;
	
//	@Autowired
//	public ItemMgr itemMgr;

	private int interval = 2 * 60 * 1000;

	public static boolean isStop = false;

	private PendingData() {
	}

	public void execute() {
		String localIp = NetUtil.getLocalIp();
//		if (localIp != null && (localIp.contains("132.231") || localIp.contains("32.119") || localIp.contains("32.67"))) {
		if (localIp != null && (localIp.contains("32.119") || localIp.contains("32.67"))) {
			// save message to hdfs
			new Thread() {
				public void run() {
					while (true) {
						final SimpleDateFormat mFormatDateTime = new SimpleDateFormat("mm");
						String datestr = DateUtil.format(new Date(), mFormatDateTime);

						if (Integer.parseInt(datestr) % 2 == 0) { // every 2mins
							popMessage();
							ApplicaRuntime.sleep(interval);
						} else {
							ApplicaRuntime.sleep(10000);
						}
					}
				}
			}.start();
		}
	}

	protected void popMessage() {
		if (isStop) {
			return;
		}
		int totalCount = 0;

		while (true) {
			try {
				Message message = RedisUtil.popMessageFromPendingSet();
				if (message == null) {
					break;
				} else {
					totalCount++;

					if (message.getType() == Item.TYPE_HTTP) {
//						Item item = itemMgr.getItem(message.getItemId());
//						if (item.getMonitorFreq() >= 60) { // save as httpMessageStat
//							HttpMessageStat hmStat = HttpHelper.getHttpMessageStat((HttpMessage) message, item.getMonitorFreq());
//							messageMgr.saveHttpMessageStat(hmStat);
//						} else {
						 	
							if (StringUtils.isNotEmpty(message.getServerIp())) {
								message.setServerIp("");
								messageMgr.saveHttpMessage((HttpMessage) message);
							}
							messageMgr.saveHttpMessage((HttpMessage) message);
//						}
						
					} else if (message.getType() == Item.TYPE_PING) {
						if (StringUtils.isNotEmpty(message.getServerIp())) {
							message.setServerIp("");
							messageMgr.savePingMessage((PingMessage) message);
						}
						messageMgr.savePingMessage((PingMessage) message);
					} else if (message.getType() == Item.TYPE_NGINX) {
						if (StringUtils.isNotEmpty(message.getServerIp())) {
							message.setServerIp("");
							messageMgr.saveNginxMessage((NginxMessage) message);
						}
						messageMgr.saveNginxMessage((NginxMessage) message);
					} else if (message.getType() == Item.TYPE_REDIS) {
						if (StringUtils.isNotEmpty(message.getServerIp())) {
							message.setServerIp("");
							messageMgr.saveRedisMessage((RedisMessage) message);
						}
						messageMgr.saveRedisMessage((RedisMessage) message);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}

		}

	}

}
