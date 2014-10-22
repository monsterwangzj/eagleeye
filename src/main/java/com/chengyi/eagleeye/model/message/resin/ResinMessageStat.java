package com.chengyi.eagleeye.model.message.resin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.model.message.MessageStat;

@Entity
@Table(name = "nginxmessagestat")
public class ResinMessageStat extends MessageStat {
	private static final long serialVersionUID = -7609096429769392194L;

	private float activeConn; // 平均活动连接数
	private float readingConn; // 平均reading连接数
	private float writingConn; // 平均writing连接数
	private float waitingConn; // 平均waiting连接数

	private int totalConn; // 总连接数
	private int totalHandshake; // 总握手次数
	private int totalRequest; // 总请求数

	private float minThroughputRate; // 最小吞吐率
	private float averageThroughputRate; // 平均吞吐率
	private float maxThroughputRate; // 最大吞吐率

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return super.getId();
	}

	public void setId(Long id) {
		super.setId(id);
	}

	@Column(name = "userid", nullable = false, length = 10)
	public Long getUserId() {
		return super.getUserId();
	}

	public void setUserId(Long userId) {
		super.setUserId(userId);
	}

	@Column(name = "itemid", nullable = false, length = 10)
	public Long getItemId() {
		return super.getItemId();
	}

	public void setItemId(Long itemId) {
		super.setItemId(itemId);
	}

	@Column(name = "serverip", nullable = false, length = 30)
	public String getServerIp() {
		return super.getServerIp();
	}

	public void setServerIp(String serverIp) {
		super.setServerIp(serverIp);
	}

	@Column(name = "type", nullable = false, length = 3)
	public int getType() {
		return super.getType();
	}

	public void setType(int type) {
		super.setType(type);
	}

	@Column(name = "workerip", nullable = true, length = 40)
	public String getWorkerIp() {
		return super.getWorkerIp();
	}

	public void setWorkerIp(String workerIp) {
		super.setWorkerIp(workerIp);
	}

	@Column(name = "timeunittype", nullable = true, length = 3)
	public byte getTimeUnitType() {
		return super.getTimeUnitType();
	}

	public void setTimeUnitType(byte timeUnitType) {
		super.setTimeUnitType(timeUnitType);
	}

	@Column(name = "createtime", nullable = true, length = 14)
	public long getCreateTime() {
		return super.getCreateTime();
	}

	public void setCreateTime(long createTime) {
		super.setCreateTime(createTime);
	}

	@Column(name = "activeconn", nullable = true, precision = 3)
	public float getActiveConn() {
		return activeConn;
	}

	public void setActiveConn(float activeConn) {
		this.activeConn = activeConn;
	}

	@Column(name = "readingconn", nullable = true, precision = 3)
	public float getReadingConn() {
		return readingConn;
	}

	public void setReadingConn(float readingConn) {
		this.readingConn = readingConn;
	}

	@Column(name = "writingconn", nullable = true, precision = 3)
	public float getWritingConn() {
		return writingConn;
	}

	public void setWritingConn(float writingConn) {
		this.writingConn = writingConn;
	}

	@Column(name = "waitingconn", nullable = true, precision = 3)
	public float getWaitingConn() {
		return waitingConn;
	}

	public void setWaitingConn(float waitingConn) {
		this.waitingConn = waitingConn;
	}

	@Column(name = "totalconn", nullable = true)
	public int getTotalConn() {
		return totalConn;
	}

	public void setTotalConn(int totalConn) {
		this.totalConn = totalConn;
	}

	@Column(name = "totalhandshake", nullable = true)
	public int getTotalHandshake() {
		return totalHandshake;
	}

	public void setTotalHandshake(int totalHandshake) {
		this.totalHandshake = totalHandshake;
	}

	@Column(name = "totalrequest", nullable = true)
	public int getTotalRequest() {
		return totalRequest;
	}

	public void setTotalRequest(int totalRequest) {
		this.totalRequest = totalRequest;
	}

	@Column(name = "minthroughputrate", nullable = true, precision = 3)
	public float getMinThroughputRate() {
		return minThroughputRate;
	}

	public void setMinThroughputRate(float minThroughputRate) {
		this.minThroughputRate = minThroughputRate;
	}

	@Column(name = "averagethroughputrate", nullable = true, precision = 3)
	public float getAverageThroughputRate() {
		return averageThroughputRate;
	}

	public void setAverageThroughputRate(float averageThroughputRate) {
		this.averageThroughputRate = averageThroughputRate;
	}

	@Column(name = "maxthroughputrate", nullable = true, precision = 3)
	public float getMaxThroughputRate() {
		return maxThroughputRate;
	}

	public void setMaxThroughputRate(float maxThroughputRate) {
		this.maxThroughputRate = maxThroughputRate;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
