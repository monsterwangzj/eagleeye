package com.chengyi.eagleeye.model.assist;

import java.io.Serializable;

public class HttpOption implements Serializable {

	private static final long serialVersionUID = -5727963037415706939L;

	public static final int ITEM_HTTPTIMEOUT_DEFAULT = 5000;

	// http request method: get, post, head
	private String httpMethod;

	// http request timeout in ms, 5000ms by default
	private int httpTimeout = ITEM_HTTPTIMEOUT_DEFAULT;

	private String resultMatchPattern;

	private byte resultMatchPatternStatus;

	// eg：token=d906b69209d9de92789fcd65a1a5d210; pvid=954970634; flv=10.0
	private String cookies;

	// 自定义HTTP请求头信息，格式为：Name: Value，多个项用换行分隔，比如：User-Agent: Mozilla/4.0
	private String httpHeader;

	private String username;

	private String password;

	private String serverIps;

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public int getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(int httpTimeout) {
		this.httpTimeout = httpTimeout;
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

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public String getHttpHeader() {
		return httpHeader;
	}

	public void setHttpHeader(String httpHeader) {
		this.httpHeader = httpHeader;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerIps() {
		return serverIps;
	}

	public void setServerIps(String serverIps) {
		this.serverIps = serverIps;
	}

}
