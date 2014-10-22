package com.chengyi.eagleeye.network.http;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.chengyi.eagleeye.network.BaseResult;

public class HttpResult extends BaseResult implements Serializable {
	private static final long serialVersionUID = 5299344644830106389L;

	public static final int STATUS_DNS_ERROR = -100;
	public static final int STATUS_CONNECTION_ERROR = -200;
	public static final int STATUS_PARSEHEADER_ERROR = -300;
	public static final int STATUS_RECEIVE_ERROR = -400;

	public static final int STATUS_OK = 0;
	public static final int STATUS_NETWORKINTERRUPT = -1001;
	public static final int STATUS_HTTPABORTED = -1002;
	public static final int STATUS_CODE_ABNORMAL = -500;
	public static final int STATUS_CONTENT_NOTMATCH = -2000;
	
	private boolean isMatch = true;

	private long dnsLookupCost;
	private long connectionCreationCost;
	private long waitingCost;
	private long receivingCost;
	
	private long costTime;

	public HttpResult() {
	}

	public HttpResult(int status, String content) {
		setStatus(status);
		setResponseContent(content);
	}

	public HttpResult(int status, String content, boolean isMatch) {
		setStatus(status);
		setResponseContent(content);
		setMatch(isMatch);
	}

	public int getErrorTypeByResult(BaseResult baseResult) {
		HttpResult httpResult = (HttpResult) baseResult;
		boolean isMatch = httpResult.isMatch();
		int status = httpResult.getStatus();
		int errorType = 0;
		if (!isMatch) { // 结果不匹配
			errorType = HttpResult.STATUS_CONTENT_NOTMATCH;
		} else {
			errorType = status;
		}
		return errorType;
	}
	
	public long getDnsLookupCost() {
		return dnsLookupCost;
	}

	public void setDnsLookupCost(long dnsLookupCost) {
		this.dnsLookupCost = dnsLookupCost;
	}

	public long getConnectionCreationCost() {
		return connectionCreationCost;
	}

	public void setConnectionCreationCost(long connectionCreationCost) {
		this.connectionCreationCost = connectionCreationCost;
	}

	public long getWaitingCost() {
		return waitingCost;
	}

	public void setWaitingCost(long waitingCost) {
		this.waitingCost = waitingCost;
	}

	public long getReceivingCost() {
		return receivingCost;
	}

	public void setReceivingCost(long receivingCost) {
		this.receivingCost = receivingCost;
	}

	public long getCostTime() {
		return costTime;
	}

	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public String getContentByErrorNo(int errorType) {
		String errorReason = ""; // TODO
		
		switch (errorType) {
		case HttpResult.STATUS_OK:
			errorReason = "Recover";
			break;
		case HttpResult.STATUS_NETWORKINTERRUPT:
			errorReason = "Network Interrupt";
			break;
		case HttpResult.STATUS_HTTPABORTED:
			errorReason = "Http Aborted";
			break;
		case HttpResult.STATUS_CODE_ABNORMAL:
			errorReason = "Status Incorrect";
			break;
		case HttpResult.STATUS_CONTENT_NOTMATCH:
			errorReason = "Content Mismatch";
		}
		
		if (errorType > 0) {
			errorReason = "Timeout in " + errorType + " ms";
		}
		return errorReason;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}