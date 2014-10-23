<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ page
import="com.chengyi.eagleeye.network.nginx.*,com.chengyi.eagleeye.network.redis.*,com.chengyi.eagleeye.network.ping.*,java.text.NumberFormat,java.util.*,com.chengyi.eagleeye.network.http.HttpResult,com.chengyi.eagleeye.util.*,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils"
%><%
List<BreakDownHistory> alarms = (List<BreakDownHistory>) request.getAttribute("alarms");
Map<Long, Item> itemMap = (Map<Long, Item>) request.getAttribute("itemMap");

String daysMode = (String) request.getAttribute("daysMode");
String start = (String) request.getAttribute("start");
String end = (String) request.getAttribute("end");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");

String pageLinkBase = null;
if (daysMode == null) {
	pageLinkBase = "/alarm/history.do?p=";
} else {
	pageLinkBase = "/alarm/history.do?mode=" + daysMode;
	if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
		pageLinkBase += "&start=" + start + "&end=" + end;
	}
	pageLinkBase += "&p=";
}
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="/graphics/style.css" type="text/css" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<script language="JavaScript" src="/graphics/FusionCharts.js"></script>
<style>
#ccontainer th {
    color: #000000;
}
#ccontainer td {
    background: none repeat scroll 0 0 #FFFFFF;
    color: #000000;
}
</style>
<title>监控系统 故障报警记录</title>
</head>
<body>
	<div class="bg01">
		<div class="con">
			<%@ include file="/WEB-INF/view/common/header.jsp"%>

			<div class="w_960 pt">
				<div class="w_left">
					<div class="w_210 bc">
						<div class="title"><h4>报警</h4></div>
						<ul class="list">
							<li><span class="point">&nbsp;</span><a href="/alarm/messages.do">报警消息</a></li>
							<li class="current"><span class="point">&nbsp;</span><a href="/alarm/history.do">故障报警记录</a></li>
							<li><span class="point">&nbsp;</span><a href="/alarm/notresume.do">尚未恢复项目</a></li>
							<li class="noline"><span class="point">&nbsp;</span><a href="/alarm/stat.do">报警通知统计</a></li>
						</ul>
					</div>
				</div>

				<div class="w_right">
					<div class="w_735">
						<div class="title01">
							<h4 class="fl">报警消息</h4>
						</div>
						<div class="liu_tab">
							<span><a <% if (daysMode == null || daysMode.equals("7")) { out.print("class=\"on\"");}%> href="/alarm/history.do?mode=7">最近七天</a></span>
							<span><a <% if (daysMode == null || daysMode.equals("30")) { out.print("class=\"on\"");}%> href="/alarm/history.do?mode=30">最近30天</a></span>
							<span><a <% if (daysMode == null || daysMode.equals("0")) { out.print("class=\"on\"");}%> href="/alarm/history.do?mode=0">所有</a></span>
							<span class="date_ch"><a class="wu" href="javascript:void(0)" style="margin-left:20px;"></a></span>
						</div>
						<table class="gray" cellpadding="0" cellspacing="0" border="0" style="margin-top: 0px;">
							<tr class="tit">
								<td width="20%">项目名称/服务器IP</td>
								<td width="7%">监控类型</td>
								<td width="19%">开始时间</td>
								<td width="19%">恢复时间</td>
								<td width="10%">故障时长</td>
								<td width="15%">故障类型</td>
								<td colspan="2">消息内容</td>
							</tr>
							<% if (CollectionUtils.isEmpty(alarms)) { %>
							<tr>
								<td>-</td>
								<td>-</td>
								<td>-</td>
								<td>-</td>
								<td>-</td>
								<td>-</td>
								<td width="50"></td>
								<td width="56"></td>
							</tr>
							<% } else {
								for (BreakDownHistory alarm : alarms) {
									long createTime = alarm.getCreateTime();
									String date = DateUtil.friendlyTimestamp(new Date(createTime));
									
									Item item = itemMap.get(alarm.getItemId());
									String itemName = "-", itemUrl = "-", serverIp = "";
									if (item != null && item.getName() != null) {
										itemName = item.getName();
									}
									if (item != null && item.getUri() != null) {
										itemUrl = item.getUri();
									}
									if (alarm.getServerIp() != null) serverIp = alarm.getServerIp();
									
									String startTimeStr = DateUtil.friendlyTimestamp(new Date(alarm.getCreateTime())), endTimeStr = "", totalTimeStr = "";
									long endTime = alarm.getEndTime();
									if (endTime != 0L) {
										endTimeStr = DateUtil.friendlyTimestamp(new Date(endTime));
										totalTimeStr = ApplicaRuntime.timeDiff(startTimeStr, endTimeStr, "yyyy-MM-dd HH:mm:ss");
									}
									String errorTypeStr = "", itemType = "";
									if (item != null) {
										if (item.getType() == Item.TYPE_HTTP) {
											errorTypeStr = new HttpResult().getContentByErrorNo(alarm.getErrorType());
										} else if (item.getType() == Item.TYPE_PING) {
											errorTypeStr = new PingResult().getContentByErrorNo(alarm.getErrorType());
										} else if (item.getType() == Item.TYPE_NGINX) {
											errorTypeStr = new NginxResult().getContentByErrorNo(alarm.getErrorType());
										} else if (item.getType() == Item.TYPE_REDIS) {
											errorTypeStr = new RedisResult().getContentByErrorNo(alarm.getErrorType());
										}
										itemType = Item.getTypeName(item.getType());
									}
									
									
								%>
							
							<tr>
								<td><%=itemName%><%
								if (StringUtils.isNotEmpty(serverIp)) {
									out.print("<br/>[" + serverIp + "]");
								}
								%></td>
								<td><%=itemType%></td>
								<td><%=startTimeStr%></td>
								<td><%=endTimeStr %></td>
								<td><%=totalTimeStr %></td>
								<td><%=errorTypeStr %></td>
								<td colspan="2"></td>
							</tr>
								<% }
							} %>
						</table>


		         	 <% if (alarms != null && alarms.size() > 0 && totalPage > 1) {%>
		              		<div class="page" style="width:733px;text-align:center;">
		              		<%-- prev page --%>
		              		<% if (currentPage > 1) {%>
		              			<a href="<%=(pageLinkBase + (currentPage-1))%>">上一页</a>
		              		<%} else {%>
		              			<a class="on" href="#">上一页</a>
		              		<%}%>
		              		
		              		<%
		              		// int numBeside = 4;
		              		int numBeside = 3;
		              		int leftP = currentPage.intValue() - numBeside;
		              		int rightP = currentPage.intValue() + numBeside;
		              		if (rightP > totalPage.intValue()) {
		              			leftP = leftP - (rightP - totalPage.intValue());
		              		}
		              		//System.out.println("------------leftP:" + leftP);%>
		              		
		            		<% if (leftP > 1) { %>
		            			<a href="<%=pageLinkBase%>1">1</a>
		            		<% } %>
		              			
		              			
		              		<% if (leftP > 3) { %>
		              			<a href="#">...</a>
		              		<%} else if (leftP == 3) {%>
		              			<a href="<%=pageLinkBase%>2">2</a>
		              		<%}%>
		              			
		
		              		<%
		              		int startP = Math.max(leftP, 1);
		              		for (int p = startP; p < currentPage; p++) {%>
		              			<a href="<%=pageLinkBase+p%>"><%=p%></a>
		              		<%}%>
		              			
		              		<a class="on" href="#"><%=currentPage%></a>
		
		              		<% 
		              		   if (leftP < 1) {
		              		   		rightP = rightP - leftP + 1;
		              		   }
		              		   //System.out.println("------------rightP:" + rightP + ", totalPage:" + totalPage + ", currentPage:" + currentPage);
							  
							   int endP = Math.min(rightP, totalPage.intValue() - 2);
							   //System.out.println("------------endP:" + endP);
							%>
							<%for (int p = currentPage.intValue() + 1; p <= endP; p++) {%>
							  <a href="<%=pageLinkBase+p%>"><%=p%></a>
						    <%}%>
						  
						    <% if (totalPage.intValue() - rightP >= 3) {%>
								<a href="#">...</a>
						    <% } else if (totalPage.intValue() - currentPage > 1) {%>
								<a href="<%=pageLinkBase+(totalPage - 1)%>"><%=(totalPage - 1)%></a>
						    <% }%>
		
		
							<% if (currentPage < totalPage) { %>
								<a href="<%=pageLinkBase+totalPage%>"><%=totalPage%></a>
							<%} %>
						    
							
							
		              		<%-- next page --%>
		              		<% if (currentPage < totalPage) {%>
		              			 <a href="<%=pageLinkBase+(currentPage+1)%>">下一页</a>
		              		<%} else {%>
		              			<a class="on" href="#">下一页</a>
		              		<%}%>

		              	  </div>
		              <%} %>
		              
					</div>
				</div>
			</div>
		</div>
	</div>
	<p>
		<br />
	</p>

<script type="text/javascript" src="/js/calendar.js"></script>
<script type="text/javascript">
var base = "http://" + location.host,//Ajax请求的基地址
	car = new Calendar("body"),
	o = $("span.date_ch").offset(),
	itemId = query("itemId"),
	data = {};
o.top += 33;
o.left -= 253;
car.setPosition(o);
car.on("choose", function(e){
	var arr = [];
	if(e.status == "1") {
		data[e.data] = 1;
	}
	if(e.status == "0") {
		delete data[e.data];
	}
	for(var s in data) {
		arr.push(s);
	}
	arr.sort();
	if(arr.length == 2) {
		location.href = base + "/alarm/history.do?mode=-1&start=" + arr[0] + "&end=" + arr[1];
	}
});
var isShow = true;
$("span.date_ch").click(function(e) {
	e.stopPropagation();
	if(isShow == true) {
		car.show();
		isShow = false;
	} else {
		car.hide();
		isShow = true;
	}
	
});
$("body").click(function() {
	isShow = true;
	car.hide();
});
if(query("mode") == "-1") {
	$(".date_ch a").addClass("on");
}
if($("body").height() < 580) {
	$("body").height(580);
}
</script>
</body>
</html>