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
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "itemalarm")
public class ItemMonitor implements Serializable {
	private static final long serialVersionUID = -5435390072244378162L;

	private Long id;

	private Long itemId;

	private Long monitorId;

	private byte alarmChannel = (byte) 1; // 1 Email, 2 SMS, 4 YOUNI

	private long createtime;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "itemid", nullable = false, length = 10)
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Column(name = "userid", nullable = false, length = 10)
	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}
	
	@Column(name = "alarmchannel", nullable = true, length = 3)
	public byte getAlarmChannel() {
		return alarmChannel;
	}

	public void setAlarmChannel(byte alarmChannel) {
		this.alarmChannel = alarmChannel;
	}

	@Column(name = "createtime", nullable = true, length = 14)
	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
