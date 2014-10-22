package com.chengyi.eagleeye.network.tomcat;

import java.io.Serializable;

import com.chengyi.eagleeye.network.BaseResult;

public class TomcatResult extends BaseResult implements Serializable {
	private static final long serialVersionUID = -8358346676801094407L;

	public static final int STATUS_OK = 0;
	public static final int STATUS_FAIL = -1001;

	private int activeConn; // 活动连接数
	private int readingConn; // reading连接数
	private int writingConn; // writing连接数
	private int waitingConn; // waiting连接数

	private int totalConn; // 总连接数，取间隔两次差值
	private int totalHandshake; // 总握手次数，取间隔两次差值
	private int totalRequest; // 总请求数，取间隔两次差值

	public int getActiveConn() {
		return activeConn;
	}

	public void setActiveConn(int activeConn) {
		this.activeConn = activeConn;
	}

	public int getReadingConn() {
		return readingConn;
	}

	public void setReadingConn(int readingConn) {
		this.readingConn = readingConn;
	}

	public int getWritingConn() {
		return writingConn;
	}

	public void setWritingConn(int writingConn) {
		this.writingConn = writingConn;
	}

	public int getWaitingConn() {
		return waitingConn;
	}

	public void setWaitingConn(int waitingConn) {
		this.waitingConn = waitingConn;
	}

	public int getTotalConn() {
		return totalConn;
	}

	public void setTotalConn(int totalConn) {
		this.totalConn = totalConn;
	}

	public int getTotalHandshake() {
		return totalHandshake;
	}

	public void setTotalHandshake(int totalHandshake) {
		this.totalHandshake = totalHandshake;
	}

	public int getTotalRequest() {
		return totalRequest;
	}

	public void setTotalRequest(int totalRequest) {
		this.totalRequest = totalRequest;
	}

	
	public int getErrorTypeByResult(BaseResult baseResult) {

		return 0;
	}

	
	public String getContentByErrorNo(int errorType) {

		return null;
	}

}
