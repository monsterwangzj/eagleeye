package com.chengyi.eagleeye.model.assist;

import java.io.Serializable;

public class NginxOption implements Serializable {
	private static final long serialVersionUID = 814875664381456164L;
	
	private String serverIps;

	public String getServerIps() {
		return serverIps;
	}

	public void setServerIps(String serverIps) {
		this.serverIps = serverIps;
	}

}
