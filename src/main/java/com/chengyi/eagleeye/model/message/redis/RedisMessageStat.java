package com.chengyi.eagleeye.model.message.redis;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.chengyi.eagleeye.model.message.MessageStat;

@Entity
@Table(name = "redismessagestat")
public class RedisMessageStat extends MessageStat {
	private static final long serialVersionUID = -7609096429769392194L;

	private String version; // 版本号
	private String mode; // redis mode
	private long totalTime; // 运行时间
	
	private float minUsedMemory; // 最小使用内存
	private float avgUsedMemory; 
	private float maxUsedMemory; 
	
	private float minCPS; // 最小执行命令数/秒
	private float avgCPS; 
	private float maxCPS; 
	
	private float minHitRate; // 命中率
	private float avgHitRate; 
	private float maxHitRate; 
	
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

	@Column(name = "type", nullable = false, length = 3)
	public int getType() {
		return super.getType();
	}

	public void setType(int type) {
		super.setType(type);
	}

	@Column(name = "workerip", nullable = true, length = 40)
	public String getWorkerIp() {
		return super.getWorkerIp();
	}

	public void setWorkerIp(String workerIp) {
		super.setWorkerIp(workerIp);
	}

	@Column(name = "timeunittype", nullable = true, length = 3)
	public byte getTimeUnitType() {
		return super.getTimeUnitType();
	}

	public void setTimeUnitType(byte timeUnitType) {
		super.setTimeUnitType(timeUnitType);
	}

	@Column(name = "createtime", nullable = true, length = 14)
	public long getCreateTime() {
		return super.getCreateTime();
	}

	public void setCreateTime(long createTime) {
		super.setCreateTime(createTime);
	}
	@Column(name = "version", nullable = false, length = 10)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	@Column(name = "mode", nullable = false, length = 20)
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
	@Column(name = "minusedmemory", nullable = true, precision = 3)
	public float getMinUsedMemory() {
		return minUsedMemory;
	}

	public void setMinUsedMemory(float minUsedMemory) {
		this.minUsedMemory = minUsedMemory;
	}
	@Column(name = "avgusedmemory", nullable = true, precision = 3)
	public float getAvgUsedMemory() {
		return avgUsedMemory;
	}

	public void setAvgUsedMemory(float avgUsedMemory) {
		this.avgUsedMemory = avgUsedMemory;
	}
	@Column(name = "maxusedmemory", nullable = true, precision = 3)
	public float getMaxUsedMemory() {
		return maxUsedMemory;
	}

	public void setMaxUsedMemory(float maxUsedMemory) {
		this.maxUsedMemory = maxUsedMemory;
	}
	@Column(name = "mincps", nullable = true, precision = 3)
	public float getMinCPS() {
		return minCPS;
	}

	public void setMinCPS(float minCPS) {
		this.minCPS = minCPS;
	}
	@Column(name = "avgcps", nullable = true, precision = 3)
	public float getAvgCPS() {
		return avgCPS;
	}

	public void setAvgCPS(float avgCPS) {
		this.avgCPS = avgCPS;
	}
	@Column(name = "maxcps", nullable = true, precision = 3)
	public float getMaxCPS() {
		return maxCPS;
	}

	public void setMaxCPS(float maxCPS) {
		this.maxCPS = maxCPS;
	}

	public float getMinHitRate() {
		return minHitRate;
	}

	public void setMinHitRate(float minHitRate) {
		this.minHitRate = minHitRate;
	}

	public float getAvgHitRate() {
		return avgHitRate;
	}

	public void setAvgHitRate(float avgHitRate) {
		this.avgHitRate = avgHitRate;
	}

	public float getMaxHitRate() {
		return maxHitRate;
	}

	public void setMaxHitRate(float maxHitRate) {
		this.maxHitRate = maxHitRate;
	}

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
