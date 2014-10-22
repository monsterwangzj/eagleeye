package com.chengyi.eagleeye.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chengyi.eagleeye.model.AlarmHistory;
import com.chengyi.eagleeye.model.BreakDownHistory;
import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@Controller
public class AlarmController extends BaseController {
	private static Logger logger = Logger.getLogger(AlarmController.class);

	@Autowired
	private DaoImpl dao;

	@RequestMapping("/alarm/messages")
	public String alarmMessages(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		String daysMode = request.getParameter("mode");
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		if (currentPage < 0) {
			currentPage = 1;
		}
		Long loginUserId = loginUser.getId();
		if (StringUtils.isEmpty(daysMode)) {
			daysMode = "7";
		}
		if (!daysMode.equals("30") && !daysMode.equals("-1") && !daysMode.equals("0")) {
			daysMode = "7";
		}
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		String start = "", end = "";
		long startTime = 0L, endTime = 0L;
		if (daysMode.equals("7")) {
			start = ApplicaRuntime.getLastNDaysStartDate(6);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else if (daysMode.equals("30")) {
			start = ApplicaRuntime.getLastNDaysStartDate(29);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else if (daysMode.equals("0")) { // 全年
			start = ApplicaRuntime.getLastNDaysStartDate(364);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else { // 自定义时间段 TODO:
			start = startDay + "00";
			end = endDay + "00";
			request.setAttribute("last", "-1");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		try {
			startTime = sdf.parse(start).getTime();
			endTime = sdf.parse(end).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<AlarmHistory> alarms = alarmHistoryMgr.getAlarmHistorysByUserIdnCreateTime2(loginUserId, startTime, endTime, currentPage);
		Long totalCount = alarmHistoryMgr.countAlarmHistorysByUserIdnCreateTime2(loginUserId, startTime, endTime);
		
		Map<Long, Item> itemMap = new HashMap<Long, Item>();
		if (CollectionUtils.isNotEmpty(alarms)) {
			for (AlarmHistory alarm : alarms) {
				Long itemId = alarm.getItemId();
				Item item = itemMap.get(itemId);
				if (item == null) {
					item = itemMgr.getItem(itemId);
					itemMap.put(itemId, item);
				}
			}
		}
		request.setAttribute("daysMode", daysMode);
		request.setAttribute("alarms", alarms);
		request.setAttribute("itemMap", itemMap);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("start", startDay);
		request.setAttribute("end", endDay);
		request.setAttribute("totalPage", CommonUtil.getTotalPage(totalCount, CommonUtil.pageSize));
		request.setAttribute("menu", "alarm");
		return "alarm/messages";
	}

	@RequestMapping("/alarm/history")
	public String alarmHistory(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		String daysMode = request.getParameter("mode");
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		if (currentPage < 0) {
			currentPage = 1;
		}
		Long loginUserId = loginUser.getId();
		if (StringUtils.isEmpty(daysMode)) {
			daysMode = "7";
		}
		if (!daysMode.equals("30") && !daysMode.equals("-1") && !daysMode.equals("0")) {
			daysMode = "7";
		}
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		String start = "", end = "";
		long startTime = 0L, endTime = 0L;
		if (daysMode.equals("7")) {
			start = ApplicaRuntime.getLastNDaysStartDate(6);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else if (daysMode.equals("30")) {
			start = ApplicaRuntime.getLastNDaysStartDate(29);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else if (daysMode.equals("0")) { // 全年
			start = ApplicaRuntime.getLastNDaysStartDate(364);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else { // 自定义时间段 TODO:
			start = startDay + "00";
			end = endDay + "00";
			request.setAttribute("last", "-1");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		try {
			startTime = sdf.parse(start).getTime();
			endTime = sdf.parse(end).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<BreakDownHistory> alarms = breakDownHistoryMgr.getBreakDownHistorysByUserIdnCreateTime(loginUserId, startTime, endTime, currentPage);
		Long totalCount = breakDownHistoryMgr.countBreakDownHistorysByUserIdnCreateTime(loginUserId, startTime, endTime);
		
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
		request.setAttribute("daysMode", daysMode);
		request.setAttribute("alarms", alarms);
		request.setAttribute("itemMap", itemMap);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("start", startDay);
		request.setAttribute("end", endDay);
		request.setAttribute("totalPage", CommonUtil.getTotalPage(totalCount, CommonUtil.pageSize));
		request.setAttribute("menu", "alarm");
		return "alarm/history";
	}

	@RequestMapping("/alarm/stat")
	public String alarmStat(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long loginUserId = loginUser.getId();
		String daysMode = request.getParameter("mode");
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		if (currentPage < 0) {
			currentPage = 1;
		}
		long totalPage = 1;
		if (StringUtils.isEmpty(daysMode)) {
			daysMode = "7";
		}
		if (!daysMode.equals("30") && !daysMode.equals("-1") && !daysMode.equals("0")) {
			daysMode = "7";
		}
		
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		String start = "", end = "";
		int head = 0, tail = 0;
		long startTime = 0L, endTime = 0L;
		if (daysMode.equals("7")) {
			head = 6;
			tail = -1;
		} else if (daysMode.equals("30")) {
			head = 29;
			tail = -1;
		} else if (daysMode.equals("0")) { // 全年
			head = 364;
			tail = -1;
		} else { // 自定义时间段 TODO:
			String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			head = ApplicaRuntime.daysDiff(startDay, nowDate, "yyyyMMdd");
			tail = ApplicaRuntime.daysDiff(endDay, nowDate, "yyyyMMdd") - 1;
			request.setAttribute("last", "-1");
		}
		totalPage = (head - tail) % CommonUtil.pageSize == 0 ? ((head - tail) / CommonUtil.pageSize) : ((head - tail) / CommonUtil.pageSize + 1);
		request.setAttribute("daysMode", daysMode);
		
		int i = (int) ((currentPage  - 1) * CommonUtil.pageSize + 9);
		if ((head - tail - 1) < i) i = head - tail - 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map<String, Map<Byte, Integer>> statMap = new TreeMap<String, Map<Byte, Integer>>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				if (o1 == null || o2 == null)
                    return 0; 
				 return String.valueOf(o2).compareTo(String.valueOf(o1));
			}
		});
		do {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -i - (1 + tail));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			Date date = cal.getTime();
			long startTime1 = date.getTime();
			String datestr = sdf.format(date);
			
			cal.add(Calendar.DAY_OF_MONTH, +1);
			date = cal.getTime();
			System.out.println(date);
			long endTime1 = date.getTime();
		
			Map<Byte, Integer> map = alarmHistoryMgr.countAlarmsByDatenChannelnUserId(startTime1, endTime1, loginUserId);
			if (loginUserId == 20L) {
				map = alarmHistoryMgr.countAlarmsByDatenChannel(startTime1, endTime1);
			}
			statMap.put(datestr, map);
			logger.info(datestr + "\t" + map);
			
			i--;
		} while (i >= (currentPage  - 1) * CommonUtil.pageSize);
		
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("start", startDay);
		request.setAttribute("end", endDay);
		request.setAttribute("statMap", statMap);
		request.setAttribute("menu", "alarm");
		return "alarm/stat";
	}

	@RequestMapping("/alarm/notresume")
	public String notresume(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User loginUser = getUserFromCookie(model, request, response);
		if (loginUser == null) {
			return "redirect:/login.do";
		}
		Long loginUserId = loginUser.getId();
		
		String daysMode = request.getParameter("mode");
		long currentPage = CommonUtil.getPage(request.getParameter("p"));
		if (currentPage < 0) {
			currentPage = 1;
		}
		if (StringUtils.isEmpty(daysMode)) {
			daysMode = "7";
		}
		if (!daysMode.equals("30") && !daysMode.equals("-1") && !daysMode.equals("0")) {
			daysMode = "7";
		}
		String startDay =  request.getParameter("start");
		String endDay = request.getParameter("end");
		String start = "", end = "";
		long startTime = 0L, endTime = 0L;
		if (daysMode.equals("7")) {
			start = ApplicaRuntime.getLastNDaysStartDate(6);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else if (daysMode.equals("30")) {
			start = ApplicaRuntime.getLastNDaysStartDate(29);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else if (daysMode.equals("0")) { // 全年
			start = ApplicaRuntime.getLastNDaysStartDate(364);
			end = ApplicaRuntime.getLastNDaysStartDate(-1);
		} else { // 自定义时间段 TODO:
			start = startDay + "00";
			end = endDay + "00";
			request.setAttribute("last", "-1");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		try {
			startTime = sdf.parse(start).getTime();
			endTime = sdf.parse(end).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<BreakDownHistory> alarms = breakDownHistoryMgr.getNotResumeItemsByUserId(loginUserId, startTime, endTime, currentPage);
		Long totalCount = breakDownHistoryMgr.countNotResumeItemsByUserId(loginUserId, startTime, endTime);
		
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
		request.setAttribute("daysMode", daysMode);
		request.setAttribute("alarms", alarms);
		request.setAttribute("itemMap", itemMap);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("start", startDay);
		request.setAttribute("end", endDay);
		request.setAttribute("totalPage", CommonUtil.getTotalPage(totalCount, CommonUtil.pageSize));
		request.setAttribute("menu", "alarm");
		
		return "alarm/notresume";
	}

}
