package com.chengyi.eagleeye.model.message.redis;

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
@Table(name = "redismessage")
public class RedisMessage extends Message {
	private static final long serialVersionUID = 8534646856775595123L;

	private String version; // 版本号
	private String mode; // redis mode
	private long totalTime; // 运行时间
	private float maxUsedMemory; // 最大使用内存
	private float currentUsedMemory; // 当前使用内存

	private float currentCPS; // 平均执行命令数/秒
	private float hitRate; // 命中率
	private int clients; // connected clients

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

	@Column(name = "version", nullable = true, length = 10)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "mode", nullable = true, length = 20)
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Column(name = "totaltime", nullable = true, length = 14)
	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	@Column(name = "maxusedmemory", nullable = true, precision = 3)
	public float getMaxUsedMemory() {
		return maxUsedMemory;
	}

	public void setMaxUsedMemory(float maxUsedMemory) {
		this.maxUsedMemory = maxUsedMemory;
	}

	@Column(name = "currentusedmemory", nullable = true, precision = 3)
	public float getCurrentUsedMemory() {
		return currentUsedMemory;
	}

	public void setCurrentUsedMemory(float currentUsedMemory) {
		this.currentUsedMemory = currentUsedMemory;
	}

	@Column(name = "currentcps", nullable = true, precision = 3)
	public float getCurrentCPS() {
		return currentCPS;
	}

	public void setCurrentCPS(float currentCPS) {
		this.currentCPS = currentCPS;
	}

	@Column(name = "hitrate", nullable = true, precision = 3)
	public float getHitRate() {
		return hitRate;
	}

	public void setHitRate(float hitRate) {
		this.hitRate = hitRate;
	}
	
	@Column(name = "clients", nullable = true, length = 10)
	public int getClients() {
		return clients;
	}

	public void setClients(int clients) {
		this.clients = clients;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
