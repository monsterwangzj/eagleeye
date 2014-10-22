package com.chengyi.eagleeye.network;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class BaseResult implements Serializable {
	private static final long serialVersionUID = -1558528233426903186L;

	private int status;

	private String responseContent;
	
	public abstract int getErrorTypeByResult(BaseResult baseResult);

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

	public abstract String getContentByErrorNo(int errorType);

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
