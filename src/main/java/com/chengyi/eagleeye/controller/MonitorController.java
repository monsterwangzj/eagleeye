package com.chengyi.eagleeye.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chengyi.eagleeye.model.Monitor;
import com.chengyi.eagleeye.model.MonitorGroup;
import com.chengyi.eagleeye.model.MonitorGroupMonitor;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.service.ItemGroupMgr;
import com.chengyi.eagleeye.service.ItemMgr;
import com.chengyi.eagleeye.service.MonitorGroupMgr;
import com.chengyi.eagleeye.service.MonitorGroupMonitorMgr;
import com.chengyi.eagleeye.service.MonitorMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class MonitorController extends BaseController {
	private static Logger logger = Logger.getLogger(MonitorController.class);

	/**
	 * 获取所有项目分组
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/monitorgroup/list")
	public String listMonitorGroups(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		// get monitorGroups
		List<MonitorGroup> monitorGroups = monitorGroupMgr.getAllMonitorGroups(loginUser.getId());

		return ApplicaRuntime.responseJSON(1, JSONArray.fromObject(monitorGroups).toString(), response);
	}

	/**
	 * 获取所有故障处理人，一次性完整取出，不需要分页
	 * 
	 * @see UserController.getMonitors(), uri address is /config/getmonitors
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/monitor/list")
	public String listMonitors(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		// get monitorGroups
		Long loginUserId = loginUser.getId();
		Long monitorCount = monitorMgr.countMonitorsByUserId(loginUserId);
		Long totalPage = CommonUtil.getTotalPage(monitorCount, CommonUtil.pageSize);
		List<Monitor> monitors = new ArrayList<Monitor>();
		for (int p = 1; p <= totalPage; p++) {
			List<Monitor> plist = monitorMgr.getFaultHandleUsers(loginUserId, p);
			monitors.addAll(plist);
		}

		return ApplicaRuntime.responseJSON(1, JSONArray.fromObject(monitors).toString(), response);
	}

	/**
	 * 获取某报警组成员及报警通道列表
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/groupmonitor/list")
	public String listMonitornMapsByMgid(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		Long mgid = CommonUtil.getLong(request.getParameter("gid"));
		if (mgid == null) {
			return ApplicaRuntime.responseJSON(-3, "参数为空", response);
		}

		Long monitorCount = monitorGroupMonitorMgr.countMonitorsByGroupId(mgid);
		Long totalPage = CommonUtil.getTotalPage(monitorCount, CommonUtil.pageSize);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Long p = 1L; p <= totalPage; p++) {
			List<MonitorGroupMonitor> mgms = monitorGroupMonitorMgr.getMonitorsByGroupId(mgid, p);
			if (CollectionUtils.isNotEmpty(mgms)) {
				for (MonitorGroupMonitor mgm : mgms) {
					JSONObject json = new JSONObject();
					json.put("mgm", mgm);
					Long monitorId = mgm.getMonitorId();
					Monitor monitor = monitorMgr.getMonitor(monitorId);
					json.put("monitor", monitor);
					jsonList.add(json);
				}
			}
		}

		return ApplicaRuntime.responseJSON(1, JSONArray.fromObject(jsonList).toString(), response);
	}
}
