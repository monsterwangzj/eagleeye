package com.chengyi.eagleeye.model.message.http;

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
@Table(name = "httpmessagestat")
public class HttpMessageStat extends MessageStat {
	private static final long serialVersionUID = 6498368795754620003L;

	private long totalAccessCount;
	private long succAccessCount;
	private long succAccessCostTime;
	
	private long succDnsLookupTime;
	private long succConnectingTime;
	private long succWaitingTime;
	private long succReceivingTime;
	
	private long minResponseTime; // 最小响应时间
	private long maxResponseTime; // 最大响应时间
	
	private int class1Count; // response time in (0, 300]ms totalCount
	private int class2Count; // response time in (300, 600]ms totalCount
	private int class3Count; // response time in (600, 900]ms totalCount
	private int class4Count; // response time in (900, 1200]ms totalCount
	private int class5Count; // response time in (1200, 1500]ms totalCount
	private int class6Count; // response time in (1500, +infinity)ms totalCount
	
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

	@Column(name = "succaccesscount", nullable = false, length = 10)
	public long getSuccAccessCount() {
		return succAccessCount;
	}

	public void setSuccAccessCount(long succAccessCount) {
		this.succAccessCount = succAccessCount;
	}
	
	@Column(name = "succaccesscosttime", nullable = false, length = 10)
	public long getSuccAccessCostTime() {
		return succAccessCostTime;
	}

	public void setSuccAccessCostTime(long succAccessCostTime) {
		this.succAccessCostTime = succAccessCostTime;
	}
	
	@Column(name = "succdnslookuptime", nullable = false, length = 10)
	public long getSuccDnsLookupTime() {
		return succDnsLookupTime;
	}

	public void setSuccDnsLookupTime(long succDnsLookupTime) {
		this.succDnsLookupTime = succDnsLookupTime;
	}
	
	@Column(name = "succconnectingtime", nullable = false, length = 10)
	public long getSuccConnectingTime() {
		return succConnectingTime;
	}

	public void setSuccConnectingTime(long succConnectingTime) {
		this.succConnectingTime = succConnectingTime;
	}

	@Column(name = "succwaitingtime", nullable = false, length = 10)
	public long getSuccWaitingTime() {
		return succWaitingTime;
	}

	public void setSuccWaitingTime(long succWaitingTime) {
		this.succWaitingTime = succWaitingTime;
	}

	@Column(name = "succreceivingtime", nullable = false, length = 10)
	public long getSuccReceivingTime() {
		return succReceivingTime;
	}

	public void setSuccReceivingTime(long succReceivingTime) {
		this.succReceivingTime = succReceivingTime;
	}

	@Column(name = "minresponsetime", nullable = true, length = 10)
	public long getMinResponseTime() {
		return minResponseTime;
	}

	public void setMinResponseTime(long minResponseTime) {
		this.minResponseTime = minResponseTime;
	}

	@Column(name = "maxresponsetime", nullable = true, length = 10)
	public long getMaxResponseTime() {
		return maxResponseTime;
	}

	public void setMaxResponseTime(long maxResponseTime) {
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

	@Column(name = "class6count", nullable = true, length = 10)
	public int getClass6Count() {
		return class6Count;
	}

	public void setClass6Count(int class6Count) {
		this.class6Count = class6Count;
	}

	public static int getClassByResponseTime(long responseTime) {
		if (responseTime <= 300) {
			return 1;
		} else if (responseTime > 300 && responseTime <= 600) {
			return 2;
		} else if (responseTime > 600 && responseTime <= 900) {
			return 3;
		} else if (responseTime > 900 && responseTime <= 1200) {
			return 4;
		} else if (responseTime > 1200 && responseTime <= 1500) {
			return 5;
		} else {
			return 6;
		}
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
