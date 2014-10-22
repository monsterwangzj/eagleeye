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
 * 项目故障跟踪模型
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "breakdownhistory")
public class BreakDownHistory implements Serializable {
	private static final long serialVersionUID = -6718277224912746699L;

	private Long id;
	private Long userId; // 创建监控项目的用户id
	private Long itemId;
	private String serverIp;
	
	private long startTime;
	private long endTime;
	private int errorType; // 故障类型
	private boolean isSendAlarm; // 是否已经发送报警消息
	private String reason; // response content
	
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

	@Column(name = "starttime", nullable = true, length = 14)
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	@Column(name = "endtime", nullable = true, length = 14)
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	@Column(name = "errortype", nullable = true, length = 10)
	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	@Column(name = "issendalarm", nullable = true, length = 2)
	public boolean isSendAlarm() {
		return isSendAlarm;
	}

	public void setSendAlarm(boolean isSendAlarm) {
		this.isSendAlarm = isSendAlarm;
	}

	@Column(name = "reason", nullable = true, length = 512)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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
