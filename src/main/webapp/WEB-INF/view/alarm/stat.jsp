<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%><%@ page import="java.text.NumberFormat,java.util.*,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
%><%
Map<String, Map<Byte, Integer>> statMap = (Map<String, Map<Byte, Integer>>) request.getAttribute("statMap");
String daysMode = (String) request.getAttribute("daysMode");
String start = (String) request.getAttribute("start");
String end = (String) request.getAttribute("end");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");

String pageLinkBase = null;
if (daysMode == null) {
	pageLinkBase = "/alarm/stat.do?p=";
} else {
	pageLinkBase = "/alarm/stat.do?mode=" + daysMode;
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
<title>监控系统 报警通知统计</title>
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
                  <li><span class="point">&nbsp;</span><a href="/alarm/history.do">故障报警记录</a></li>
                  <li><span class="point">&nbsp;</span><a href="/alarm/notresume.do">尚未恢复项目</a></li>
                  <li class="current"><span class="point">&nbsp;</span><a href="/alarm/stat.do">报警通知统计</a></li>
              </ul>
             </div>
          </div>
          
       <div class="w_right">
          <div class="w_735">
              <div class="title01">
              <h4 class="fl">报警通知统计</h4>
			  </div>
			  <div class="liu_tab">
			  	 <span><a href="/alarm/stat.do" <% if (daysMode == null || daysMode.equals("7")) { out.print("class=\"on\"");}%>>最近七天</a></span>
			  	 <span><a href="/alarm/stat.do?mode=30" <% if (daysMode != null && daysMode.equals("30")) { out.print("class=\"on\"");}%>>最近30天</a></span>
			  	 <span><a href="/alarm/stat.do?mode=0" <% if (daysMode != null && daysMode.equals("0")) { out.print("class=\"on\"");}%>>所有</a></span>
			  	 <span class="date_ch"><a href="javascript:void(0)" class="wu" style="margin-left:20px"></a></span>
			  </div>
              <table class="gray" cellpadding="0" cellspacing="0" border="0" style="margin-top: 0px;">
              <tr class="tit">
                  <td width="104">日期</td>
                  <td width="104">Email报警次数</td>
                  <td width="104">短信报警次数</td>
                  <td width="104">Youni报警次数</td>
              </tr>
              <% if (statMap.size() == 0) {%>
              
              <tr>
                  <td>-</td>
                  <td width="50">-</td>
                  <td width="56">-</td>
                   <td width="56">-</td>
              </tr>
				  
			  <% } else {
				  Iterator it = statMap.entrySet().iterator();
				  while (it.hasNext()) {
					  Map.Entry entry = (Map.Entry) it.next();
					  
					  String date = entry.getKey().toString();
					  Map<Byte, Integer> map = (Map<Byte, Integer>) entry.getValue();
					  Integer emailCount = 0, smsCount = 0,  youniCount = 0;
					  if (map != null && map.size() > 0) {
						  emailCount = map.get((byte)2);
						  smsCount = map.get((byte)1);
						  youniCount = map.get((byte)4);
					  }
			  %>
				  
              <tr>
                  <td><%=date%></td>
                  <td width="50"><%=(emailCount == null ? 0 : emailCount)%></td>
                  <td width="56"><%=(smsCount == null ? 0 : smsCount)%></td>
                  <td width="56"><%=(youniCount == null ? 0 : youniCount)%></td>
              </tr>
			  <% 
				  }
				} %>

              </table>
         	 <%if (statMap != null && statMap.size() > 0 && totalPage > 1) {%>
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
<script type="text/javascript" src="/js/jquery-1.4.3.min.js"></script>
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
		location.href = base + "/alarm/stat.do?mode=-1&start=" + arr[0] + "&end=" + arr[1];
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