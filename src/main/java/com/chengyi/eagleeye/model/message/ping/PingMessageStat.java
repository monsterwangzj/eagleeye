package com.chengyi.eagleeye.model.message.ping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.model.message.MessageStat;

/**
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "pingmessagestat")
public class PingMessageStat extends MessageStat {
	private static final long serialVersionUID = -4785503270607533435L;

	private long totalAccessCount;

	private float lostPercent;

	private float minResponseTime; // 最小响应时间
	private float averageResponseTime; // 平均响应时间
	private float maxResponseTime; // 最大响应时间

	private int class1Count; // response time in (0, 1]ms totalCount
	private int class2Count; // response time in (1, 10]ms totalCount
	private int class3Count; // response time in (10, 100]ms totalCount
	private int class4Count; // response time in (100, 1000]ms totalCount
	private int class5Count; // response time in (1000, +infinity)ms totalCount
	
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

	@Column(name = "totalaccesscount", nullable = true, length = 10)
	public long getTotalAccessCount() {
		return totalAccessCount;
	}

	public void setTotalAccessCount(long totalAccessCount) {
		this.totalAccessCount = totalAccessCount;
	}

	@Column(name = "losspercent", nullable = true, precision = 3)
	public float getLostPercent() {
		return lostPercent;
	}

	public void setLostPercent(float lostPercent) {
		this.lostPercent = lostPercent;
	}

	@Column(name = "minresponsetime", nullable = true, precision = 3)
	public float getMinResponseTime() {
		return minResponseTime;
	}

	public void setMinResponseTime(float minResponseTime) {
		this.minResponseTime = minResponseTime;
	}

	@Column(name = "averageresponsetime", nullable = true, precision = 3)
	public float getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(float averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	@Column(name = "maxresponsetime", nullable = true, precision = 3)
	public float getMaxResponseTime() {
		return maxResponseTime;
	}

	public void setMaxResponseTime(float maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}
	
	@Column(name = "class1count", nullable = true, length = 10)
	public int getClass1Count() {
		return class1Count;
	}

	public void setClass1Count(int class1Count) {
		this.class1Count = class1Count;
	}
	
	@Column(name = "class2count", nullable = true, length = 10)
	public int getClass2Count() {
		return class2Count;
	}

	public void setClass2Count(int class2Count) {
		this.class2Count = class2Count;
	}
	
	@Column(name = "class3count", nullable = true, length = 10)
	public int getClass3Count() {
		return class3Count;
	}

	public void setClass3Count(int class3Count) {
		this.class3Count = class3Count;
	}
	
	@Column(name = "class4count", nullable = true, length = 10)
	public int getClass4Count() {
		return class4Count;
	}

	public void setClass4Count(int class4Count) {
		this.class4Count = class4Count;
	}
	
	@Column(name = "class5count", nullable = true, length = 10)
	public int getClass5Count() {
		return class5Count;
	}

	public void setClass5Count(int class5Count) {
		this.class5Count = class5Count;
	}

	public static int getClassByResponseTime(double responseTime) {
		if (responseTime <= 1.0) {
			return 1;
		} else if (responseTime > 1.0 && responseTime <= 10.0) {
			return 2;
		} else if (responseTime > 10.0 && responseTime <= 100.0) {
			return 3;
		} else if (responseTime > 100.0 && responseTime <= 1000.0) {
			return 4;
		} else if (responseTime > 1000.0) {
			return 5;
		} else {
			return 5;
		}
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
