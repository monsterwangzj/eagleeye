<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chengyi.eagleeye.model.*,java.util.*" %><% 

MonitorGroup monitorGroup = (MonitorGroup) request.getAttribute("monitorGroup");

List<Monitor> monitors = (List<Monitor>) request.getAttribute("monitors");
HashMap<Long, Byte> monitorMap = (HashMap<Long, Byte>) request.getAttribute("monitorMap");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");
String pageLinkBase = null;
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<title>监控系统 设置</title>
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
</head>
<body data="<%=monitorMap%>">
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="w_960 pt">
           <div class="w_left">
              <div class="w_210 bc">
              <div class="title"><h4>设置</h4></div>
              <ul class="list">
                  <li><span class="point">&nbsp;</span><a href="/config/index.do">故障处理人</a></li>
                  <li class="current"><span class="point">&nbsp;</span><a href="/config/alarmgroup.do">报警组</a></li>
              </ul>
             </div>
             
             <div class="w_210 mt">
              <div class="title"><h4>个人信息设置</h4></div>
              <ul class="list">
                  <li><span class="point">&nbsp;</span><a href="/config/editprofile.do">修改个人信息</a></li> 
                  <li><span class="point">&nbsp;</span><a href="/config/editpwd.do">修改密码</a></li>
              </ul>
             </div>
             
          </div>

          <div class="w_right">
          
		<div class="w_735">
              <div class="title01">
              <h4 class="fl"><%=monitorGroup.getGroupName()%>报警组 &gt; <b>选择报警组成员及报警方式</b></h4>
              </div>
              <table id="box01" cellspacing="0" cellpadding="0" border="0" class="gray" data="<%=totalPage%>">   
              <tbody>
              <tr class="tit">
                  <td></td>
                  <td>故障处理人</td>
                  <td>手机短信</td>
                  <td>邮件</td>
                  <td>盛大有你</td>
              </tr>
              <% if (CollectionUtils.isEmpty(monitors)) { %>
	              <tr>
	                  <td><span class="ch02"></span></td>
	                  <td>-</td>
	                  <td><span class="ch02"></span></td>
	              	  <td><span class="ch02"></span></td>
	                  <td><span class="ch02"></span></td>
	             </tr>
              <%} else { 
              		for (Monitor monitor : monitors) { %>
		              <tr>
		                  <td><span class="ch02" data="0"></span></td>
		                  <td><%=monitor.getName()%></td>
		                  <td><span class="ch02" data="1"></span></td>
		              	  <td><span class="ch02" data="2"></span></td>
		                  <td><span class="ch02" data="4"></span></td>
		             </tr>
              	   <%}
              } %>
              <tr>
              <td colspan="6" class="ri pt pb"><input id="btnSubmit" type="button" value="提 交" name="" class="button"/></td>
              </tr>
            </tbody>
            </table>
             <div id="page01" class="page" style="width:733px;text-align:center;"></div>
          </div>
                    
        </div>
      </div>
    </div>
</div>
<div id="panels1" class="w_right" style="width:300px;display:none;">
	<div class="w_735" style="width:298px;">
    	<div class="title01">
      		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
      	</div>
      	<div style="margin:10px auto;overflow:hidden;zoom:1;text-align:center;">
      		<p style="line-height:46px;">按分类查看按分类查看按分类查看</p>
      	</div>
      	<div class="gbtns" style="margin:10px auto;overflow:hidden;zoom:1;text-align:center;">
      		<input class="button01" name="" type="button" value="确定">
      		<input class="button01" name="" type="button" value="取消">
      	</div>
  </div>
</div>
<script type="text/javascript" src="/js/jquery-1.4.3.min.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script>
var base = "http://" + location.host,//Ajax请求的基地址
	dataUrl = base+ "/config/getmonitors.do?gid=" + query("gid"),
	layer1 = new popLayer($("#panels1"), 1),//创建分组的弹层
	map = eval("(" + $("body").attr("data").replace(/=/g, ":") + ")"),
	dataResult = {};
