package com.chengyi.eagleeye.model.message.tomcat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.model.message.Message;

public class TomcatMessage extends Message {
	private static final long serialVersionUID = -6526140926893671440L;

	
	
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
	
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
