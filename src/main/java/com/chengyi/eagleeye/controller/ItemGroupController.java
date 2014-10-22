package com.chengyi.eagleeye.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
public class ItemGroupController extends BaseController {
	private static Logger logger = Logger.getLogger(ItemGroupController.class);

	@RequestMapping("/itemgroup/new")
	public String newGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(loginUser.getId());
		request.setAttribute("itemGroups", itemGroups);
		return "itemgroup/newgroup";
	}

	/**
	 * 创建分组， FIXME!! 异常未捕捉
	 * 
	 * @param model
	 * @return 返回json数据
	 * @throws IOException
	 */
	@RequestMapping("/itemgroup/save")
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
		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(userId);
		if (CollectionUtils.isNotEmpty(itemGroups)) {
			for (ItemGroup itemGroup : itemGroups) {
				if (itemGroup.getName().equals(groupName)) {
					return ApplicaRuntime.responseJSON(-11, "重复分组名", response);
				}
			}
		}

		ItemGroup itemGroup = new ItemGroup();
		itemGroup.setName(groupName);
		itemGroup.setStatus((byte) 1);
		itemGroup.setUserId(userId);
		itemGroup.setCreatetime(System.currentTimeMillis());

		ItemGroup group = itemGroupMgr.saveItemGroup(itemGroup);
		if (group != null) {
			return ApplicaRuntime.responseJSON(1, "创建分组成功", response);
		} else {
			return ApplicaRuntime.responseJSON(-1, "服务器内部错误", response);
		}
	}

	@RequestMapping("/itemgroup/edit")
	public String editGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));
		ItemGroup itemGroup = itemGroupMgr.getItemGroup(gid);
		request.setAttribute("itemGroup", itemGroup);

		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(loginUser.getId());
		request.setAttribute("itemGroups", itemGroups);

		return "itemgroup/editgroup";
	}

	@RequestMapping("/itemgroup/update")
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
		ItemGroup itemGroup = itemGroupMgr.getItemGroup(gid);
		if (itemGroup == null) {
			return ApplicaRuntime.responseJSON(-1, "分组已不存在", response);
		} else if (!loginUser.getId().equals(itemGroup.getUserId())) {
			return ApplicaRuntime.responseJSON(-1, "权限错误", response);
		}

		// 判断是否重复
		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(loginUser.getId());
		if (CollectionUtils.isNotEmpty(itemGroups)) {
			for (ItemGroup tItemGroup : itemGroups) {
				if (!tItemGroup.getId().equals(itemGroup.getId()) && tItemGroup.getName().equals(gname)) {
					return ApplicaRuntime.responseJSON(-1, "已经存在该分组", response);
				}
			}
		}
		itemGroup.setName(gname);
		itemGroupMgr.updateItemGroup(itemGroup);

		return ApplicaRuntime.responseJSON(1, "修改分组成功", response);
	}

	@RequestMapping("/itemgroup/del")
	public String delete(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));

		ItemGroup itemGroup = itemGroupMgr.getItemGroup(gid);
		if (itemGroup == null) {
			return ApplicaRuntime.responseJSON(-1, "分组不存在", response);
		} else if (!loginUser.getId().equals(itemGroup.getUserId())) {
			return ApplicaRuntime.responseJSON(-1, "权限错误", response);
		}

		itemGroupMgr.deleteItemGroup(gid);

		return ApplicaRuntime.responseJSON(1, "删除分组成功", response);
	}
	
	@RequestMapping("/itemgroup/getMonitorsByGid")
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

	@RequestMapping("/itemgroup/list")
	public String list(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}

		// get itemGroups
		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(loginUser.getId());

		return ApplicaRuntime.responseJSON(1, JSONArray.fromObject(itemGroups).toString(), response);
	}

}
