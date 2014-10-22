package com.chengyi.eagleeye.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

/**
 * 监控人分组于监控人关联表
 * 
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "monitorgroupmonitor")
public class MonitorGroupMonitor implements Serializable {
	private static final long serialVersionUID = -4922890220819641427L;

	private Long id;

	private Long monitorGroupId;

	private Long monitorId;
	
	private byte alarmChannel = (byte) 0; // 1 Email, 2 SMS, 4 YOUNI
	
	private long priority = 0;

	private long createTime;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "monitorgroupid", nullable = false, length = 10)
	public Long getMonitorGroupId() {
		return monitorGroupId;
	}

	public void setMonitorGroupId(Long monitorGroupId) {
		this.monitorGroupId = monitorGroupId;
	}

	@Column(name = "monitorid", nullable = false, length = 10)
	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}
	
	@Column(name = "alarmChannel", nullable = false, length = 3)
	public byte getAlarmChannel() {
		return alarmChannel;
	}

	public void setAlarmChannel(byte alarmChannel) {
		this.alarmChannel = alarmChannel;
	}

	@Column(name = "priority", nullable = false, length = 10)
	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	@Column(name = "createtime", nullable = false, length = 14)
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