for(var arr in map) {
	if(map[arr] == 1) {
		dataResult[arr] = "1,b,c";
	}
	if(map[arr] == 2) {
		dataResult[arr] = "a,2,c";
	}
	if(map[arr] == 3) {
		dataResult[arr] = "1,2,c";
	}
	if(map[arr] == 4) {
		dataResult[arr] = "a,b,4";
	}
	if(map[arr] == 5) {
		dataResult[arr] = "1,b,4";
	}
	if(map[arr] == 6) {
		dataResult[arr] = "a,2,4";
	}
	if(map[arr] == 7) {
		dataResult[arr] = "1,2,4";
	}
}
$("#panels1").find(".title01>a").click(function() {
	layer1.hide();
});
$("#panels1").find(".button01").eq(0).click(function() {
	layer1.ensure();
});
$("#panels1").find(".button01").eq(1).click(function() {
	layer1.hide();
});
//分页
var page = new chPage(dataUrl, $("#box01>tbody"), $("#page01"), $("#box01").attr("data"), map);
//提交故障处理人
$("#box01").delegate(".button", "click", function(e) {
	//发送故障处理人的代码
});
//复选框切换
$("#box01").delegate("span", "click", function(e) {
	var parent = $(this).parent().parent(),
		gid = parent.attr("data");
	e.stopPropagation();
	e.preventDefault();
	if($(this).hasClass("ch01")) {
		$(this).removeClass("ch01").addClass("ch02");
		parent.find("span").eq(0).removeClass("ch01").addClass("ch02");
		parent.attr("order", parseInt(parent.attr("order") || 0) + 1);
		dataResult[gid] = dataResult[gid] ? dataResult[gid] : "a,b,c";
		if($(this).attr("data") == "0") {
			parent.find("span").removeClass("ch01").addClass("ch02");
			parent.attr("order", 3);
			dataResult[gid] = "1,2,4";
			map[gid] = map[gid] || 0;
			map[gid] = 7;
		}
		if($(this).attr("data") == "1") {
			dataResult[gid] = dataResult[gid].replace("a", "1");
			map[gid] = map[gid] || 0;
			map[gid] += 1;
		}
		if($(this).attr("data") == "2") {
			dataResult[gid] = dataResult[gid].replace("b", "2");
			map[gid] = map[gid] || 0;
			map[gid] += 2;
		}
		if($(this).attr("data") == "4") {
			dataResult[gid] = dataResult[gid].replace("c", "4");
			map[gid] = map[gid] || 0;
			map[gid] += 4;
		}
			
	} else if($(this).hasClass("ch02")) {
		var falg = false;
		$(this).removeClass("ch02").addClass("ch01");
		parent.attr("order", parseInt(parent.attr("order") || 0) - 1);
		if($(this).attr("data") == "0") {
			parent.find("span").removeClass("ch02").addClass("ch01");
			parent.attr("order", 0);
			dataResult[gid] = "a,b,c";
			map[gid] = map[gid] || 0;
			map[gid] = 0;
		}
		if($(this).attr("data") == "1") {
			dataResult[gid] = dataResult[gid].replace("1", "a");
			map[gid] = map[gid] || 0;
			map[gid] -= 1;
		}
		if($(this).attr("data") == "2") {
			dataResult[gid] = dataResult[gid].replace("2", "b");
			map[gid] = map[gid] || 0;
			map[gid] -= 2;
		}
		if($(this).attr("data") == "4") {
			dataResult[gid] = dataResult[gid].replace("4", "c");
			map[gid] = map[gid] || 0;
			map[gid] -= 4;
		}
		if(parent.attr("order") == 0) {
			parent.find("span").eq(0).removeClass("ch02").addClass("ch01");
			delete dataResult[gid];
		}
	}
});
$("#box01").delegate("#btnSubmit", "click", function() {
	var o = {
		"gid": query("gid"),
		"data": ""
	};
	for(var arr in dataResult) {
		o.data += arr + "@" + dataResult[arr].replace(/(,*[a-z])+/g, "").replace(/^,|,$/, "") + ";";
	}
	o.data = encodeURIComponent(o.data);
	$.get(base + "/config/reconfigmonitorgroup.do", o, function(results) {
		results = eval("(" + results + ")");
		if(results.status == 1) {
			layer1.un().show(function(el) {
				el.pan.find("p").html("修改报警组设置成功");
				el.pan.find(".button01").hide().eq(0).show();;
			});
			layer1.on("ensure", function() {
				location.reload();
			});
		}
	});
});
$("td, span").mousedown(function() {
	return false;
});
$("td, span").mouseup(function() {
	return false;
});
</script>
</body>
</html>
