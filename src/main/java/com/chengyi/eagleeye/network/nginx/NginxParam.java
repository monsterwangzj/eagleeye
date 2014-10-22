package com.chengyi.eagleeye.network.nginx;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.network.BaseParam;

public class NginxParam extends BaseParam implements Serializable {
	private static final long serialVersionUID = 2600768560218610195L;

	public static Logger logger = Logger.getLogger(NginxParam.class);

	private NginxResult lastErrorNginxResult;

	public NginxParam() {
	}

	public NginxParam(Long itemId, String uri) {
		setItemId(itemId);
		setUri(uri);
	}

	public NginxParam(Long itemId, String uri, String bindAddress) {
		setItemId(itemId);
		setUri(uri);
		setBindAddress(bindAddress);
	}

	public void add2Queue(int status) {
		if (queue.size() < 14) {
			queue.add(status);
		} else {
			queue.remove(0);
			queue.add(status);
		}
		logger.info(getUri() + ":" + super.getBindAddress() + ":" + queue);
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

	public String getWorkUri() {
		return super.uri;
	}

	public NginxResult getLastErrorResult() {
		return lastErrorNginxResult;
	}

	public void setLastErrorNginxResult(NginxResult lastErrorNginxResult) {
		this.lastErrorNginxResult = lastErrorNginxResult;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
