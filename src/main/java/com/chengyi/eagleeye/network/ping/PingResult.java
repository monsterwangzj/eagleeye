package com.chengyi.eagleeye.network.ping;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.chengyi.eagleeye.network.BaseResult;

public class PingResult extends BaseResult implements Serializable {
	private static final long serialVersionUID = 4406531060774519912L;

	public static final int STATUS_OK = 0;
	public static final int STATUS_PARTLY_TIMEOUT = -3000;
	public static final int STATUS_TOTAL_TIMEOUT = -3001;
	public static final int STATUS_UNREACHABLE = -3002;

	// package loss percent 丢包率
	private float lossPercent;

	// 响应时间（最大、最小、平均）
	private float minimum;
	private float maximum;
	private float average;

	public PingResult() {
	}

	public PingResult(int status, String content) {
		setStatus(status);
		setResponseContent(content);
	}
	
	public int getErrorTypeByResult(BaseResult baseResult) {
		return super.getStatus();
	}

	public float getLossPercent() {
		return lossPercent;
	}

	public void setLossPercent(float lossPercent) {
		this.lossPercent = lossPercent;
	}

	public float getMinimum() {
		return minimum;
	}

	public void setMinimum(float minimum) {
		this.minimum = minimum;
	}

	public float getMaximum() {
		return maximum;
	}

	public void setMaximum(float maximum) {
		this.maximum = maximum;
	}

	public float getAverage() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public String getContentByErrorNo(int errorType) {
		String errorReason = "";
		switch (errorType) {
		case PingResult.STATUS_OK:
			errorReason = "Recover";
			break;
		case PingResult.STATUS_PARTLY_TIMEOUT:
			errorReason = "Dest Timeout";
			break;
		case PingResult.STATUS_TOTAL_TIMEOUT:
			errorReason = "Dest Timeout";
			break;
		case PingResult.STATUS_UNREACHABLE:
			errorReason = "Dest Unreachable";
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