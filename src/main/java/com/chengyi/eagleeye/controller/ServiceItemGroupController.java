package com.chengyi.eagleeye.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chengyi.eagleeye.model.ItemGroup;
import com.chengyi.eagleeye.model.Monitor;
import com.chengyi.eagleeye.model.MonitorGroup;
import com.chengyi.eagleeye.model.MonitorGroupMonitor;
import com.chengyi.eagleeye.model.ServiceItemGroup;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.service.ServiceItemGroupMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class ServiceItemGroupController extends BaseController {
	private static Logger logger = Logger.getLogger(ServiceItemGroupController.class);

	@Autowired
	protected ServiceItemGroupMgr serviceItemGroupMgr;
	
	@RequestMapping("/serviceitemgroup/new")
	public String newGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		List<ServiceItemGroup> serviceItemGroups = serviceItemGroupMgr.getServiceItemGroupsByUserId(loginUser.getId());
		request.setAttribute("serviceItemGroups", serviceItemGroups);
		return "serviceitemgroup/newgroup";
	}

	/**
	 * 创建分组， FIXME!! 异常未捕捉
	 * 
	 * @param model
	 * @return 返回json数据
	 * @throws IOException
	 */
	@RequestMapping("/serviceitemgroup/save")
	public String saveGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String groupName = request.getParameter("gname");
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long userId = loginUser.getId();
		if (StringUtils.isEmpty(groupName)) {
			return ApplicaRuntime.responseJSON(-10, "请输入组名", response);
		} else if (groupName.length() > 32) { // FIXME, chinese length
			return ApplicaRuntime.responseJSON(-11, "组名过长", response);
		}

		// 判断是否重复
		List<ServiceItemGroup> serviceItemGroups = serviceItemGroupMgr.getServiceItemGroupsByUserId(userId);
		if (CollectionUtils.isNotEmpty(serviceItemGroups)) {
			for (ServiceItemGroup serviceItemGroup : serviceItemGroups) {
				if (serviceItemGroup.getName().equals(groupName)) {
					return ApplicaRuntime.responseJSON(-11, "重复分组名", response);
				}
			}
		}

		ServiceItemGroup itemGroup = new ServiceItemGroup();
		itemGroup.setName(groupName);
		itemGroup.setStatus((byte) 1);
		itemGroup.setUserId(userId);
		itemGroup.setCreatetime(System.currentTimeMillis());

		ServiceItemGroup group = serviceItemGroupMgr.saveServiceItemGroup(itemGroup);
		if (group != null) {
			return ApplicaRuntime.responseJSON(1, "创建分组成功", response);
		} else {
			return ApplicaRuntime.responseJSON(-1, "服务器内部错误", response);
		}
	}

	@RequestMapping("/serviceitemgroup/edit")
	public String editGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));
		ServiceItemGroup serviceItemGroup = serviceItemGroupMgr.getServiceItemGroup(gid);
		request.setAttribute("serviceItemGroup", serviceItemGroup);

		List<ServiceItemGroup> serviceItemGroups = serviceItemGroupMgr.getServiceItemGroupsByUserId(loginUser.getId());
		request.setAttribute("serviceItemGroups", serviceItemGroups);

		return "serviceitemgroup/editgroup";
	}

	@RequestMapping("/serviceitemgroup/update")
	public String updateGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));
		String gname = request.getParameter("gname");

		if (StringUtils.isEmpty(gname)) {
			return ApplicaRuntime.responseJSON(-1, "组名称为空", response);
		}
		ServiceItemGroup itemGroup = serviceItemGroupMgr.getServiceItemGroup(gid);
		if (itemGroup == null) {
			return ApplicaRuntime.responseJSON(-1, "分组已不存在", response);
		} else if (!loginUser.getId().equals(itemGroup.getUserId())) {
			return ApplicaRuntime.responseJSON(-1, "权限错误", response);
		}

		// 判断是否重复
		List<ServiceItemGroup> itemGroups = serviceItemGroupMgr.getServiceItemGroupsByUserId(loginUser.getId());
		if (CollectionUtils.isNotEmpty(itemGroups)) {
			for (ServiceItemGroup tItemGroup : itemGroups) {
				if (!tItemGroup.getId().equals(itemGroup.getId()) && tItemGroup.getName().equals(gname)) {
					return ApplicaRuntime.responseJSON(-1, "已经存在该分组", response);
				}
			}
		}
		itemGroup.setName(gname);
		serviceItemGroupMgr.updateServiceItemGroup(itemGroup);

		return ApplicaRuntime.responseJSON(1, "修改分组成功", response);
	}

	@RequestMapping("/serviceitemgroup/del")
	public String delete(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));

		ServiceItemGroup itemGroup = serviceItemGroupMgr.getServiceItemGroup(gid);
		if (itemGroup == null) {
			return ApplicaRuntime.responseJSON(-1, "分组不存在", response);
		} else if (!loginUser.getId().equals(itemGroup.getUserId())) {
			return ApplicaRuntime.responseJSON(-1, "权限错误", response);
		}

		serviceItemGroupMgr.deleteServiceItemGroup(gid);

		return ApplicaRuntime.responseJSON(1, "删除分组成功", response);
	}
	
	@RequestMapping("/serviceitemgroup/getMonitorsByGid")
	public String getMonitorsByGid(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));

		MonitorGroup monitorGroup = monitorGroupMgr.getMonitorGroup(gid);
		if (monitorGroup == null) {
			return ApplicaRuntime.responseJSON(-1, "分组不存在", response);
		} else if (!loginUser.getId().equals(monitorGroup.getUserId())) {
			return ApplicaRuntime.responseJSON(-1, "权限错误", response);
		}

		Long monitorTotalCount = monitorGroupMonitorMgr.countMonitorsByGroupId(gid);
		Long monitorTotalPage = CommonUtil.getTotalPage(monitorTotalCount, CommonUtil.pageSize);
		List<Monitor> monitors = new ArrayList<Monitor>();
		for (long p = 1; p <= monitorTotalPage; p++) {
			List<MonitorGroupMonitor> mgms = monitorGroupMonitorMgr.getMonitorsByGroupId(gid, p);
			for (MonitorGroupMonitor mgm : mgms ) {
				Monitor monitor = monitorMgr.getMonitor(mgm.getMonitorId());
				monitors.add(monitor);
			}
		}
		
		return ApplicaRuntime.responseJSON(1, JSONArray.fromObject(monitors).toString(), response);
	}

	@RequestMapping("/serviceitemgroup/list")
	public String list(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		// get itemGroups
		List<ServiceItemGroup> serviceItemGroups = serviceItemGroupMgr.getServiceItemGroupsByUserId(loginUser.getId());

		return ApplicaRuntime.responseJSON(1, JSONArray.fromObject(serviceItemGroups).toString(), response);
	}

}
