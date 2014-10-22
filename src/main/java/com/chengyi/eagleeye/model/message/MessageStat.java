package com.chengyi.eagleeye.model.message;

import java.io.Serializable;

public class MessageStat implements Serializable {
	private static final long serialVersionUID = -652287644088465926L;

	public static final byte TIMEUNITTYPE_MINUTE = (byte) 0;
	public static final byte TIMEUNITTYPE_HOUR = (byte) 1;

	private Long id;

	private Long userId;

	private Long itemId;

	private String serverIp;

	private int type;

	private String workerIp;

	private byte timeUnitType = (byte) 0; // 1 timeUnit or 1 hour

	private long createTime; // like 2013083010, by hour

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public byte getTimeUnitType() {
		return timeUnitType;
	}

	public void setTimeUnitType(byte timeUnitType) {
		this.timeUnitType = timeUnitType;
	}

	public String getWorkerIp() {
		return workerIp;
	}

	public void setWorkerIp(String workerIp) {
		this.workerIp = workerIp;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
