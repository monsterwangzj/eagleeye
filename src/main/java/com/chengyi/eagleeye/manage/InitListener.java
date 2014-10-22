package com.chengyi.eagleeye.manage;

import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoader;

import com.chengyi.eagleeye.util.ApplicaRuntime;

public class InitListener extends ContextLoader implements ServletContextListener {
	public static final String startUpTimeKey = "startupTime";

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.setAttribute(startUpTimeKey, System.currentTimeMillis());

		String contextStr = context.toString(); // WebApp[http://localhost:8080]
		
		System.out.println("contextStr:" + contextStr);

//		String[] strarr = contextStr.split(":|]");
		String hosturi = contextStr.replaceAll("WebApp\\[http://", "").replaceAll("\\]", "");
		String[] strarr = hosturi.split(":");
		
		if (strarr != null && strarr.length >= 2) {
    		ApplicaRuntime.port = Integer.parseInt(strarr[1]);
		}
		ApplicaRuntime.serverDomain = strarr[0];

		String uuid = UUID.randomUUID().toString();
//		ApplicaRuntime.globalFlag = uuid;

		String realPath = context.getRealPath("/"); // webapp's dir
		String containerPath = System.getProperty("user.dir"); // web container's dir
		ApplicaRuntime.containerPath = containerPath;
		
		String serverInfo = context.getServerInfo();
		
		System.out.println("contextStr:" + contextStr + ", port:" + ApplicaRuntime.port + ", uuid:" + uuid + ", realPath:" + realPath + ", containerPath:" + containerPath + ", serverInfo:" + serverInfo);
	}

	public void contextDestroyed(ServletContextEvent sce) {
	    
	}

}
