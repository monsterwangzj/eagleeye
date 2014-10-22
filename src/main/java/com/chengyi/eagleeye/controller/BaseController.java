package com.chengyi.eagleeye.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.chengyi.eagleeye.manage.IdentityCheckPoint;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.service.AlarmHistoryMgr;
import com.chengyi.eagleeye.service.BreakDownHistoryMgr;
import com.chengyi.eagleeye.service.ItemGroupMgr;
import com.chengyi.eagleeye.service.ItemMgr;
import com.chengyi.eagleeye.service.ItemMonitorGroupMgr;
import com.chengyi.eagleeye.service.MessageMgr;
import com.chengyi.eagleeye.service.MonitorGroupMgr;
import com.chengyi.eagleeye.service.MonitorGroupMonitorMgr;
import com.chengyi.eagleeye.service.MonitorMgr;
import com.chengyi.eagleeye.service.UserMgr;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class BaseController {
	private static Logger logger = Logger.getLogger(BaseController.class);

	@Autowired
	protected UserMgr userMgr;

	@Autowired
	protected MonitorMgr monitorMgr;

	@Autowired
	protected MonitorGroupMgr monitorGroupMgr;

	@Autowired
	protected MonitorGroupMonitorMgr monitorGroupMonitorMgr;

	@Autowired
	protected MonitorGroupMgr userGroupMgr;

	@Autowired
	protected ItemMgr itemMgr;

	@Autowired
	protected ItemGroupMgr itemGroupMgr;

	@Autowired
	protected ItemMonitorGroupMgr itemMonitorGroupMgr;

	@Autowired
	protected BreakDownHistoryMgr breakDownHistoryMgr;

	@Autowired
	protected AlarmHistoryMgr alarmHistoryMgr;

	@Autowired
	protected MessageMgr messageMgr;
	
	public User getUserFromCookie(Model model, HttpServletRequest request, HttpServletResponse response) {
		Long loginUserId = IdentityCheckPoint.getUidFromCookie(request, response);
		if (loginUserId != null) {
			User loginUser = userMgr.getUser(loginUserId);
			request.setAttribute("loginUser", loginUser);
			return loginUser;
		}
		return null;
	}

}
