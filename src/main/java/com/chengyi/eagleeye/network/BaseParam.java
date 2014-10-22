package com.chengyi.eagleeye.network;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.util.AlarmStatus;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public abstract class BaseParam implements Serializable {
	public static Logger logger = Logger.getLogger(BaseParam.class);

	private static final long serialVersionUID = -3340030188037631986L;

	private Long itemId;
	protected String uri;
	private String bindAddress;

	private int retryTimes = 0;

	private int failTimes = 0;
	private int succTimes = 0;

	public ArrayList<Integer> queue = new ArrayList<Integer>(); // serverStatus取值队列
	public int alarmStatus = AlarmStatus.INSTABLE;

	public abstract BaseResult getLastErrorResult();

	public void add2Queue(int status) {
		if (queue.size() < 14) {
			queue.add(status);
		} else {
			queue.remove(0);
			queue.add(status);
		}
		logger.info(uri + ":" + bindAddress + ":" + queue);
	}

	public int sumQueueChangeCount() {
		int sum = 0;
		for (int i = 1; i < queue.size(); i++) {
			if (!queue.get(i).equals(queue.get(i - 1))) {
				sum++;
			}
		}
		return sum;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getFailTimes() {
		return failTimes;
	}

	public void setFailTimes(int failTimes) {
		this.failTimes = failTimes;
	}

	public int getSuccTimes() {
		return succTimes;
	}

	public void setSuccTimes(int succTimes) {
		this.succTimes = succTimes;
	}

	public String getWorkUri() {
		String workUri = uri;
		String host = uri.split("/")[2];
		if (bindAddress != null) {
			workUri = uri.replaceFirst(host, bindAddress);
		}
		return workUri;
	}

	public long getServerStatus() {
		String key = ApplicaRuntime.globalFlag + itemId + "-" + bindAddress + "-ServerStatus-" + ApplicaRuntime.globalFlag;
		Long serverStatus = RedisUtil.getLong(key);

		logger.info("key:" + key + ", serverStatus:" + serverStatus);

		if (serverStatus == null)
			return ServerStatus.UNKNOWN;
		else
			return serverStatus;
	}

	public void setServerStatus(long serverStatus) {
		String key = ApplicaRuntime.globalFlag + itemId + "-" + bindAddress + "-ServerStatus-" + ApplicaRuntime.globalFlag;
		RedisUtil.setLong(key, serverStatus);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
