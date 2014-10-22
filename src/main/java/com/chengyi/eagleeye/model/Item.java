package com.chengyi.eagleeye.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.util.DateUtil;

/**
 * item object, basic monitor object.
 * 
 * @author wangzhaojun
 * 
 */
@Entity
@Table(name = "item2")
public class Item implements Serializable {
	private static final long serialVersionUID = 8234908451575349523L;

	/**
	 * item status 定义
	 */
	public static final byte STATUS_FREEZED = (byte) 0;
	public static final byte STATUS_NORMAL = (byte) 1;
	public static final byte STATUS_REMOVED = (byte) -1;

	/**
	 * item type 定义
	 */
	public static final int TYPE_HTTP = 0;
	public static final int TYPE_PING = 10;
	public static final int TYPE_NGINX = 20;
	public static final int TYPE_APACHE = 30;
	public static final int TYPE_RESIN = 40;
	public static final int TYPE_REDIS = 100;
	
	private Long id;

	private Long userId;

	private Long gid;

	private String name;

	private String uri;

	// 监控类型：http或ping、或nginx、dns、mc等
	private int type = 0;

	// item 详细参数, JSONObject格式
	private String options;

	private byte status; // 0 未启用; 1启用; -1已删除

	private long priority = 0; // 优先级, 默认0

	// 监控频率， 单位秒
	private int monitorFreq;

	// 重试几次后告警
	private int retryTimes;

	// 重试间隔，单位秒
	private int retryInterval;

	// 连续告警提醒间隔，单位秒， 0表示持续发送告警, Integer.MAX_VALUE表示仅发一次报警(超过68年)
	private int continuousReminder = Integer.MAX_VALUE;

	private long createtime;

	private long lastmodified;

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

	@Column(name = "gid", nullable = true, length = 10)
	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

	@Column(name = "name", nullable = false, length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "uri", nullable = false, length = 512)
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Column(name = "type", nullable = false, length = 10)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "options", nullable = true, length = 1024)
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	@Column(name = "status", nullable = true, length = 3)
	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	@Column(name = "priority", nullable = false, length = 10)
	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	@Column(name = "monitorfreq", nullable = true, length = 10)
	public int getMonitorFreq() {
		return monitorFreq;
	}

	public void setMonitorFreq(int monitorFreq) {
		this.monitorFreq = monitorFreq;
	}

	@Column(name = "retrytimes", nullable = true, length = 10)
	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	@Column(name = "retryinterval", nullable = true, length = 10)
	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	@Column(name = "continuousreminder", nullable = true, length = 10)
	public int getContinuousReminder() {
		return continuousReminder;
	}

	public void setContinuousReminder(int continuousReminder) {
		this.continuousReminder = continuousReminder;
	}

	@Column(name = "createtime", nullable = true, length = 14)
	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	@Column(name = "lastmodified", nullable = true, length = 14)
	public long getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}

	public static int getStatFreqByMonitorFreq(int monitorFreq) {
		if (monitorFreq == 10) {
			return 1 * 60;
		} else if (monitorFreq == 1 * 60) {
			return 6 * 60;
		} else if (monitorFreq == 2 * 60) {
			return 12 * 60;
		} else if (monitorFreq == 5 * 60) {
			return 30 * 60;
		} else if (monitorFreq == 10 * 60) {
			return 60 * 60;
		} else if (monitorFreq == 15 * 60) {
			return 90 * 60;
		} else if (monitorFreq == 20 * 60) {
			return 120 * 60;
		} else if (monitorFreq == 30 * 60) {
			return 180 * 60;
		} else if (monitorFreq == 60 * 60) {
			return 360 * 60;
		} else {
			return 60 * 60;
		}
	}

	public static long[] getStatTimeByStatFreq(int statFreq) {
		long current = System.currentTimeMillis();
		long todayBegin = DateUtil.getStartOfDay(new Date()).getTime();
		long secondsGoesBy = (current - todayBegin) / 1000;
		long dep = secondsGoesBy % statFreq;

		long endTime = secondsGoesBy - dep;
		long startTime = endTime - statFreq;

		int previous = 1;
		if (statFreq == 60) {
			previous = 10;
		} else if (statFreq == 6 * 60) {
			previous = 2;
		}
		long endTime2 = startTime - (previous - 1) * statFreq;
		long startTime2 = endTime2 - statFreq;

		long r[] = { todayBegin + startTime * 1000, todayBegin + endTime * 1000, todayBegin + startTime2 * 1000, todayBegin + endTime2 * 1000 };
		return r;
	}

	public static String getTypeName(int type) {
		switch (type) {
		case TYPE_HTTP:
			return "Http";
		case TYPE_PING:
			return "Ping";
		case TYPE_NGINX:
			return "Nginx";
		case TYPE_APACHE:
			return "Apache";
		case TYPE_REDIS:
			return "Redis";
		case TYPE_RESIN:
			return "Resin";
		}
		return "-";
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	

}
