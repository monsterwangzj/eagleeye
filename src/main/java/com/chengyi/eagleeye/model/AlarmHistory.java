/**
 * 
 */
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
 * 报警通知模型
 * 
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "alarmhistory")
public class AlarmHistory implements Serializable {
	private static final long serialVersionUID = -6718277224912746699L;

	public static final byte ERRORTYPE_TIMEOUT = -100;
	
	private Long id;
	private Long userId; // 创建监控项目的用户id
	private Long itemId;
	private String serverIp;
	private Long breakDownId;
	
	private byte alarmChannel;
	private int errorType; // 故障类型
	private Long monitorId; // 故障处理人id
	private String monitorAddr; // 故障处理人地址
	private String content; // 报警文本
	private long eventSeqId; // 事件流水id
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

	@Column(name = "userid", nullable = false, length = 10)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "itemid", nullable = false, length = 10)
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Column(name = "serverip", nullable = true, length = 32)
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
 
	@Column(name = "breakdownid", nullable = true, length = 10)
	public Long getBreakDownId() {
		return breakDownId;
	}

	public void setBreakDownId(Long breakDownId) {
		this.breakDownId = breakDownId;
	}

	@Column(name = "alarmchannel", nullable = false, length = 3)
	public byte getAlarmChannel() {
		return alarmChannel;
	}

	public void setAlarmChannel(byte alarmChannel) {
		this.alarmChannel = alarmChannel;
	}
	
	@Column(name = "errortype", nullable = true, length = 10)
	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	@Column(name = "monitorid", nullable = false, length = 10)
	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}
	
	@Column(name = "monitoraddr", nullable = false, length = 100)
	public String getMonitorAddr() {
		return monitorAddr;
	}

	public void setMonitorAddr(String monitorAddr) {
		this.monitorAddr = monitorAddr;
	}

	@Column(name = "content", nullable = false, length = 65535)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(name = "eventseqid", nullable = true, length = 10)
	public long getEventSeqId() {
		return eventSeqId;
	}

	public void setEventSeqId(long eventSeqId) {
		this.eventSeqId = eventSeqId;
	}

	@Column(name = "createtime", nullable = true, length = 14)
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
