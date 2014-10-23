<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%><%@ page import="com.chengyi.eagleeye.model.assist.HttpOption,java.text.NumberFormat,java.util.*,net.sf.json.JSONObject,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
%><%
Item item = (Item) request.getAttribute("item");
String status = "";
if (item.getStatus() == (byte) 1) {
	status = "开启";
} else if (item.getStatus() == (byte) 0) {
	status = "冻结";
} else if (item.getStatus() == (byte) -1) {
	status = "其它";
}

String monitorFreq = "";
if (item.getMonitorFreq() < 60) {
	monitorFreq = item.getMonitorFreq() + " 秒钟";
} else {
	double freq = item.getMonitorFreq() * 1. / 60;
	NumberFormat nf = NumberFormat.getInstance();
	monitorFreq = nf.format(freq) + " 分钟";
}

ArrayList<String> keyList = (ArrayList<String>) request.getAttribute("keyList");
ArrayList<JSONObject> jsonList = (ArrayList<JSONObject>) request.getAttribute("jsonList");
System.out.println("keyList:" + keyList);

String daysMode = (String) request.getAttribute("mode");
if (daysMode == null) daysMode = "";
String start = (String) request.getAttribute("start");
String end = (String) request.getAttribute("end");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");

String pageLinkBase = null;
//if (daysMode == null) {
//	pageLinkBase = "/alarm/messages.do?p=";
//} else {
	pageLinkBase = "/item/dailystat2.do?itemId=" + item.getId() + "&mode=" + daysMode + "&p=";
//}

	if (daysMode == null) {
		pageLinkBase = "/item/dailystat2.do?itemId=" + item.getId() + "&p=";
	} else {
		pageLinkBase = "/item/dailystat2.do?itemId=" + item.getId() + "&mode="+ daysMode;
		if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
			pageLinkBase += "&start=" + start + "&end=" + end;
		}
		pageLinkBase += "&p=";
	}
	



%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<style>
#ccontainer td{
	color:#000;
	background:#fff;
}
#ccontainer th{
	color:#000;
}
</style>
<title>监控系统 创建项目分类</title>
</head>
<body>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="w_b mt">
      <table class="tongji" width="930" border="1">
  <tr>
    <td width="65" height="33">项目名称：</td>
    <td width="423" class="fb"><%=item.getName() %></td>
    <td width="69">服务状态：</td>
    <td width="341"><%=status%>项目</td>
  </tr>
  <tr>
    <td class="noline">URL：</td>
    <td class="noline"><%=item.getUri() %> </td>
    <td class="noline">监控频率：</td>
    <td class="noline"><%=monitorFreq %></td>
  </tr>
  <tr>
  	<%
  	//JSONObject optionJSON = JSONObject.fromObject(item.getOptions());
  	%>
    <td class="noline">监控类型：</td>
    <td class="noline">NGINX</td>
  </tr> 
</table>
      </div>
      <div class="w_960 pt">
           <div class="w_left">
              <div class="w_210 bc">
              <div class="title"><h4>监控信息</h4></div>
              <ul class="list">
              	  <li class="point"><span class="point">&nbsp;</span><a href="/nginxitem/summary.do?itemId=<%=item.getId()%>">概况</a></li>
                  <li class="current noline"><span class="point">&nbsp;</span><a href="#">每日统计</a></li>
                  
              </ul>
             </div>
             
          </div>
          <div class="w_right">
          <div class="w_735">
              <div class="title01">
              <h4 class="fl">每日统计汇总</h4>
              <p class="fr pr">
              		<span><a <%=( daysMode.equals("7") ? "class=\"on\"" : "")%> href="/nginxitem/dailystat2.do?itemId=<%=item.getId()%>&mode=7">最近7天</a></span>
              		<span><a <%=( daysMode.equals("15") ? "class=\"on\"" : "")%> href="/nginxitem/dailystat2.do?itemId=<%=item.getId()%>&mode=15">最近15天</a></span>
              		<span><a <%=( daysMode.equals("30") ? "class=\"on\"" : "")%> href="/nginxitem/dailystat2.do?itemId=<%=item.getId()%>&mode=30">最近30天</a></span>
              		<span class="date_ch"><a href="javascript:void(0)" <%=( daysMode.equals("-1") ? "class=\"on\"" : "")%> href="javascript:void(0)"></a></span>
              </p>
              </div>
              <table class="gray" cellpadding="0" cellspacing="0" border="0">
              <tr class="tit">
                  <td>日期</td>
                  <td>可用性</td>
                  <td>故障时间</td>
                  <td>最小响应时间</td>
                  <td>平均响应时间</td>
                  <td>最大响应时间</td>
              </tr>
              <%
              if (keyList != null && keyList.size() > 0) {
            	     for (int k = keyList.size() - 1; k >= 0; k--) {
            	    	 String key = keyList.get(k); 
            	    	 JSONObject jobj = jsonList.get(k);
            	    	 
            	    	 Double usability = jobj.getDouble("usability");
            	    	 String errorTime = "-";
            	    	 if (usability != 100L) {
            	    		 Long l_error_time = new Double(24 * 60 * (100 - usability)).longValue();
            	    		 errorTime = l_error_time.toString();
            	    	 }
            	    	 %>
		 			  <tr>
		                  <td><%=key%></td>
		                  <td><%=jobj.getDouble("usability") %>%</td>
		                  <td><%=errorTime %></td>
		                  <td><%=jobj.getDouble("min") %>ms</td>
		                  <td><%=jobj.getDouble("avg") %>ms</td>
		                  <td><%=jobj.getDouble("max") %>ms</td>
		              </tr>
 					 <%}
     
              } else { %>
             <tr>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
              </tr>
              <% } %>
              </table>
		         	 <% if (keyList != null && keyList.size() > 0 && totalPage > 1) {%>
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
<p><br/></p>

<script type="text/javascript" src="/js/calendar.js"></script>
<script type="text/javascript">
var base = "http://" + location.host,//Ajax请求的基地址
	car = new Calendar("body"),
	o = $("span.date_ch").offset(),
	itemId = query("itemId"),
	data = {};
o.top += 33;
o.left -= 271;
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
		location.href = base + "/item/dailystat.do?itemId=" + itemId + "&mode=-1&start=" + arr[0] + "&end=" + arr[1];
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
</script>
</body>
</html>
