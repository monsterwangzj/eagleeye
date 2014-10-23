<%@page import="com.chengyi.eagleeye.util.CookieUtil"%>
<%@ page contentType="text/html;charset=UTF-8"%><%@page import="com.chengyi.eagleeye.model.User,com.chengyi.eagleeye.manage.IdentityCheckPoint,org.apache.commons.lang.StringUtils"%><%
response.setHeader("Cache-Control", "no-cache, must-revalidate");
response.setHeader("Pragma", "no-cache");

// User loginUser = (User) request.getSession().getAttribute("loginUser");
User loginUser = (User) request.getAttribute("loginUser");
String loginName = IdentityCheckPoint.getUsernameFromCookie(request, response);

String menu = (String) request.getAttribute("menu");
if (menu == null) menu = "";

%>
<!-- head Start -->

<div class="head">
	<a href="/index.do"><img class="s_logo" src="/styles/images/s_logo.jpg" /></a>
	<p><!--<span><img src="/styles/images/piv.jpg" /></span>-->
	<span><b><%=loginName%></b></span>
	<span>|</span>
	<span><a href="/help/help01.jsp">帮助</a></span>
	<span>|</span>
	<span><a href="/logout.do">退出</a></span>
    </p>
</div>

<div class="nav">
	<div class="menu">
		<div class="main"><a <%=(menu.equals("summary") ? "class=\"on\"" : "") %> href="/index.do">概述</a></div>
	</div>
	
	<div class="menu">
		<div class="main" id="jiankongmenu"><a <%=(menu.equals("monitor") ? "class=\"on\"" : "") %> href="/monitor/index.do">监控</a></div>
		<div class="me" style="display:none" id="jiankongmenu2">
			<a href="/monitor/index.do">网站监控<span>监控网站是否可以访问，以及访问速度是否稳定！</span></a>
			<a href="/monitor/service.do">常见服务监控<span>监控常用的服务性能，如Apache，Nginx等</span></a>
		</div>
	</div>
	
	<div class="menu">
		<div class="main"><a <%=(menu.equals("alarm") ? "class=\"on\"" : "") %> href="/alarm/messages.do">报警</a></div>
	</div>
	<div class="menu">
		<div class="main"><a <%=(menu.equals("config") ? "class=\"on\"" : "") %>  href="/config/index.do">设置</a></div>
	</div>
	
</div>
<!-- head End -->
<%-- --%>
<script type="text/javascript">
	$("#jiankongmenu").mouseover(function() {
		if ($("#jiankongmenu2").is(":hidden")) {
			$("#jiankongmenu2").show(300);
		}
	});
	$("#jiankongmenu").mouseout(function() {
		$("#jiankongmenu2").hide();
	});
	
	$("#jiankongmenu2").mouseout(function() {
		$("#jiankongmenu2").hide();
	});
	$("#jiankongmenu2").mouseover(function() {
		if ($("#jiankongmenu2").is(":hidden")) {
			$("#jiankongmenu2").show();
		}
	});
</script>