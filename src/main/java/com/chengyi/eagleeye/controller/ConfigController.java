package com.chengyi.eagleeye.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chengyi.eagleeye.model.Monitor;
import com.chengyi.eagleeye.model.MonitorGroup;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.service.MonitorGroupMgr;
import com.chengyi.eagleeye.service.MonitorGroupMonitorMgr;
import com.chengyi.eagleeye.service.MonitorMgr;
import com.chengyi.eagleeye.service.UserMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class ConfigController extends BaseController {
	private static Logger logger = Logger.getLogger(ConfigController.class);

	@RequestMapping("/config/index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get index:" + loginUser);
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		if (loginUser == null) {
			return "redirect:/login.do";
		}

		Long userId = loginUser.getId();
		List<Monitor> users = monitorMgr.getFaultHandleUsers(userId, currentPage);
		long monitorCount = monitorMgr.countMonitorsByUserId(userId);
		long totalPage = CommonUtil.getTotalPage(monitorCount, CommonUtil.pageSize);
		if (currentPage > totalPage)
			currentPage = totalPage;

		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("monitors", users);

		request.setAttribute("menu", "config");
		return "config/config_index";
	}

	@RequestMapping("/config/alarmgroup")
	public String faultHandlerGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get index:" + loginUser);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		long currentPage = CommonUtil.getPage(request.getParameter("p"));

		Long loginUserId = loginUser.getId();
		Long monitorGroupCount = monitorGroupMgr.countMonitorGroupByUserId(loginUserId);
		Long totalPage = CommonUtil.getTotalPage(monitorGroupCount, CommonUtil.pageSize);
		if (currentPage > totalPage)
			currentPage = totalPage;
		if (currentPage < 1)
			currentPage = 1;
		List<MonitorGroup> monitorGroups = monitorGroupMgr.getMonitorGroupsByUserId(loginUserId, currentPage);
		HashMap<Long, Long> monitorGroupMap = new HashMap<Long, Long>();
		if (CollectionUtils.isNotEmpty(monitorGroups)) {
			for (MonitorGroup monitorGroup : monitorGroups) {
				Long tId = monitorGroup.getId();
				Long tCount = monitorGroupMonitorMgr.countMonitorsByGroupId(tId);
				monitorGroupMap.put(tId, tCount);
			}
		}
		request.setAttribute("monitorGroups", monitorGroups);
		request.setAttribute("monitorGroupMap", monitorGroupMap);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);

		request.setAttribute("menu", "config");

		return "config/config_alarmgroup";
	}

	@RequestMapping("/config/editprofile")
	public String editprofile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get user:" + loginUser);
		if (loginUser == null) {
			return "redirect:/login.do";
		}

		request.setAttribute("menu", "config");

		return "config/config_editprofile";
	}

	@RequestMapping("/config/updateprofile")
	public String updateprofile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get user:" + loginUser);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String weibo = request.getParameter("weibo");
		String cellphone = request.getParameter("cellphone");

		if (StringUtils.isNotEmpty(name)) {
			loginUser.setName(name);
		}
		if (StringUtils.isNotEmpty(email)) {
			loginUser.setEmail(email);
		}
		if (StringUtils.isNotEmpty(cellphone)) {
			loginUser.setCellphone(cellphone);
		}
		loginUser.setWeibo(weibo);

		userMgr.updateUser(loginUser);
		return ApplicaRuntime.responseJSON(1, "修改个人信息成功", response);
	}

	@RequestMapping("/config/editpwd")
	public String editpwd(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get user:" + loginUser);
		if (loginUser == null) {
			return "redirect:/login.do";
		}

		request.setAttribute("loginUser", loginUser);
		request.setAttribute("menu", "config");

		return "config/config_editpwd";
	}

	@RequestMapping("/config/updatepwd")
	public String updatepwd(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		String currentPasswd = request.getParameter("currentPasswd");
		String newPasswd = request.getParameter("newPasswd");
		String newPasswd2 = request.getParameter("newPasswd2");

		if (!loginUser.getPassword().equals(currentPasswd)) {
			return ApplicaRuntime.responseJSON(-1, "当前登录密码不正确", response);
		}
		if (StringUtils.isEmpty(newPasswd) || StringUtils.isEmpty(newPasswd2)) {
			return ApplicaRuntime.responseJSON(-2, "新密码不能为空", response);
		}
		if (newPasswd.equals(currentPasswd)) {
			return ApplicaRuntime.responseJSON(-3, "新密码不能与旧密码相同", response);
		}
		if (!newPasswd.equals(newPasswd2)) {
			return ApplicaRuntime.responseJSON(-4, "两次输入的新密码不相同", response);
		}

		loginUser.setPassword(newPasswd);
		loginUser.setLastmodified(System.currentTimeMillis());
		userMgr.updateUser(loginUser);

		return ApplicaRuntime.responseJSON(1, "修改密码成功", response);
	}

}
