package com.chengyi.eagleeye.model.assist;

import java.io.Serializable;

public class PingOption implements Serializable {

	private static final long serialVersionUID = -3437507980639039266L;

	private String serverIps;

	public String getServerIps() {
		return serverIps;
	}

	public void setServerIps(String serverIps) {
		this.serverIps = serverIps;
	}

}
