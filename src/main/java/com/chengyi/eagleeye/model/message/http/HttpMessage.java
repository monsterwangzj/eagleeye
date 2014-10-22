package com.chengyi.eagleeye.model.message.http;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.model.message.Message;

/**
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "httpmessage")
public class HttpMessage extends Message {
	private static final long serialVersionUID = -797665120558788829L;

	private long dnsLookupTime;

	private long connectingTime;

	private long waitingTime;

	private long receivingTime;

	private long totalTime;

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

	@Column(name = "type", nullable = false, length = 10)
	public int getType() {
		return super.getType();
	}

	public void setType(int type) {
		super.setType(type);
	}

	@Column(name = "status", nullable = true, length = 10)
	public int getStatus() {
		return super.getStatus();
	}

	public void setStatus(int status) {
		super.setStatus(status);
	}

	@Column(name = "workerip", nullable = false, length = 30)
	public String getWorkerIp() {
		return super.getWorkerIp();
	}

	public void setWorkerIp(String workerIp) {
		super.setWorkerIp(workerIp);
	}

	@Column(name = "createtime", nullable = true, length = 14)
	public long getCreateTime() {
		return super.getCreateTime();
	}

	public void setCreateTime(long createTime) {
		super.setCreateTime(createTime);
	}
	
	@Column(name = "dnslookuptime", nullable = true, length = 10)
	public long getDnsLookupTime() {
		return dnsLookupTime;
	}

	public void setDnsLookupTime(long dnsLookupTime) {
		this.dnsLookupTime = dnsLookupTime;
	}

	@Column(name = "connectingtime", nullable = true, length = 10)
	public long getConnectingTime() {
		return connectingTime;
	}

	public void setConnectingTime(long connectingTime) {
		this.connectingTime = connectingTime;
	}

	@Column(name = "waitingtime", nullable = true, length = 10)
	public long getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}

	@Column(name = "receivingtime", nullable = true, length = 10)
	public long getReceivingTime() {
		return receivingTime;
	}

	public void setReceivingTime(long receivingTime) {
		this.receivingTime = receivingTime;
	}

	@Column(name = "totaltime", nullable = true, length = 10)
	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
