package com.chengyi.eagleeye.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chengyi.eagleeye.manage.IdentityCheckPoint;
import com.chengyi.eagleeye.model.BreakDownHistory;
import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.ItemGroup;
import com.chengyi.eagleeye.model.Monitor;
import com.chengyi.eagleeye.model.MonitorGroup;
import com.chengyi.eagleeye.model.MonitorGroupMonitor;
import com.chengyi.eagleeye.model.ServiceItemGroup;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.patrol.RedisUtil;
import com.chengyi.eagleeye.service.ServiceItemGroupMgr;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.DateUtil;
import com.chengyi.eagleeye.util.ServerStatus;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class UserController extends BaseController{
	private static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	protected ServiceItemGroupMgr serviceItemGroupMgr;
	
	@RequestMapping("/login")
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser != null) {
			return "redirect:/index.do";
		} else {
			return "user/login";
		}
	}

	@RequestMapping("/login/signin.do")
	public String signin(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		logger.info("username:" + username + ", password:" + password + ", userMgr:" + userMgr);
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return "redirect:/index.do";
		}

		User user = userMgr.getUser(username, password);
		logger.info(user);
		if (user == null) {
			logger.info("get into signin error page");
			model.addAttribute("message", "用户名或密码错误");
			return "user/login";
		} else {
			IdentityCheckPoint.setIdentity(response, user);

			// check default monitorgroup
			boolean hasDefaultMonitorGroup = userGroupMgr.isDefaultMonitorGroupExists(user.getId());
			logger.info("DefaultMonitorGroup exists:" + hasDefaultMonitorGroup);
			if (!hasDefaultMonitorGroup) {
				MonitorGroup mg = new MonitorGroup();
				mg.setUserId(user.getId());
				mg.setGroupName(MonitorGroup.GROUPNAME_DEFAULT);
				mg.setType((byte) 1);
				mg.setCreateTime(System.currentTimeMillis());
				mg = userGroupMgr.saveUserGroup(mg);

				
				// 根据uid找到monitorId
				Monitor m = monitorMgr.getMonitorByNamenId(user.getName(), user.getId());
				if (m == null) {
					m = new Monitor();
					m.setName(user.getName());
					m.setUserId(user.getId());
					m.setCellphone(user.getCellphone());
					m.setEmail(user.getEmail());
					m.setWeixin(user.getWeixin());
					m.setWeibo(user.getWeibo());
					m.setCreateTime(System.currentTimeMillis());
					m = monitorMgr.saveMonitor(m);
				}
				
				// save 关联关系
				MonitorGroupMonitor mgm = new MonitorGroupMonitor();
				mgm.setAlarmChannel((byte) 3); // email, sms
				mgm.setMonitorGroupId(mg.getId());
				mgm.setMonitorId(m.getId()); // monitorId
				mgm.setPriority(1);
				mgm.setCreateTime(System.currentTimeMillis());
				monitorGroupMonitorMgr.saveMonitorGroupMonitor(mgm);
			}
			return "redirect:/index.do";
		}
	}

	@RequestMapping("/index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long loginUserId = loginUser.getId();
		
		List<Item> items = new ArrayList<Item>();
		List<Item> webItems = itemMgr.getItemsByUserId(loginUserId);
		List<Item> serviceItems = itemMgr.getServiceItemsByUserId(loginUserId);
		items.addAll(webItems);
		items.addAll(serviceItems);
		
		Map<Long, Object[]> rateMap = new LinkedHashMap<Long, Object[]>();
		Map<String, Long> statusMap = new LinkedHashMap<String, Long>();
		List<Item> downingItems = new ArrayList<Item>();
		List<Item> instableItems = new ArrayList<Item>();
		if (items != null) {
			for (Item item : items) {
				Long itemId = item.getId();
				String keyPrefix = ApplicaRuntime.globalFlag + itemId + "_" + DateUtil.format8chars(new Date());
				
				Long itemStatus = RedisUtil.getLong(itemId + "_status_" + ApplicaRuntime.globalFlag);
				
				if (itemStatus == null) {
					itemStatus = 0L;
				} else if (itemStatus == ServerStatus.DOWN) { // downing item
					downingItems.add(item);
				} else if (itemStatus == ServerStatus.INSTABLE) { // instable item
					instableItems.add(item);
				}
				if (statusMap.get(itemStatus.toString()) == null) {
					statusMap.put(itemStatus.toString(), 0L);
				}
				if (statusMap.get(item.getType() + "-" + itemStatus) == null) {
					statusMap.put(item.getType() + "-" + itemStatus, 0L);
				}
				
				statusMap.put(itemStatus.toString(), statusMap.get(itemStatus.toString()) + 1L);
				statusMap.put(item.getType() + "-" + itemStatus, statusMap.get(item.getType() + "-" + itemStatus) + 1L);
				
				logger.info("itemId:" + itemId + ", status:" + itemStatus + ", count:" + statusMap.get(itemStatus));
				
				Double avgCost = null;
				if (item.getType() == Item.TYPE_HTTP) {
					Long succAccessCount = RedisUtil.getLong(keyPrefix + "_succAccessCount");
					Long succAccessCostTime = RedisUtil.getLong(keyPrefix + "_succAccessCostTime");
					if (succAccessCount != null && succAccessCount > 0) {
						if (succAccessCostTime == null) avgCost = 0.;
						else avgCost = succAccessCostTime * 1.0 / succAccessCount;
					}
				} else if (item.getType() == Item.TYPE_PING){
					Long totalAccessCount = RedisUtil.getLong(keyPrefix + "_totalAccessCount");
					Long totalUsablity = RedisUtil.getLong(keyPrefix + "_totalUsablity");
					Long succAccessCostTime =  RedisUtil.getLong(keyPrefix + "_succAccessCostTime");
					if (totalUsablity != null && totalUsablity > 0) {
						if (succAccessCostTime == null) avgCost = 0.;
						else avgCost = succAccessCostTime * 0.001 / totalAccessCount;
					}
				}
				if (avgCost != null) {
					Object[] arr = { item, CommonUtil.get2pDouble(avgCost), itemStatus};
					rateMap.put(itemId, arr);
				}
			}
			
			// last 7 days alarms
			String start = ApplicaRuntime.getLastNDaysStartDate(6), end = ApplicaRuntime.getLastNDaysStartDate(-1);
			long startTime = 0L, endTime = 0L;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
			try {
				startTime = sdf.parse(start).getTime();
				endTime = sdf.parse(end).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			List<BreakDownHistory> alarms = breakDownHistoryMgr.getBreakDownHistorysByUserIdnCreateTime(loginUserId, startTime, endTime, 1L);
			Map<Long, Item> itemMap = new HashMap<Long, Item>();
			if (CollectionUtils.isNotEmpty(alarms)) {
				for (BreakDownHistory alarm : alarms) {
					Long itemId = alarm.getItemId();
					Item item = itemMap.get(itemId);
					if (item == null) {
						item = itemMgr.getItem(itemId);
						itemMap.put(itemId, item);
					}
				}
			}
			request.setAttribute("alarms", alarms);
			request.setAttribute("itemMap", itemMap);
		
			// not resume items
			List<BreakDownHistory> notResumeList = breakDownHistoryMgr.getNotResumeItemsByUserId(loginUserId, startTime, endTime, 1L);
			Map<Long, Item> notResumeItemMap = new HashMap<Long, Item>();
			if (CollectionUtils.isNotEmpty(notResumeList)) {
				for (BreakDownHistory alarm : notResumeList) {
					Long itemId = alarm.getItemId();
					Item item = notResumeItemMap.get(itemId);
					if (item == null) {
						item = itemMgr.getItem(itemId);
						notResumeItemMap.put(itemId, item);
					}
				}
			}
			request.setAttribute("notResumeList", notResumeList);
			request.setAttribute("notResumeItemMap", notResumeItemMap);
			
		}
		request.setAttribute("rateMap", rateMap);
		request.setAttribute("statusMap", statusMap);
		request.setAttribute("downingItems", downingItems);
		request.setAttribute("instableItems", instableItems);
		request.setAttribute("menu", "summary");
		return "index";
	}
	
	@RequestMapping("/monitor/index.do")
	public String monitor(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get index:" + loginUser);
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		Long gid = CommonUtil.getLong(request.getParameter("g"));
		String type = request.getParameter("tid");
		Integer tid = null;
		if (type != null) {
			if (type.equals("http")) {
				tid = Item.TYPE_HTTP;
			} else if (type.equals("ping")) {
				tid = Item.TYPE_PING;
			}
		}
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		if (currentPage < 0) {
			currentPage = 1;
		}
		Long userId = loginUser.getId();
		
		// get itemGroups
		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(loginUser.getId());
		HashMap<Long, Long> itemGroupMap = new HashMap<Long, Long>();
		if (CollectionUtils.isNotEmpty(itemGroups)) {
			for (ItemGroup itemGroup : itemGroups) {
				Long tId = itemGroup.getId();
				Long tCount = itemMgr.countWebItemsByGid(tId);
				itemGroupMap.put(tId, tCount);
			}
		}
		HashMap<Long, Long> itemTypeMap = new HashMap<Long, Long>();
		itemTypeMap.put(new Long(Item.TYPE_HTTP), itemMgr.countItemsByTid(userId, Item.TYPE_HTTP));
		itemTypeMap.put(new Long(Item.TYPE_PING), itemMgr.countItemsByTid(userId, Item.TYPE_PING));
		
		request.setAttribute("itemGroups", itemGroups);
		request.setAttribute("itemGroupMap", itemGroupMap);
		request.setAttribute("itemTypeMap", itemTypeMap);
		
		// get Items
		long itemCount = 0;
		long totalPage = 1;
		List<Item> items = null;
		if (tid != null) {
			itemCount = itemMgr.countItemsByTid(userId, tid);
			totalPage = CommonUtil.getTotalPage(itemCount, CommonUtil.pageSize);
			if (currentPage > totalPage) currentPage = totalPage;
			items = itemMgr.getItemsByTid(userId, tid, currentPage);
			
			request.setAttribute("tid", type);
		} else if (gid != null) {
			itemCount = itemMgr.countWebItemsByGid(gid);
			totalPage = CommonUtil.getTotalPage(itemCount, CommonUtil.pageSize);
			if (currentPage > totalPage) currentPage = totalPage;
			items = itemMgr.getItemsByGid(gid, currentPage);
			
			request.setAttribute("gid", gid);
		} else {
    		itemCount = itemMgr.countItemsByUserId(userId);
    		totalPage = CommonUtil.getTotalPage(itemCount, CommonUtil.pageSize);
    		if (currentPage > totalPage) currentPage = totalPage;
    		items = itemMgr.getItemsByUserId(userId, currentPage);
		}
		request.setAttribute("items", items);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		
		
		// available rate
		Map<Long, Double[]> rateMap = new HashMap<Long, Double[]>();
		if (items != null) {
			for (Item item : items) {
				Long itemId = item.getId();
				String keyPrefix = ApplicaRuntime.globalFlag + itemId + "_" + DateUtil.format8chars(new Date());
				if (item.getType() == Item.TYPE_HTTP) {
					Long totalAccessCount = RedisUtil.getLong(keyPrefix + "_totalAccessCount");
					Long succAccessCount = RedisUtil.getLong(keyPrefix + "_succAccessCount");
					Long succAccessCostTime =  RedisUtil.getLong(keyPrefix + "_succAccessCostTime");
					logger.info("succAccessCount|totalAccessCount|succAccessCostTime:" + succAccessCount + "|" + totalAccessCount + "|" + succAccessCostTime);
					
					Double rate = null, avgCost = null;
					if (totalAccessCount != null && totalAccessCount > 0) {
						if (succAccessCount == null) rate = 0.;
						else rate = succAccessCount * 100.0 / totalAccessCount;
					}
					
					if (succAccessCount != null && succAccessCount > 0) {
						if (succAccessCostTime == null) avgCost = 0.;
						else avgCost = succAccessCostTime * 1.0 / succAccessCount;
					}
					
					if (rate != null || avgCost != null) {
						Double[] arr = { CommonUtil.get2pDouble(rate), CommonUtil.get2pDouble(avgCost) };
						rateMap.put(itemId, arr);
					}
				} else if (item.getType() == Item.TYPE_PING) {
					Long totalAccessCount = RedisUtil.getLong(keyPrefix + "_totalAccessCount");
					Long totalUsablity = RedisUtil.getLong(keyPrefix + "_totalUsablity");
					Long succAccessCostTime =  RedisUtil.getLong(keyPrefix + "_succAccessCostTime");
					logger.info("totalAccessCount|totalUsablity|succAccessCostTime:" + totalAccessCount + "|" + totalUsablity + "|" + succAccessCostTime);
					Double rate = null, avgCost = null;
					
					if (totalAccessCount != null && totalAccessCount > 0) {
						if (totalUsablity == null) rate = 0.;
						else rate = totalUsablity  * 1.0 / totalAccessCount;
					}
					
					if (totalUsablity != null && totalUsablity > 0) {
						if (succAccessCostTime == null) avgCost = 0.;
						else avgCost = succAccessCostTime * 0.001 / totalAccessCount;
					}
					
					if (rate != null || avgCost != null) {
						Double[] arr = { CommonUtil.getNpDouble(rate, 3), CommonUtil.get2pDouble(avgCost) };
						rateMap.put(itemId, arr);
					}
				}
			}
		}
		request.setAttribute("rateMap", rateMap);
		request.setAttribute("menu", "monitor");
		
		return "user/http_index";
	}
	
	@RequestMapping("/monitor/service.do")
	public String service(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		logger.info("get index:" + loginUser);
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		Long gid = CommonUtil.getLong(request.getParameter("g"));
		String type = request.getParameter("tid");
		Integer tid = null;
		if (type != null) {
			if (type.equals("nginx")) {
				tid = Item.TYPE_NGINX;
			} else if (type.equals("apache")) {
				tid = Item.TYPE_APACHE;
			} else if (type.equals("resin")) {
				tid = Item.TYPE_RESIN;
			} else if (type.equals("redis")) {
				tid = Item.TYPE_REDIS;
			} 
		}
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		if (currentPage < 0) {
			currentPage = 1;
		}
		Long userId = loginUser.getId();
		
		
		
		// get itemGroups
		List<ServiceItemGroup> serviceItemGroups = serviceItemGroupMgr.getServiceItemGroupsByUserId(loginUser.getId());
		HashMap<Long, Long> serviceItemGroupMap = new HashMap<Long, Long>();
		if (CollectionUtils.isNotEmpty(serviceItemGroups)) {
			for (ServiceItemGroup serviceItemGroup : serviceItemGroups) {
				Long tId = serviceItemGroup.getId();
				Long tCount = itemMgr.countServiceItemsByGid(tId);
				serviceItemGroupMap.put(tId, tCount);
			}
		}
		request.setAttribute("serviceItemGroups", serviceItemGroups);
		request.setAttribute("serviceItemGroupMap", serviceItemGroupMap);
		
		// get Items
		long itemCount = 0;
		long totalPage = 1;
		List<Item> items = null;
		if (tid != null) {
			itemCount = itemMgr.countItemsByTid(userId, tid);
			totalPage = CommonUtil.getTotalPage(itemCount, CommonUtil.pageSize);
			if (currentPage > totalPage) currentPage = totalPage;
			items = itemMgr.getItemsByTid(userId, tid, currentPage);
			
			request.setAttribute("tid", type);
		} else if (gid == null) {
    		itemCount = itemMgr.countServiceItemsByUserId(userId);
    		totalPage = CommonUtil.getTotalPage(itemCount, CommonUtil.pageSize);
    		if (currentPage > totalPage) currentPage = totalPage;
    		items = itemMgr.getServiceItemsByUserId(userId, currentPage);
		} else {
			itemCount = itemMgr.countServiceItemsByGid(gid);
			totalPage = CommonUtil.getTotalPage(itemCount, CommonUtil.pageSize);
			if (currentPage > totalPage) currentPage = totalPage;
			items = itemMgr.getItemsByGid(gid, currentPage);
			
			request.setAttribute("gid", gid);
		}
		request.setAttribute("items", items);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		
		
		HashMap<Long, Long> itemTypeMap = new HashMap<Long, Long>();
		itemTypeMap.put(new Long(Item.TYPE_NGINX), itemMgr.countItemsByTid(userId, Item.TYPE_NGINX));
		itemTypeMap.put(new Long(Item.TYPE_RESIN), itemMgr.countItemsByTid(userId, Item.TYPE_RESIN));
		itemTypeMap.put(new Long(Item.TYPE_APACHE), itemMgr.countItemsByTid(userId, Item.TYPE_APACHE));
		itemTypeMap.put(new Long(Item.TYPE_REDIS), itemMgr.countItemsByTid(userId, Item.TYPE_REDIS));
		request.setAttribute("itemTypeMap", itemTypeMap);
		
		// available rate
		Map<Long, Double[]> rateMap = new HashMap<Long, Double[]>();
		if (items != null) {
			for (Item item : items) {
				Long itemId = item.getId();
				String keyPrefix = ApplicaRuntime.globalFlag + itemId + "_" + DateUtil.format8chars(new Date());
				Long totalAccessCount = RedisUtil.getLong(keyPrefix + "_totalAccessCount");
				Long succAccessCount = RedisUtil.getLong(keyPrefix + "_succAccessCount");
				Long succAccessCostTime =  RedisUtil.getLong(keyPrefix + "_succAccessCostTime");
				logger.info("succAccessCount|totalAccessCount|succAccessCostTime:" + succAccessCount + "|" + totalAccessCount + "|" + succAccessCostTime);
				
				Double rate = null, avgCost = null;
				if (totalAccessCount != null && totalAccessCount > 0) {
					if (succAccessCount == null) rate = 0.;
					else rate = succAccessCount * 100.0 / totalAccessCount;
				}
				if (succAccessCount != null && succAccessCount > 0) {
					if (succAccessCostTime == null) avgCost = 0.;
					else avgCost = succAccessCostTime * 1.0 / succAccessCount;
				}
				
				if (rate != null || avgCost != null) {
					Double[] arr = { CommonUtil.get2pDouble(rate), CommonUtil.get2pDouble(avgCost) };
					rateMap.put(itemId, arr);
				}
			}
		}
		request.setAttribute("rateMap", rateMap);
		request.setAttribute("menu", "monitor");
		
		return "user/service_index";
	}

	@RequestMapping("/logout")
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		IdentityCheckPoint.removeIdentity(request, response);
		return "redirect:/login.do";
	}

	@RequestMapping("/user/addmonitor")
	public String saveMonitor(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		String name = request.getParameter("name");
		String cellphone = request.getParameter("cellphone");
		String email = request.getParameter("email");
		String youni = request.getParameter("youni");
		String weixin = request.getParameter("weixin");
		if (StringUtils.isEmpty(name)) {
			return ApplicaRuntime.responseJSON(-1, "姓名为空", response);
		}
		if (StringUtils.isEmpty(cellphone)) {
			return ApplicaRuntime.responseJSON(-1, "手机号码为空", response);
		}
		if (StringUtils.isEmpty(email)) {
			return ApplicaRuntime.responseJSON(-1, "邮箱地址为空", response);
		}
//		if (StringUtils.isEmpty(weixin)) {
//			return ApplicaRuntime.responseJSON(-1, "微信号码为空", response);
//		}
		
		if (StringUtils.isEmpty(youni)) {
			youni = cellphone;
		}
		
		// check name replication
		List<Monitor> monitors = monitorMgr.getMonitorsByUserId(loginUser.getId());
		if (CollectionUtils.isNotEmpty(monitors)) {
			for (Monitor element : monitors) {
				if (element != null) {
					if (name.equals(element.getName())) {
						return ApplicaRuntime.responseJSON(-1, name + " 故障处理人已经存在", response);
					}
					if (cellphone.equals(element.getCellphone())) {
						return ApplicaRuntime.responseJSON(-1, cellphone + " 手机号码已经存在", response);
					}
					if (email.equals(element.getEmail())) {
						return ApplicaRuntime.responseJSON(-1, email + " 邮箱地址重复", response);
					}
				}
			}
		}
		
		Monitor monitor = new Monitor();
		monitor.setName(name);
		monitor.setCellphone(cellphone);
		monitor.setEmail(email);
		monitor.setWeixin(weixin == null ? "" : weixin);
		monitor.setUserId(loginUser.getId());
		monitor.setCreateTime(System.currentTimeMillis());
		
		monitor = monitorMgr.saveMonitor(monitor);
		return ApplicaRuntime.responseJSON(1, JSONObject.fromObject(monitor).toString(), response);
	}
	
	@RequestMapping("/user/delmonitor")
	public String delMonitor(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long id = CommonUtil.getLong(request.getParameter("id"));
		if (id == null) {
			return ApplicaRuntime.responseJSON(-1, "id为空", response);
		}
		Monitor monitor = monitorMgr.getMonitor(id);
		if (monitor == null) {
			return ApplicaRuntime.responseJSON(-3, "监控人不存在", response);
		} else if (!monitor.getUserId().equals(loginUser.getId())) {
			return ApplicaRuntime.responseJSON(-4, "权限错误", response);
		}
		
		
		// 删除故障处理人所在的组
		Long mgmCount = monitorGroupMonitorMgr.countMonitorGroupMonitorsByMonitorId(id);
		for (long p = 1; p <= CommonUtil.getTotalPage(mgmCount, CommonUtil.pageSize); p++) {
			List<Long> monitorGroupMonitorIds = monitorGroupMonitorMgr.getMonitorGroupMonitorIdsByMonitorId(id, p);
			for (Long eleId : monitorGroupMonitorIds) {
				if (eleId != null)
					monitorGroupMonitorMgr.deleteMonitorGroupMonitor(eleId);
			}
		}
		
		// 删除故障处理人
		monitorMgr.delMonitor(id);

		return ApplicaRuntime.responseJSON(1, "删除成功", response);
	}
	
	@RequestMapping("/user/updatemonitor")
	public String updateMonitor(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long id = CommonUtil.getLong(request.getParameter("id"));
		String name = request.getParameter("name");
		String cellphone = request.getParameter("cellphone");
		String email = request.getParameter("email");
		String youni = request.getParameter("youni");
		String weixin = request.getParameter("weixin");
		if (id == null) {
			return ApplicaRuntime.responseJSON(-1, "监控人id为空", response);
		}
		Monitor monitor = monitorMgr.getMonitor(id);
		if (monitor == null) {
			return ApplicaRuntime.responseJSON(-2, "监控人不存在", response);
		} else if (!monitor.getUserId().equals(loginUser.getId())) {
			return ApplicaRuntime.responseJSON(-4, "权限错误", response);
		}
		
		if (StringUtils.isEmpty(name)) {
			return ApplicaRuntime.responseJSON(-1, "姓名为空", response);
		}
		if (StringUtils.isEmpty(cellphone)) {
			return ApplicaRuntime.responseJSON(-1, "手机号码为空", response);
		}
		if (StringUtils.isEmpty(email)) {
			return ApplicaRuntime.responseJSON(-1, "邮箱地址为空", response);
		}
		
		if (StringUtils.isEmpty(youni)) {
			youni = cellphone;
		}
		
		// check name replication
		List<Monitor> monitors = monitorMgr.getMonitorsByUserId(loginUser.getId());
		if (CollectionUtils.isNotEmpty(monitors)) {
			for (Monitor element : monitors) {
				if (element != null && !element.getId().equals(monitor.getId())) {
					if (name.equals(element.getName())) {
						return ApplicaRuntime.responseJSON(-1, name + " 故障处理人已经存在", response);
					}
					if (cellphone.equals(element.getCellphone())) {
						return ApplicaRuntime.responseJSON(-1, cellphone + " 手机号码已经存在", response);
					}
					if (email.equals(element.getEmail())) {
						return ApplicaRuntime.responseJSON(-1, email + " 邮箱地址重复", response);
					}
				}
			}
		}
		
		monitor.setName(name);
		monitor.setCellphone(cellphone);
		monitor.setEmail(email);
		monitor.setWeixin(weixin);
		monitor.setUserId(loginUser.getId());
		
		monitor = monitorMgr.updateMonitor(monitor);
		return ApplicaRuntime.responseJSON(1, "修改成功", response);
	}
	
	@RequestMapping("/user/addmonitorgroup")
	public String saveMonitorGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		String gname = request.getParameter("gname");
		if (StringUtils.isEmpty(gname)) {
			return ApplicaRuntime.responseJSON(-1, "组名为空", response);
		}
		List<MonitorGroup> groups = userGroupMgr.getFaultHandleGroups(loginUser.getId());
		if (CollectionUtils.isNotEmpty(groups)) {
			for (MonitorGroup element: groups) {
				if (element != null && element.getGroupName().equals(gname)) {
					return ApplicaRuntime.responseJSON(-3, "组名重复", response);
				}
			}
		}
		
		MonitorGroup group = new MonitorGroup();
		group.setGroupName(gname);
		group.setUserId(loginUser.getId());
		group.setCreateTime(System.currentTimeMillis()); 
		group = userGroupMgr.saveUserGroup(group);
		
		return ApplicaRuntime.responseJSON(1, "添加分组成功", response);
	}

	@RequestMapping("/user/renamemonitorgroup")
	public String renameMonitorGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));
		String gname = request.getParameter("gname");
		if (StringUtils.isEmpty(gname)) {
			return ApplicaRuntime.responseJSON(-1, "组名为空", response);
		}

		Long loginUserId = loginUser.getId();
		MonitorGroup group = userGroupMgr.getMonitorGroup(gid);
		if (group == null) {
			return ApplicaRuntime.responseJSON(-2, "报警组不存在", response);
		} else if (!group.getUserId().equals(loginUserId)) {
			return ApplicaRuntime.responseJSON(-4, "权限错误", response);
		} else if (group.getType() == MonitorGroup.TYPE_DEFAULT) {
			return ApplicaRuntime.responseJSON(-4, "不能修改默认报警组名称", response);
		}


		// 检查 重名
		List<MonitorGroup> groups = userGroupMgr.getFaultHandleGroups(loginUserId);
		if (CollectionUtils.isNotEmpty(groups)) {
			for (MonitorGroup element : groups) {
				if (!element.getId().equals(gid) && element.getGroupName().equals(gname)) {
					return ApplicaRuntime.responseJSON(-3, "已存在该报警组", response);
				}
			}
		}
		
		group.setGroupName(gname);
		userGroupMgr.updateMonitorGroup(group);
		return ApplicaRuntime.responseJSON(1, "修改分组名成功", response);
	}
	
	@RequestMapping("/user/delmonitorgroup")
	public String delMonitorGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));

		Long loginUserId = loginUser.getId();
		MonitorGroup group = userGroupMgr.getMonitorGroup(gid);
		if (group == null) {
			return ApplicaRuntime.responseJSON(-2, "报警组不存在", response);
		} else if (!group.getUserId().equals(loginUserId)) {
			return ApplicaRuntime.responseJSON(-4, "权限错误", response);
		} else if (group.getType() == MonitorGroup.TYPE_DEFAULT) {
			return ApplicaRuntime.responseJSON(-4, "不能删除默认报警组", response);
		}

		userGroupMgr.deleteMonitorGroup(gid);
		return ApplicaRuntime.responseJSON(1, "删除分组名成功", response);
	}
	
	@RequestMapping("/config/setmonitorgroup")
	public String setMonitorGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));
		Long loginUserId = loginUser.getId();
		MonitorGroup group = userGroupMgr.getMonitorGroup(gid);
		if (group == null) {
			return null; // FIXME!!
		} else if (!group.getUserId().equals(loginUserId)) {
			return null; // FIXME!! 需要一个错误页
		}
		
		long currentPage = 1L;
		long monitorCount = monitorMgr.countMonitorsByUserId(loginUserId);
		long totalPage = CommonUtil.getTotalPage(monitorCount, CommonUtil.pageSize);
		List<Monitor> monitors = monitorMgr.getFaultHandleUsers(loginUserId, currentPage);
		
		// get monitorMap
		HashMap<Long, Byte> monitorMap = new HashMap<Long, Byte>();
		Long totalMgmCount = monitorGroupMonitorMgr.countMonitorsByGroupId(gid);
		Long totalMgmPage = CommonUtil.getTotalPage(totalMgmCount, CommonUtil.pageSize);
		for (long tp = 1; tp <= totalMgmPage; tp++) {
			List<MonitorGroupMonitor> mgms = monitorGroupMonitorMgr.getMonitorsByGroupId(gid, tp);
			if (CollectionUtils.isNotEmpty(mgms)) {
				for (MonitorGroupMonitor mgm : mgms) {
					monitorMap.put(mgm.getMonitorId(), mgm.getAlarmChannel());
				}
			}
		}

		request.setAttribute("monitorGroup", group);
		request.setAttribute("monitors", monitors);
		request.setAttribute("monitorMap", monitorMap);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("currentPage", currentPage);
		
		request.setAttribute("menu", "config");
		return "config/config_setmonitorgroup";
	}
	
	@RequestMapping("/config/reconfigmonitorgroup")
	public String reconfigMonitorGroup(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long gid = CommonUtil.getLong(request.getParameter("gid"));
		String data = request.getParameter("data"); // 2@0,1;3@0,1;4@2

		MonitorGroup mg = userGroupMgr.getMonitorGroup(gid);
		if (mg == null) {
			return ApplicaRuntime.responseJSON(-4, "该分组不存在", response);
		} else if (!mg.getUserId().equals(loginUser.getId())) {
			return ApplicaRuntime.responseJSON(-4, "权限错误", response);
		}
		if (data != null) data = URLDecoder.decode(data, "UTF-8");
		String[] elements = data.split(";");
		monitorGroupMonitorMgr.deleteMonitorGroupMonitorByGid(gid);
		if (elements != null && elements.length > 0) {
			for (int i = 0; i < Math.min(10, elements.length); i++) {
				String element = elements[i];
				String[] arr = element.split("@");
				if (arr != null && arr.length == 2) {
					Long monitorId = CommonUtil.getLong(arr[0]);
					Monitor monitor = monitorMgr.getMonitor(monitorId);
					if (monitor == null) continue;

					String[] channels = arr[1].split(",");
					if (channels != null && channels.length > 0) {
						MonitorGroupMonitor mgm = new MonitorGroupMonitor();
						for (int j = 0; j < channels.length; j++) {
							byte channel = Byte.parseByte(channels[j]);
							mgm.setAlarmChannel((byte) (mgm.getAlarmChannel() + channel));
						}
						mgm.setMonitorGroupId(gid);
						mgm.setMonitorId(monitorId);
						mgm.setCreateTime(System.currentTimeMillis());
						
						monitorGroupMonitorMgr.saveMonitorGroupMonitor(mgm);
					}
				}
			}
		}
		
		if (elements != null && elements.length > 10) {
			return ApplicaRuntime.responseJSON(1, "修改报警组设置成功，成功保存前10名报警人", response);
		} else {
			return ApplicaRuntime.responseJSON(1, "修改报警组设置成功", response);
		}
	}
	
	@RequestMapping("/config/getmonitors")
	public String getMonitors(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return ApplicaRuntime.responseJSON(-2, "未登录", response);
		}
		Long loginUserId = loginUser.getId();
		Long currentPage = CommonUtil.getPage(request.getParameter("p"));

		List<Monitor> monitors = monitorMgr.getFaultHandleUsers(loginUserId, currentPage);
		JSONObject json = new JSONObject();
		json.put("monitors", monitors);

		return ApplicaRuntime.responseJSON(1, json.toString(), response);
	}



}
