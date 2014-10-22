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
 * item对应的故障组
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "itemmonitorgroup")
public class ItemMonitorGroup implements Serializable {
	private static final long serialVersionUID = -5435390072244378162L;

	private Long id;

	private Long itemId;

	private Long userId;

	private Long monitorGroupId;

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
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "monitorgroupid", nullable = false, length = 10)
	public Long getMonitorGroupId() {
		return monitorGroupId;
	}

	public void setMonitorGroupId(Long monitorGroupId) {
		this.monitorGroupId = monitorGroupId;
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
