package com.chengyi.eagleeye.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.ItemGroup;
import com.chengyi.eagleeye.model.ItemMonitorGroup;
import com.chengyi.eagleeye.model.Monitor;
import com.chengyi.eagleeye.model.MonitorGroup;
import com.chengyi.eagleeye.model.MonitorGroupMonitor;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.model.assist.HttpOption;
import com.chengyi.eagleeye.model.assist.PingOption;
import com.chengyi.eagleeye.model.message.ping.PingMessageStat;
import com.chengyi.eagleeye.network.http.StatHttpMessage;
import com.chengyi.eagleeye.network.http.HttpParam.HttpRequestMethod;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.DateUtil;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class PingItemController extends BaseController {
	private static Logger logger = Logger.getLogger(PingItemController.class);
	
	@RequestMapping("/pingitem/save")
	public String saveItem(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long loginUserId = loginUser.getId();
		
		String itemName = request.getParameter("itemname"); // 项目名称
		try {
			itemName = new String(itemName.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Long gid = CommonUtil.getLong(request.getParameter("gid")); // 项目分组id
		String url = request.getParameter("url"); // 监控url地址
		try {
			url = url.trim();
			url = new String(url.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String monitorFreq = request.getParameter("monitorfreq"); // 监控频率
		String retrytimes = request.getParameter("retrytimes"); // 连续报警提醒

		String retryInterval = request.getParameter("retryinterval");
		String continuousReminder = request.getParameter("continuousreminder");

		String serverIps = request.getParameter("serverips");
		
		Item item = new Item();
		item.setName(itemName);
		item.setUserId(loginUserId);
		if (gid != null) item.setGid(gid);
		item.setUri(url);
		item.setType(Item.TYPE_PING);
		
		PingOption httpOption = new PingOption();
		
		
		if (StringUtils.isEmpty(retryInterval)) retryInterval = "1";
		item.setRetryInterval(60 * Integer.parseInt(retryInterval));
		
		if (StringUtils.isNotEmpty(continuousReminder)) item.setContinuousReminder(60 * Integer.parseInt(continuousReminder));
		else item.setContinuousReminder(Integer.MAX_VALUE);
		
		if (StringUtils.isEmpty(retrytimes)) retrytimes = "3";
		item.setRetryTimes(Integer.parseInt(retrytimes));
		
		if (StringUtils.isEmpty(monitorFreq)) monitorFreq = "60";
		item.setMonitorFreq(Integer.parseInt(monitorFreq));
		
		httpOption.setServerIps(serverIps);
		item.setOptions(JSONObject.fromObject(httpOption).toString());
		item.setStatus(Item.STATUS_NORMAL);
		item.setCreatetime(System.currentTimeMillis());
		Item nitem = itemMgr.saveItem(item);
		logger.info("-----------------------" + nitem);
		
		
		// 故障分组
		Long mgid = CommonUtil.getLong(request.getParameter("mgid")); // 报警组id
		MonitorGroup mg = monitorGroupMgr.getMonitorGroup(mgid);
		if (mg != null && mg.getUserId().equals(loginUserId)) {
			List<ItemMonitorGroup> imgs = itemMonitorGroupMgr.getItemMonitorGroupsByItemId(item.getId());
			if (CollectionUtils.isNotEmpty(imgs)) {
				for (int i = 0;i < imgs.size();i++) {
					ItemMonitorGroup img = imgs.get(i);
					if (i == 0) {
						img.setMonitorGroupId(mgid);
						img.setCreatetime(System.currentTimeMillis());
						itemMonitorGroupMgr.updateItemMonitorGroup(img);
					} else {
						itemMonitorGroupMgr.deleteItemMonitorGroup(img.getId());
					}
				}
			} else {
				ItemMonitorGroup img = new ItemMonitorGroup();
				img.setItemId(item.getId());
				img.setMonitorGroupId(mgid);
				img.setUserId(loginUserId);
				img.setCreatetime(System.currentTimeMillis());
				itemMonitorGroupMgr.saveItemMonitorGroup(img);
			}
		}
		
		
		com.chengyi.eagleeye.patrol.Monitor.needRefresh = true;
		return "redirect:/monitor/index.do";
	}

	@RequestMapping("/pingitem/edit")
	public String editItem(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		
		Long itemId = CommonUtil.getLong(request.getParameter("id"));
		
		Item item = itemMgr.getItem(itemId);
		request.setAttribute("item", item);
		
		// 列出所有项目分组
		Long loginUserId = loginUser.getId();
		List<ItemGroup> itemGroups = itemGroupMgr.getItemGroupsByUserId(loginUserId);
		request.setAttribute("itemGroups", itemGroups);

		// 列出当前报警组
		MonitorGroup currentMonitorGroup = null;
		List<ItemMonitorGroup> imgs = itemMonitorGroupMgr.getItemMonitorGroupsByItemId(item.getId());
		if (CollectionUtils.isNotEmpty(imgs)) {
			ItemMonitorGroup img = imgs.get(0);
			if (img != null) {
				currentMonitorGroup = monitorGroupMgr.getMonitorGroup(img.getMonitorGroupId());
			}
		}
		request.setAttribute("currentMonitorGroup", currentMonitorGroup);

		
		// 列出所有报警组，和当前报警组下的用户列表
		List<MonitorGroup> monitorGroups = monitorGroupMgr.getFaultHandleGroups(loginUserId);
		request.setAttribute("monitorGroups", monitorGroups);
		if (currentMonitorGroup != null) {
			List<MonitorGroupMonitor> mgms = monitorGroupMonitorMgr.getMonitorsByGroupId(currentMonitorGroup.getId(), 1L);
			Long mgmsSize = monitorGroupMonitorMgr.countMonitorsByGroupId(currentMonitorGroup.getId());
			
			HashMap<Long, Monitor> currentMonitorsMap = new HashMap<Long, Monitor>();
			for (MonitorGroupMonitor mgm : mgms) {
				Monitor m = monitorMgr.getMonitor(mgm.getMonitorId());
				currentMonitorsMap.put(mgm.getId(), m);
			}
			
			request.setAttribute("mgms", mgms);
			request.setAttribute("currentMonitorsMap", currentMonitorsMap);
			request.setAttribute("mgmsSize", mgmsSize);
		}
		request.setAttribute("menu", "monitor");
		return "item//ping/edititem";
	}

	/**
	 * 修改监控项目
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/pingitem/update")
	public String updateItem(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long itemId = CommonUtil.getLong(request.getParameter("id"));
		Item item = itemMgr.getItem(itemId);
		String itemName = request.getParameter("itemname"); // 项目名称
		try {
			itemName = new String(itemName.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Long gid = CommonUtil.getLong(request.getParameter("gid")); // 项目分组id
		String url = request.getParameter("url"); // 监控url地址
		String monitorFreq = request.getParameter("monitorfreq"); // 监控频率
		String retrytimes = request.getParameter("retrytimes"); // 连续报警提醒
	
//		String retryInterval = request.getParameter("retryinterval");
		String continuousReminder = request.getParameter("continuousreminder");
		String method = request.getParameter("httpmethod");
		Integer httpTimeout = CommonUtil.getInteger(request.getParameter("timeout"));
		if (httpTimeout == null || httpTimeout <= 0 || httpTimeout > 120000) {
			httpTimeout = HttpOption.ITEM_HTTPTIMEOUT_DEFAULT;
		}
		String resultMatchPattern = request.getParameter("resultmatchpattern");
		String resultMatchPatternStatus = request.getParameter("ntmothod");
	
		String cookies = request.getParameter("cookies");
		String header = request.getParameter("httpheader");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String serverIps = request.getParameter("serverips");

		
		item.setName(itemName);
		item.setUserId(loginUser.getId());
		item.setGid(gid);
		item.setUri(url);
		if (method == null) method = HttpRequestMethod.GET.toString();
		HttpOption httpOption = new HttpOption();
		httpOption.setHttpMethod(method);
		httpOption.setHttpTimeout(httpTimeout);
		
		httpOption.setResultMatchPattern(resultMatchPattern);
		if (StringUtils.isEmpty(resultMatchPatternStatus)) resultMatchPatternStatus = "1";
		httpOption.setResultMatchPatternStatus(Byte.parseByte(resultMatchPatternStatus));
		
		httpOption.setUsername(username);
		httpOption.setPassword(password);
		httpOption.setCookies(cookies);
		httpOption.setHttpHeader(header);
		
//		if (StringUtils.isEmpty(retryInterval)) retryInterval = "1";
//		item.setRetryInterval(60 * Integer.parseInt(retryInterval));
		
		item.setContinuousReminder(Integer.parseInt(continuousReminder));
		
		if (StringUtils.isEmpty(retrytimes)) retrytimes = "3";
		item.setRetryTimes(Integer.parseInt(retrytimes));
		
		if (StringUtils.isEmpty(monitorFreq)) monitorFreq = "60"; // 60s for default
		item.setMonitorFreq(Integer.parseInt(monitorFreq));
		
		httpOption.setServerIps(serverIps);
		item.setOptions(JSONObject.fromObject(httpOption).toString());
		item.setLastmodified(System.currentTimeMillis());
//		Item nitem = itemMgr.saveItem(item);
		item = itemMgr.updateItem(item);
		logger.info("-----------------------" + item);
		
		// 故障分组
		Long mgid = CommonUtil.getLong(request.getParameter("mgid")); // 报警组id
		if (mgid != null) {
			MonitorGroup mg = monitorGroupMgr.getMonitorGroup(mgid);
			if (mg != null && mg.getUserId().equals(loginUser.getId())) {
				List<ItemMonitorGroup> imgs = itemMonitorGroupMgr.getItemMonitorGroupsByItemId(item.getId());
				if (CollectionUtils.isNotEmpty(imgs)) {
					for (int i = 0;i < imgs.size();i++) {
						ItemMonitorGroup img = imgs.get(i);
						if (i == 0) {
							img.setMonitorGroupId(mgid);
							img.setCreatetime(System.currentTimeMillis());
							itemMonitorGroupMgr.updateItemMonitorGroup(img);
						} else {
							itemMonitorGroupMgr.deleteItemMonitorGroup(img.getId());
						}
					}
				} else {
					ItemMonitorGroup img = new ItemMonitorGroup();
					img.setItemId(item.getId());
					img.setMonitorGroupId(mgid);
					img.setUserId(loginUser.getId());
					img.setCreatetime(System.currentTimeMillis());
					itemMonitorGroupMgr.saveItemMonitorGroup(img);
				}
			}
		}

		// 重新分配任务
		com.chengyi.eagleeye.patrol.Monitor.needRefresh = true;
	
		return "redirect:/index.do";
	}

	@RequestMapping("/pingitem/usability2")
	public String usability2(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		
		Item item = null;
		Long itemId = CommonUtil.getLong(request.getParameter("itemId"));
		if (itemId != null) {
			item = itemMgr.getItem(itemId);
		}
		if (item == null) {
			return "error"; // TODO: 错误页
		}
		String last = request.getParameter("last");
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		if (StringUtils.isEmpty(last)) {
			last = "0";
		}
		if (!last.equals("0") && !last.equals("1") && !last.equals("7") && !last.equals("15") && !last.equals("30")) {
			if (StringUtils.isEmpty(startDay)) {
				last = "0";
			} else {
				last = "-1";
			}
		}
		
		request.setAttribute("item", item);
		request.setAttribute("last", last);
		
		request.setAttribute("menu", "monitor");
		return "item/ping/usability";
	}

	@RequestMapping("/pingitem/detailusability2")
	public String detailusability2(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return null;
		}
		
		Item item = null;
		Long itemId = CommonUtil.getLong(request.getParameter("itemId"));
		if (itemId != null) {
			item = itemMgr.getItem(itemId);
		}
		if (item == null) {
			return null;
		}
		request.setAttribute("item", item);
		String last = request.getParameter("last");
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		
		String start = "";
		String end = "";
		if (StringUtils.isEmpty(last)) { // 今日
			start = ApplicaRuntime.getLastNDaysStartDate(0) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "0");
		} else if (last.equals("1")) { // 昨日
			start = ApplicaRuntime.getLastNDaysStartDate(1) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(1) + "00";
			request.setAttribute("last", "1");
		} else if (last.equals("7")) { // 最近7天
			start = ApplicaRuntime.getLastNDaysStartDate(6) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "7");
		} else if (last.equals("15")) { // 最近15天
			start = ApplicaRuntime.getLastNDaysStartDate(14) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "15");
		} else if (last.equals("30")) { // 最近30天
			start = ApplicaRuntime.getLastNDaysStartDate(29) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "30");
		} else { // 自定义时间段 TODO:
			start = startDay + "0000";
			end = endDay + "0000";
			request.setAttribute("last", "-1");
		}
		if (CommonUtil.getLong(end) > CommonUtil.getLong(DateUtil.format(new Date(System.currentTimeMillis() + 3600 * 1000), StatHttpMessage.mFormatDateTime) + "00")) {
			end = DateUtil.format(new Date(System.currentTimeMillis() + 3600 * 1000), StatHttpMessage.mFormatDateTime) + "00";
		}
		
		Map<String, JSONObject> dataMap = new LinkedHashMap<String, JSONObject>();
		int[] classCountArr = new int[] { 0, 0, 0, 0, 0, 0 };
		Double totalUsability = null, totalUnUsablity = null;
		List<PingMessageStat> stats = messageMgr.getPingMessageStatsByDateInterval(itemId, Long.parseLong(start.substring(0, start.length() - 2)), Long.parseLong(end.substring(0, end.length() - 2)));
		if (last == null || !last.equals("1")) { // 不是昨日
			long hourStartTimeMillis = getCurrentHourBegin();
			PingMessageStat lastHmStat = messageMgr.getPingMessageStatByDateInterval(itemId, hourStartTimeMillis, hourStartTimeMillis + StatHttpMessage.msInAnHour);
			if (lastHmStat != null) {
				stats.add(lastHmStat);
			}
		}
		
		if (CollectionUtils.isNotEmpty(stats)) {
			long totalAccessCount = 0L, tmpTotalCount = 0L;
			double tmpUsability = 0.0f;
			for (PingMessageStat stat : stats) {
				double usability = 100.0 - stat.getLostPercent();
				totalAccessCount += stat.getTotalAccessCount();
				
				tmpTotalCount++;
				tmpUsability += usability;
				
				String hour = stat.getCreateTime() + "";

				JSONObject jobj = dataMap.get(hour);
				if (jobj == null) {
					jobj = new JSONObject();

					jobj.put("usability", usability);
					jobj.put("count", 1);
				} else {
					int preCount = jobj.getInt("count");
					double preUsability = jobj.getDouble("usability");
					double newUsability = (preCount * preUsability + usability) / (preCount + 1);

					jobj.put("usability", newUsability);
					jobj.put("count", preCount + 1);
				}
				dataMap.put(hour, jobj);
			}
			if (totalAccessCount != 0L) {
				totalUsability = tmpUsability / tmpTotalCount;
			}
		}

		if (totalUsability != null) {
			totalUsability = CommonUtil.getNpDouble(totalUsability, 3);
			totalUnUsablity = CommonUtil.getNpDouble(100 - totalUsability, 3);
		} else {
			totalUsability = 0.0;
			totalUnUsablity = 0.0;
		}
		StringBuilder datas = new StringBuilder("[");
		if (dataMap.size() > 0) {
			long startL = CommonUtil.getLong(start.substring(0, start.length() - 2));
			long endL = CommonUtil.getLong(end.substring(0, end.length() - 2));
			for (long time = startL; time < endL;) {
				try {
					String timeStr = Long.toString(time);

					JSONObject jobj = dataMap.get(timeStr);
					if (jobj != null) {
						String usability = jobj.getString("usability");
						datas.append("[").append(ApplicaRuntime.transformStatTimeByHour(timeStr)).append(",").append(usability).append("]").append(",");
					} else {
						datas.append("[").append(ApplicaRuntime.transformStatTimeByHour(timeStr)).append(",").append("null").append("]").append(",");
					}

					Date datetmp = timeSdf.parse(timeStr);
					long timetmp = datetmp.getTime() + 3600 * 1000;
					time = CommonUtil.getLong(timeSdf.format(new Date(timetmp)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			datas.deleteCharAt(datas.length() - 1);
		}
		datas.append("]");
		JSONObject result = new JSONObject();
		result.put("data", datas.toString());
		result.put("totalUsability", totalUsability);
		result.put("totalUnUsablity", totalUnUsablity);
		
		return ApplicaRuntime.responseJSON(1, result.toString(), response);
	}
	
	private SimpleDateFormat timeSdf = new SimpleDateFormat("yyyyMMddHH");
	
	@RequestMapping("/pingitem/responsetime2")
	public String responsetime2(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}

		Item item = null;
		Long itemId = CommonUtil.getLong(request.getParameter("itemId"));
		if (itemId != null) {
			item = itemMgr.getItem(itemId);
		}
		if (item == null) {
			return "error"; // TODO: 错误页
		}
		request.setAttribute("item", item);

		String last = request.getParameter("last");
		String startDay = request.getParameter("start");
		String endDay = request.getParameter("end");
		
		boolean isHourUnit = true;
		String start = "";
		String end = "";
		if (StringUtils.isEmpty(last)) { // 今日
			isHourUnit = true;
			start = ApplicaRuntime.getLastNDaysStartDate(0) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "0");
		} else if (last.equals("1")) { // 昨日
			isHourUnit = true;
			start = ApplicaRuntime.getLastNDaysStartDate(1) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(1) + "00";
			request.setAttribute("last", "1");
		} else if (last.equals("7")) { // 最近7天
			start = ApplicaRuntime.getLastNDaysStartDate(6) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "7");
		} else if (last.equals("15")) { // 最近15天
			start = ApplicaRuntime.getLastNDaysStartDate(14) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "15");
		} else if (last.equals("30")) { // 最近30天
			start = ApplicaRuntime.getLastNDaysStartDate(29) + "00";
			end = ApplicaRuntime.getLastNDaysEndDate(0) + "00";
			request.setAttribute("last", "30");
		} else { // 自定义时间段 TODO:
			start = startDay + "0000";
			end = endDay + "0000";
			request.setAttribute("last", "-1");
		}
		if (CommonUtil.getLong(end) > CommonUtil.getLong(DateUtil.format(new Date(System.currentTimeMillis() + 3600 * 1000), StatHttpMessage.mFormatDateTime) + "00")) {
			end = DateUtil.format(new Date(System.currentTimeMillis() + 3600 * 1000), StatHttpMessage.mFormatDateTime) + "00";
		}
		
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		int[] classCountArr = new int[] { 0, 0, 0, 0, 0, 0 };
		long  avgCount = 0L;
		double totalMinTime = 0, totalAvgTime = 0L, totalMaxTime = 0;

		List<PingMessageStat> hmStats = messageMgr.getPingMessageStatsByDateInterval(itemId, Long.parseLong(start.substring(0, start.length() - 2)), Long.parseLong(end.substring(0, end.length() - 2)));
		if (last == null || !last.equals("1")) { // 不是昨日
			long hourStartTimeMillis = getCurrentHourBegin();
			PingMessageStat lastHmStat = messageMgr.getPingMessageStatByDateInterval(itemId, hourStartTimeMillis, hourStartTimeMillis + StatHttpMessage.msInAnHour);
			if (lastHmStat != null) {
				hmStats.add(lastHmStat);
			}
		}
		
		Map<String, JSONObject> dataMap = new LinkedHashMap<String, JSONObject>();
		if (CollectionUtils.isNotEmpty(hmStats)) {
			for (PingMessageStat hmStat : hmStats) {
				avgCount++;
				JSONObject jobj = new JSONObject();

				jobj.put("min", CommonUtil.getNpDouble(new Double(hmStat.getMinResponseTime()), 3));
				jobj.put("avg", CommonUtil.getNpDouble(new Double(hmStat.getAverageResponseTime()), 3));
				jobj.put("max", CommonUtil.getNpDouble(new Double(hmStat.getMaxResponseTime()), 3));

				if (hmStat.getMinResponseTime() > 0) {
					if (totalMinTime == 0L) {
						totalMinTime = hmStat.getMinResponseTime();
					}
					if (hmStat.getMinResponseTime() < totalMinTime) {
						totalMinTime = hmStat.getMinResponseTime();
					}
				}
				if (hmStat.getMaxResponseTime() > totalMaxTime) {
					totalMaxTime = hmStat.getMaxResponseTime();
				}
				totalAvgTime += hmStat.getAverageResponseTime();

				String keyele = Long.toString(hmStat.getCreateTime());
				dataMap.put(keyele, jobj);
				
				classCountArr[0] += hmStat.getClass1Count();
				classCountArr[1] += hmStat.getClass2Count();
				classCountArr[2] += hmStat.getClass3Count();
				classCountArr[3] += hmStat.getClass4Count();
				classCountArr[4] += hmStat.getClass5Count();
			}

		}

		long startL = CommonUtil.getLong(start.substring(0, start.length() - 2));
		long endL = CommonUtil.getLong(end.substring(0, end.length() - 2));
		for (long time = startL; time < endL;) {
			try {
				String timeStr = Long.toString(time);
				keyList.add(timeStr);

				JSONObject jobj = dataMap.get(timeStr);
				jsonList.add(jobj);

				Date datetmp = timeSdf.parse(timeStr);
				long timetmp = datetmp.getTime() + 3600 * 1000;
				time = CommonUtil.getLong(timeSdf.format(new Date(timetmp)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		request.setAttribute("totalMinTime", CommonUtil.getNpDouble(totalMinTime, 3));
		request.setAttribute("totalMaxTime", CommonUtil.getNpDouble(totalMaxTime, 3));
		request.setAttribute("totalAvgTime", CommonUtil.getNpDouble(avgCount == 0 ? 0 : totalAvgTime / avgCount, 3));
		request.setAttribute("keyList", keyList);
		request.setAttribute("jsonList", jsonList);
		request.setAttribute("classCountArr", classCountArr);
		request.setAttribute("isHourUnit", isHourUnit);
		
		request.setAttribute("menu", "monitor");
		return "item/ping/responsetime";
	}

	private long getCurrentHourBegin() {
		try {
			return DateUtil.parse(DateUtil.format(new Date(), StatHttpMessage.mFormatDateTime), StatHttpMessage.mFormatDateTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	@RequestMapping("/pingitem/dailystat2")
	public String dailystat2(Model model, HttpServletRequest request, HttpServletResponse response) {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		if (currentPage < 0) {
			currentPage = 1;
		}
		Item item = null;
		Long itemId = CommonUtil.getLong(request.getParameter("itemId"));
		if (itemId != null) {
			item = itemMgr.getItem(itemId);
		}
		if (item == null) {
			return "error"; // TODO: 错误页
		}
		request.setAttribute("item", item);
		String daysMode = request.getParameter("mode");
		if (StringUtils.isEmpty(daysMode) || (!daysMode.equals("30") && !daysMode.equals("15") && !daysMode.equals("-1"))) {
			daysMode = "7";
		}
		
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		int mode = CommonUtil.getInteger(daysMode);
		if (mode != -1) {
			endDay = ApplicaRuntime.getLastNDaysStartDate(-1) + "00";
			startDay = ApplicaRuntime.getLastNDaysStartDate(mode - 1) + "00";
		} else {
			endDay += "0000";
			startDay += "0000";
		}
		String format = "yyyyMMddHH";
		int totalCount = ApplicaRuntime.daysDiff(startDay, endDay, format);
		String pageEnd = ApplicaRuntime.getDayBeforeDate(endDay, (int) (currentPage - 1) * CommonUtil.pageSize, format) + "00";
		String pageStart = ApplicaRuntime.getDayBeforeDate(endDay, (int)(currentPage * CommonUtil.pageSize - 1), format) + "00";
		if (Long.parseLong(startDay) > Long.parseLong(pageStart)) {
			pageStart = startDay;
		}
		request.setAttribute("mode", daysMode);
		logger.info("pageStart:" + pageStart + ", pageEnd:" + pageEnd + ", daysDiff:" + totalCount);
		
		
		
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		Double totalUsability = 0.0;
		
		List<PingMessageStat> hmStats = messageMgr.getPingMessageStatsByDateInterval(itemId, Long.parseLong(pageStart.substring(0, pageStart.length() - 2)), Long.parseLong(pageEnd.substring(0, pageEnd.length() - 2)));
		long hourStartTimeMillis = getCurrentHourBegin();
		PingMessageStat lastHmStat = messageMgr.getPingMessageStatByDateInterval(itemId, hourStartTimeMillis, hourStartTimeMillis + StatHttpMessage.msInAnHour);
		if (lastHmStat != null) {
			hmStats.add(lastHmStat);
		}
		
		if (CollectionUtils.isNotEmpty(hmStats)) {
			long succAccessCount = 0L, totalAccessCount = 0L;
			LinkedHashMap<String, PingMessageStat> dayStatMap = new LinkedHashMap<String, PingMessageStat>();
			Map<String, Integer> dayStatCountMap = new HashMap<String, Integer>();
			for (PingMessageStat hmStat : hmStats) { // convert hour stat to day stat
				String createTime = hmStat.getCreateTime() + "";
				createTime = createTime.substring(0, createTime.length() - 2);
				String key = hmStat.getItemId() + "-" + createTime;
				
				Integer hourCount = dayStatCountMap.get(key) == null ? 0 : dayStatCountMap.get(key);
				dayStatCountMap.put(key, hourCount + 1);
				
				PingMessageStat ele = dayStatMap.get(key);
				if (ele == null) ele = new PingMessageStat();
				
				ele.setUserId(hmStat.getUserId());
				ele.setItemId(hmStat.getItemId());
				ele.setTotalAccessCount(ele.getTotalAccessCount() + hmStat.getTotalAccessCount());
				
				if (ele.getMinResponseTime() == 0L) {
					ele.setMinResponseTime(hmStat.getMinResponseTime());
				}
				if (hmStat.getMinResponseTime() < ele.getMinResponseTime()) {
					ele.setMinResponseTime(hmStat.getMinResponseTime());
				}
				ele.setAverageResponseTime((ele.getAverageResponseTime() * hourCount + hmStat.getAverageResponseTime()) / (hourCount + 1));
				ele.setLostPercent((ele.getLostPercent() * hourCount + hmStat.getLostPercent()) / (hourCount + 1));

				if (hmStat.getMaxResponseTime() > ele.getMaxResponseTime()) {
					ele.setMaxResponseTime(hmStat.getMaxResponseTime());
				}
				
				ele.setClass1Count(ele.getClass1Count() + hmStat.getClass1Count());
				ele.setClass2Count(ele.getClass2Count() + hmStat.getClass2Count());
				ele.setClass3Count(ele.getClass3Count() + hmStat.getClass3Count());
				ele.setClass4Count(ele.getClass4Count() + hmStat.getClass4Count());
				ele.setClass5Count(ele.getClass5Count() + hmStat.getClass5Count());
				
				dayStatMap.put(key, ele);
			}
			
			Set<String> keyset = dayStatMap.keySet();
			for (String key : keyset) {
				PingMessageStat stat = dayStatMap.get(key);
				
				double avgTime = CommonUtil.getNpDouble(new Double(stat.getAverageResponseTime()), 3);
				double usability = CommonUtil.getNpDouble(new Double(100.0 - stat.getLostPercent()), 3);
				
				totalAccessCount += stat.getTotalAccessCount();
				JSONObject jobj = new JSONObject();
				jobj.put("usability", usability);
				jobj.put("min", CommonUtil.getNpDouble(new Double(stat.getMinResponseTime()), 3));
				jobj.put("avg", avgTime);
				jobj.put("max", CommonUtil.getNpDouble(new Double(stat.getMaxResponseTime()), 3));
				
				String keyele = key.substring(key.length() - 8);
				keyList.add(keyele.substring(0, 4) + "/" + keyele.substring(4,6) + "/" + keyele.substring(6));
				jsonList.add(jobj);
			}
			if (totalAccessCount != 0L) {
				totalUsability = new Double((succAccessCount * 100.) / totalAccessCount);
			}
		}
		
		// get totalCount
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", CommonUtil.getTotalPage(totalCount, CommonUtil.pageSize));
		request.setAttribute("start", startDay.substring(0, startDay.length() - 2));
		request.setAttribute("end", endDay.substring(0, endDay.length() - 2));
		request.setAttribute("totalUsability", totalUsability);
		request.setAttribute("keyList", keyList);
		request.setAttribute("jsonList", jsonList);
		
		request.setAttribute("menu", "monitor");
		return "item/ping/dailystat";
	}
	
}
