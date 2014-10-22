package com.chengyi.eagleeye.model.message.nginx;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.model.message.Message;

@Entity
@Table(name = "nginxmessage")
public class NginxMessage extends Message {
	private static final long serialVersionUID = -3824750873333451394L;

	private int activeConn; // 活动连接数
	private int readingConn; // reading连接数
	private int writingConn; // writing连接数
	private int waitingConn; // waiting连接数

	private int totalConn; // 总连接数，取间隔两次差值
	private int totalHandshake; // 总握手次数，取间隔两次差值
	private int totalRequest; // 总请求数，取间隔两次差值

	private float throughputRate; // 吞吐率(requests/second)
	
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

	@Column(name = "activeconn", nullable = false, length = 10)
	public int getActiveConn() {
		return activeConn;
	}

	public void setActiveConn(int activeConn) {
		this.activeConn = activeConn;
	}

	@Column(name = "readingconn", nullable = false, length = 10)
	public int getReadingConn() {
		return readingConn;
	}

	public void setReadingConn(int readingConn) {
		this.readingConn = readingConn;
	}

	@Column(name = "writingconn", nullable = false, length = 10)
	public int getWritingConn() {
		return writingConn;
	}

	public void setWritingConn(int writingConn) {
		this.writingConn = writingConn;
	}

	@Column(name = "waitingconn", nullable = false, length = 10)
	public int getWaitingConn() {
		return waitingConn;
	}

	public void setWaitingConn(int waitingConn) {
		this.waitingConn = waitingConn;
	}

	@Column(name = "totalconn", nullable = false, length = 10)
	public int getTotalConn() {
		return totalConn;
	}

	public void setTotalConn(int totalConn) {
		this.totalConn = totalConn;
	}

	@Column(name = "totalhandshake", nullable = false, length = 10)
	public int getTotalHandshake() {
		return totalHandshake;
	}

	public void setTotalHandshake(int totalHandshake) {
		this.totalHandshake = totalHandshake;
	}

	@Column(name = "totalrequest", nullable = false, length = 10)
	public int getTotalRequest() {
		return totalRequest;
	}

	public void setTotalRequest(int totalRequest) {
		this.totalRequest = totalRequest;
	}

	@Column(name = "throughputrate", nullable = true, precision = 3)
	public float getThroughputRate() {
		return throughputRate;
	}

	public void setThroughputRate(float throughputRate) {
		this.throughputRate = throughputRate;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
