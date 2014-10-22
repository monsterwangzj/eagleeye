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
 * 监控人分组
 * 
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "monitorgroup")
public class MonitorGroup implements Serializable {
	private static final long serialVersionUID = -4922890220819641427L;

	private Long id;

	private String groupName;

	private Long userId;

	private byte type; // 0 普通报警组; 1 默认报警组

	private long createTime;

	public static final String GROUPNAME_DEFAULT = "默认报警组";
	public static final byte TYPE_NORMAL = (byte) 0;
	public static final byte TYPE_DEFAULT = (byte) 1;
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "groupname", nullable = true, length = 32)
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Column(name = "userid", nullable = true, length = 10)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "type", nullable = true, length = 3)
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
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
