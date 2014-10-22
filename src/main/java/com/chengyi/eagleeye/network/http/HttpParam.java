package com.chengyi.eagleeye.network.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.assist.HttpOption;
import com.chengyi.eagleeye.network.BaseParam;
import com.chengyi.eagleeye.network.BaseResult;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.util.AlarmStatus;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.ServerStatus;

public class HttpParam extends BaseParam implements Serializable {
	public static Logger logger = Logger.getLogger(HttpParam.class);

	private static final long serialVersionUID = 2600768560218610195L;

	public static enum HttpRequestMethod {
		GET, POST, HEAD
	};

	private HttpRequestMethod method;
	private int timeout = HttpOption.ITEM_HTTPTIMEOUT_DEFAULT;
	private String encoding;

	private String resultMatchPattern;
	private byte resultMatchPatternStatus;
	private Map<String, String> postParams;

	private String jsonHeader;
	private String cookie;

	private BaseResult lastErrorResult;

	public ArrayList<Integer> queue = new ArrayList<Integer>(); // serverStatus取值队列
	public int alarmStatus = AlarmStatus.INSTABLE;

	public HttpParam() {
	}

	public HttpParam(Long itemId, String uri) {
		setItemId(itemId);
		setUri(uri);
	}

	public HttpParam(Long itemId, String uri, String bindAddress, int timeout, String resultMatchPattern, byte resultMatchPatternStatus) {
		setItemId(itemId);
		setUri(uri);
		setBindAddress(bindAddress);
		this.timeout = timeout;
		this.resultMatchPattern = resultMatchPattern;
		this.resultMatchPatternStatus = resultMatchPatternStatus;
	}

	public void add2Queue(int status) {
		if (queue.size() < 14) {
			queue.add(status);
		} else {
			queue.remove(0);
			queue.add(status);
		}
		logger.info(getUri() + ":" + getBindAddress() + ":" + queue);
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

	public HttpRequestMethod getMethod() {
		if (method == null)
			return HttpRequestMethod.GET;
		else
			return method;
	}

	public void setMethod(HttpRequestMethod method) {
		this.method = method;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Map<String, String> getPostParams() {
		return postParams;
	}

	public void setPostParams(Map<String, String> postParams) {
		this.postParams = postParams;
	}

	public String getJsonHeader() {
		return jsonHeader;
	}

	public void setJsonHeader(String jsonHeader) {
		this.jsonHeader = jsonHeader;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public BaseResult getLastErrorResult() {
		return lastErrorResult;
	}

	public void setLastErrorResult(HttpResult lastErrorResult) {
		this.lastErrorResult = lastErrorResult;
	}

	public String getWorkUri() {
		String workUri = getUri();
		String host = workUri.split("/")[2];
		if (getBindAddress() != null) {
			workUri = getUri().replaceFirst(host, getBindAddress());
		}
		return workUri;
	}

	public String getResultMatchPattern() {
		return resultMatchPattern;
	}

	public void setResultMatchPattern(String resultMatchPattern) {
		this.resultMatchPattern = resultMatchPattern;
	}

	public byte getResultMatchPatternStatus() {
		return resultMatchPatternStatus;
	}

	public void setResultMatchPatternStatus(byte resultMatchPatternStatus) {
		this.resultMatchPatternStatus = resultMatchPatternStatus;
	}

	public long getServerStatus() {
		String key = ApplicaRuntime.globalFlag + getItemId() + "-" + getBindAddress() + "-ServerStatus-" + ApplicaRuntime.globalFlag;
		Long serverStatus = RedisUtil.getLong(key);

		logger.info("key:" + key + ", serverStatus:" + serverStatus);

		if (serverStatus == null)
			return ServerStatus.UNKNOWN;
		else
			return serverStatus;
	}

	public void setServerStatus(long serverStatus) {
		String key = ApplicaRuntime.globalFlag + getItemId() + "-" + getBindAddress() + "-ServerStatus-" + ApplicaRuntime.globalFlag;
		RedisUtil.setLong(key, serverStatus);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
