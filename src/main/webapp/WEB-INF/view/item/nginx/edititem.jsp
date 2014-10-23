<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%><%@ page import="com.chengyi.eagleeye.model.assist.*,java.util.*,com.chengyi.eagleeye.model.assist.HttpOption,net.sf.json.JSONObject,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
%><%
Item item = (Item) request.getAttribute("item");
List<ServiceItemGroup> itemGroups = (List<ServiceItemGroup>) request.getAttribute("itemGroups"); // 分组列表
Long itemGid = item.getGid();
String itemGroupName = "无";
if (CollectionUtils.isNotEmpty(itemGroups)) {
	for (ServiceItemGroup itemGroup : itemGroups) {
		if (itemGroup.getId().equals(itemGid)) {
			itemGroupName = itemGroup.getName();
			break;
		}
	}
}
JSONObject optionJSON = JSONObject.fromObject(item.getOptions());
PingOption pingOption = (PingOption) JSONObject.toBean(optionJSON, PingOption.class);

// System.out.println("itemGroupName:" + itemGroupName);
List<MonitorGroup> monitorGroups = (List<MonitorGroup>) request.getAttribute("monitorGroups"); // 报警组

// 当前报警组
MonitorGroup currentMonitorGroup = (MonitorGroup) request.getAttribute("currentMonitorGroup");
// System.out.println("currentMonitorGroup:" + currentMonitorGroup);

// 默认报警组数据
Long mgmsSize = (Long) request.getAttribute("mgmsSize");
HashMap<Long, Monitor> currentMonitorsMap  = (HashMap<Long, Monitor>) request.getAttribute("currentMonitorsMap");
List<MonitorGroupMonitor> mgms = (List<MonitorGroupMonitor>) request.getAttribute("mgms");

// System.out.println("-------------monitorGroups.count:" + monitorGroups.size() + ", mgmsSize:" + mgmsSize);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<link href="/styles/gb.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="/js/jquery-1.4.3.min.js"></script>
<title>监控系统 创建监控</title>
<style>
table td span.ch01, table td span.ch02{
	margin:0px;
	float:left;
	margin-left:26px;
}
strong.username{
	margin-left:36px;
	display:block;
	float:left;
	height:19px;
	line-height:19px;
	margin-left:23px;
}
.b_bg table td span {
	margin-left:48px;
}
</style>
</head>
<body>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="b_bg mt" >
      <div class="title"><h4>创建监控项目> nginx监控</h4></div>
      <div class="fx_top1" style="overflow:hidden;">
      	<div class="fx_top1L">填写监控信息</div><div class="fx_top1R"></div>
      </div>
      <form name="form1" method="post" action="/nginxitem/update.do">
      <input type="hidden" name="id" value="<%=item.getId()%>"/>
      <table cellpadding="0" cellspacing="0" border="0" style="width:910px;margin-bottom:100px;margin-left:48px;">
          <tr>
              <td width="13%" class="ri">监控项目名称：</td>
              <td width="32%"><input name="itemname" type="text" style="width:180px;" value="<%=item.getName()%>"/><font style="margin-left:10px;color:#f00;">*</font><font style="margin-left:10px;color:#fff;">是否加入分类？</font></td>
              <td width="56%" colspan="2">

              	<div class="ww" style="width:53%;float:left;">
              	<div class="select">
              		<input type="text" value="<%=itemGroupName%>">
              		<input name="gid" type="text" value="<%=item.getGid()==null?"":item.getGid()%>" style="display:none;">
              		<label></label>
             	</div>
	              <div class="open" style="display: none;">
	              	<%if (CollectionUtils.isEmpty(itemGroups)) {%>
	 		              <a class="on" href="javascript:void(0)">无</a>
	              	<%} else {%>
	              		<a href="javascript:void(0)" <%=(itemGroupName.equals("无") ? "class=\"on\"" : "")%>>无</a>
	              		<%for (ServiceItemGroup itemGroup: itemGroups) {%>
	              			<a href="javascript:void(0)" data="<%=itemGroup.getId()%>" <%=(item.getGid() == itemGroup.getId() ? "class=\"on\"" : "")%>><%=itemGroup.getName()%></a>
	              		<%}%>
	              	<%}%>
              		</div>
            	</div>
            	<div style="width:80px;height:20px;line-height:20px;float:left;"><font id="createGroup" style="margin-left:10px;color:#a5d8ff;cursor:pointer;">创建分类？</font></div>
              </td>
          </tr>
          <tr>
              <td width="13%" class="ri"></td>
              <td width="87%" class="tip" colspan="3">给您的监控项起一个名字，比如：第三方数据系统。</td>
          </tr>
          <tr>
              <td width="13%" class="ri">监控类型：</td>
              <td width="87%" colspan="3">NGINX 监控</td>
          </tr>
           <tr>
              <td width="13%" class="ri">状态页面URL：</td>
              <td width="87%" colspan="3"><input name="url" type="text" size="400" value="<%=item.getUri()%>" class="longinput" style="width: 400px; "/><font style="margin-left:10px;color:#f00;">*</font></td>
          </tr>
          <tr>
              <td width="13%" class="ri"></td>
              <td  width="87%" colspan="3" class="tip">比如：http://122.11.33.32/NginxStatus</td>
          </tr>
          <tr><td width="100%" colspan="4"><div class=" setMore1 setMore" style="width:88px;">更多高级设置</div></td></tr>
          <tr class="shidden">
              <td width="13%" class="ri">监控频率：</td>
              <td width="87%" colspan="3">
	              <div class="ww" style="z-index:11">
	              	  <div class="select">
	              	    <%
						int freq = item.getMonitorFreq();
						String freqtext = null;
						if (freq < 60) {
							freqtext = freq + "秒钟";
						} else {
							freqtext = freq / 60 + "分钟"; 
						}
						%>
	              	  	<input type="text" value="<%=freqtext%>"/>
	              	  	<input name="monitorfreq" type="text" style="display:none;" value="<%=(item.getMonitorFreq())%>"/>
	              	  	<label></label>
	             	  </div>
		              <div class="open" style="display:none;">
		                  <a <%=(freq == 10 ? "class=\"on\"" : "")%> data="10" href="javascript:void(0)">10秒钟</a>
			              <a <%=(freq == 60 ? "class=\"on\"" : "")%> data="60" href="javascript:void(0)">1分钟</a>
			              <a <%=(freq == 120 ? "class=\"on\"" : "")%> data="120" href="javascript:void(0)">2分钟</a>
			              <a <%=(freq == 300 ? "class=\"on\"" : "")%> data="300" href="javascript:void(0)">5分钟</a>
			              <a <%=(freq == 600 ? "class=\"on\"" : "")%> data="600" href="javascript:void(0)">10分钟</a>
			              <a <%=(freq == 900 ? "class=\"on\"" : "")%> data="900" href="javascript:void(0)">15分钟</a>
			              <a <%=(freq == 1200 ? "class=\"on\"" : "")%> data="1200" href="javascript:void(0)">20分钟</a>
			              <a <%=(freq == 1800 ? "class=\"on\"" : "")%> data="1800" href="javascript:void(0)">30分钟</a>
			              <a <%=(freq == 3600 ? "class=\"on\"" : "")%> data="3600" href="javascript:void(0)">60分钟</a>
		              </div>
	              </div>
              </td>
          </tr>
          <tr class="shidden">
              <td width="13%" class="ri"></td>
              <td width="87%" colspan="3" class="tip">每个监控项的执行间隔。执行间隔越短越容易更早的发现问题，当然越短频率越高，所需要消耗的系统资源相对也多一些。</td>
          </tr>
          <tr class="shidden">
              <td width="13%" class="ri">重试几次后报警：</td>

              <td width="87%" colspan="3">
              <div class="ww" style="z-index:10">
              	<div class="select">
              			<input type="text" value="<%=item.getRetryTimes()%>次"/>
              			<input name="retrytimes" type="text" style="display:none;" value="<%=item.getRetryTimes()%>"/>
              			<label></label>
              		</div>
              	<div class="open" style=" display:none;">
						  <a <%=(item.getRetryTimes() == 1 ? "class=\"on\"" : "")%> data="1" href="javascript:void(0)">1次</a>
			              <a <%=(item.getRetryTimes() == 2 ? "class=\"on\"" : "")%>data="2" href="javascript:void(0)">2次</a>
			              <a <%=(item.getRetryTimes() == 3 ? "class=\"on\"" : "")%>data="3" href="javascript:void(0)">3次</a>
			    	</div>
			    </div>
              </td>
          </tr>
          <tr class="shidden">
              <td width="13%" class="ri"></td>
              <td width="87%" colspan="3" class="tip">发现故障后自动进行以上次数的重试，多次重试都失败后，才会触发故障告警。重试时间间隔目前固定为1分钟。</td>
          </tr>
          <tr class="shidden">
              <td width="13%" class="ri">连续报警提醒：</td>
			  <%
			  int continuousReminder = item.getContinuousReminder();
			  String continuousReminderText = "无";
			  if (continuousReminder != Integer.MAX_VALUE && continuousReminder != 0) {
			  	  continuousReminderText = continuousReminder / 60 + "分钟";
			  }
			  %>
              <td width="87%" colspan="3">
              <div class="ww" style="z-index:9">
              	<div class="select">
              			<input type="text" value="<%=continuousReminderText%>"/>
              			<input name="continuousreminder" type="text" style="display:none;" value="<%=continuousReminder%>"/>
              			<label></label>
              		</div>
              <div class="open" style="display:none;">
              <a <%=((continuousReminder == Integer.MAX_VALUE || continuousReminder == 0) ? "class=\"on\"" : "")%> data="<%=Integer.MAX_VALUE%>" href="javascript:void(0)">无</a>
              <a <%=(continuousReminder == 15 * 60 ? "class=\"on\"" : "")%> data="900" href="javascript:void(0)">15分钟</a>
              <a <%=(continuousReminder == 30 * 60 ? "class=\"on\"" : "")%> data="1800" href="javascript:void(0)">30分钟</a>
              <a <%=(continuousReminder == 45 * 60 ? "class=\"on\"" : "")%> data="2700" href="javascript:void(0)">45分钟</a>
              <a <%=(continuousReminder == 75 * 60 ? "class=\"on\"" : "")%> data="4500" href="javascript:void(0)">75分钟</a>
              <a <%=(continuousReminder == 150 * 60 ? "class=\"on\"" : "")%> data="9000" href="javascript:void(0)">150分钟</a>
              <a <%=(continuousReminder == 300 * 60 ? "class=\"on\"" : "")%> data="18000" href="javascript:void(0)">300分钟</a>
              </div></div>

              </td>
          </tr>
          <tr class="shidden">
              <td width="13%" class="ri"></td>
              <td width="87%" colspan="3" class="tip">当任务设置开启连续报警，如果您的任务出现故障，在第一次报警以后，每隔您所选的时间，将给您发送一次报警。</td>
          </tr>
         <%-- 
          <tr class="shidden">
              <td width="13%" class="ri">服务器IP：</td>
              <td width="87%" colspan="3"><input name="serverips" type="text" value="<%=(pingOption.getServerIps()==null?"":pingOption.getServerIps())%>" class="longinput" style="width: 400px;"></td>
          </tr>
          <tr class="tip shidden">
              <td width="13%" class="ri"></td>
              <td width="87%" colspan="3" class="tip"> 如果您的域名DNS指向多台服务器，您可以指定IP来监控特定的服务器。如果有多个IP，请使用';'分隔。</td>
          </tr>
          --%>
          <tr>
          	<td width="100%" colspan="4"><div class="fx_top1" style="width:98%;">
		      	<div class="fx_top1L fx_top2L" style="width:13%;margin-left:-10px;">设置常规告警通知</div><div class="fx_top1R fx_top2R" style="width:83%;"></div>
		    </div></td>
          </tr>
          <tr>
              <td width="13%" class="ri">选择报警组：</td>
			  <%
			  String currentMonitorGroupName = "无";
			  String currentMonitorGroupId = "";
			  if (currentMonitorGroup != null && currentMonitorGroup.getGroupName() != null) {
			      currentMonitorGroupName = currentMonitorGroup.getGroupName();
			      currentMonitorGroupId = currentMonitorGroup.getId().toString();
			  }
			  %>
              <td width="87%" colspan="3">
              <div class="wl" style="width:251px;margin:0px;padding:0px;">
              	<div class="select">
              			<input type="text" value="<%=currentMonitorGroupName%>">
              			<input id="alertG" name="mgid" type="text" value="<%=currentMonitorGroupId%>" style="display:none;">
              			<label></label>
              		</div>
              <div id="open002" class="open" style=" display:none;">
              <%if (CollectionUtils.isEmpty(monitorGroups)) {%>
	 		        <a class="on" href="javascript:void(0)">无</a>
	          <%} else {%>
	          		<a <%=(currentMonitorGroupId.equals("") ? "class=\"on\"" : "")%> href="javascript:void(0)">无</a>
	              	<%for (MonitorGroup monitorGroup: monitorGroups) {%>
	              		<a <%=(currentMonitorGroupId.equals(monitorGroup.getId().toString()) ? "class=\"on\"" : "")%> href="javascript:void(0)" data="<%=monitorGroup.getId()%>"><%=monitorGroup.getGroupName()%></a>
	              	<%}%>
	          <%}%>
              </div>
              </td>
          </tr>
          <tr><td width="100%" colspan="4" class="b_bg" style="vertical-align:top;border:0px none;">
          	<table border="0" cellspacing="0" cellpadding="0" class="fx_tel" style="margin:20px 10px 20px 146px;">
              <tbody id="box002">
              	<tr>
	                <td class="fx_teltd">&nbsp;</td>
	                <td class="fx_teltd">手机短信</td>
	                <td class="fx_teltd">邮件</td>
	                <td class="fx_teltd">盛大有你</td>
              	</tr>
              	
              	<% if (CollectionUtils.isNotEmpty(mgms)) {
              		for (MonitorGroupMonitor mgm : mgms) {
              			Monitor monitor = currentMonitorsMap.get(mgm.getId());
              			if (monitor == null) continue;
              			
              			byte alarmChannel = mgm.getAlarmChannel(); // 报警方式
              			%>
			            <tr id="moren" data="<%=alarmChannel%>">
			                <td><%=monitor.getName()%></td>
			                <td><span class="fx_click"></span></td>
			                <td><span class="fx_click"></span></td>
			                <td><span class="fx_click"></span></td>
			             </tr>
              		<%}
              	 } %>


            </tbody></table>
          </td></tr>
           <tr><td width="100%" colspan="4"><div id="jianAddMen" class="setMore addUser" style="margin-left:127px;">添加新故障处理人</div></td></tr>
           <tr><td width="100%" colspan="4"><div id="jianAddAMen" class="setMore addUser" style="margin-left:127px;">添加已有故障处理人</div></td></tr>
          <tr>
              <td width="13%" ></td>
              <td width="87%" colspan="3" class="ri"><input id="btnSubmit" class="button" name="" type="button" value="保 存" /></td>
          </tr>
      </tbody>
    	</table>
    </div>
</div>
<div id="panels1" class="w_right" style="width: 360px; overflow: hidden; display: none;">
	<div class="w_735" style="width:358px;">
    	<div class="title01">
     		<h4 class="fl">监控 &gt; <b>创建项目分类</b></h4>
     		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
         	<tbody>
         		<tr>
             		<td width="72">组名称：</td>
             		<td colspan="2"><input class="namedata" type="text" style="width:218px;"></td>
         		</tr>
         		<tr>
         			<td colspan="3" style="height:25px;"></td>
         		</tr>
         		<tr>
             		<td></td>
             		<td width="264" colspan="2"><input class="btnCreateGroup button01" name="" type="button" value="创建分类"></td>
         		</tr>
     		</tbody>
    	</table>
    </div>
</div>
<div id="panels2" class="w_right" style="width: 300px; display: none;">
	<div class="w_735" style="width:298px;">
    	<div class="title01">
      		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
      	</div>
      	<div style="margin:10px auto;overflow:hidden;zoom:1;text-align:center;">
      		<p style="line-height:46px;">名称不能为空！</p>
      	</div>
      	<div class="gbtns" style="margin:10px auto;overflow:hidden;zoom:1;text-align:center;">
      		<input class="button01" name="" type="button" value="确定" style="display: inline-block;">
      		<input class="button01" name="" type="button" value="取消" style="display: none;">
      	</div>
  </div>
</div>
<div id="panels3" class="w_right" style="width: 360px; overflow: hidden; display: none;">
	<div class="w_735" style="width:358px;">
    	<div class="title01">
     		<h4 class="fl">监控 &gt; <b>创建报警组</b></h4>
     		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
         	<tbody>
         		<tr>
             		<td width="72">报警组名称：</td>
             		<td colspan="2"><input class="namedata" type="text" style="width:218px;"></td>
         		</tr>
         		<tr>
         			<td colspan="3" style="height:25px;"></td>
         		</tr>
         		<tr>
             		<td></td>
             		<td width="264" colspan="2"><input class="btnCreateGroup button01" name="" type="button" value="提交"></td>
         		</tr>
     		</tbody>
    	</table>
    </div>
</div>
<div id="panels4" class="w_right" style="width: 360px; overflow: hidden; display: none;">
	<div class="w_735" style="width:358px;">
    	<div class="title01">
     		<h4 class="fl">监控 &gt; <b>添加新故障处理人</b></h4>
     		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
         	<tbody>
         		<tr>
             		<td width="72">故障处理人：</td>
             		<td colspan="2"><input class="guzhangMenName" type="text" style="width:218px;"><font style="margin-left:10px;color:#f00;">*</font></td>
         		</tr>
         		<tr>
             		<td width="72">手机号码：</td>
             		<td colspan="2"><input class="phoneNumber" type="text" style="width:218px;"><font style="margin-left:10px;color:#f00;">*</font></td>
         		</tr>
         		<tr>
             		<td width="72">邮件地址：</td>
             		<td colspan="2"><input class="emailAddress" type="text" style="width:218px;"><font style="margin-left:10px;color:#f00;">*</font></td>
         		</tr>
         		<tr>
         			<td colspan="3" style="height:25px;"></td>
         		</tr>
         		<tr>
             		<td></td>
             		<td width="264" colspan="2"><input class="btnCreateGroup button01" name="" type="button" value="提交"></td>
         		</tr>
     		</tbody>
    	</table>
    </div>
</div>
<div id="panels5" data="0" class="w_right" style="width: 390px; overflow: hidden; display: none; position: absolute;">
	<div class="w_735" style="width:388px;">
    	<div class="title01">
     		<h4 class="fl">监控 &gt; <b>添加已有故障处理人</b></h4>
     		<a class="btnClose" href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     		<div style="width:39px;height:30px;line-height:30px;float:right;margin-right:10px;"><a id="btnChangePage" href="javascript:void(0);">换一批</a></div>
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:388px;margin:32px auto;overflow:hidden;">
         	<tbody class="pan"></tbody>
    	</table>
    </div>
</div>

<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript">
var base = "http://" + location.host,
	layer1 = new popLayer($("#panels1")),//创建分类的弹层
	layer2 = new popLayer($("#panels2"), 1),//消息框
	layer3 = new popLayer($("#panels3"), 2),//创建分组的弹层
	layer4 = new popLayer($("#panels4"), 2);//创建分组的弹层
	layer5 = new popLayer($("#panels5"), 2);//创建分组的弹层
//关闭和消息层
$("#panels2").find(".title01>a").click(function() {
	layer2.hide();
});
$("#panels2").find(".button01").eq(0).click(function() {
	layer2.ensure();
});
$("#panels2").find(".button01").eq(1).click(function() {
	layer3.cancel();
});
$("#panels3").find(".title01>a").click(function() {
	layer3.hide();
});
$("#panels4 a").click(function() {
	layer4.hide();
});
$("#panels5 a.btnClose").click(function() {
	layer5.hide();
});
//下拉列表
$("div.ww").eq(0).select();
$("div.ww").eq(1).select();
$("div.ww").eq(2).select();
$("div.ww").eq(3).select();
var select01 = new select($("div.wl").eq(0));
//更多
$(".setMore1").toggle(function() {
	$(".shidden").show(function() {$(".setMore1").addClass("setMoreLink");});
	
}, function() {
	$(".shidden").hide(function() {$(".setMore1").removeClass("setMoreLink");});
});
//模似单选框
$("#box1>span").click(function() {
	$("#box1 .ra01").removeClass("ra02");
	$(this).find(".ra01").addClass("ra02");
	if($(this).attr("data") == "GET") {
		$(".mget").attr("checked", true);
	}
	if($(this).attr("data") == "POST") {
		$(".mpost").attr("checked", true);
	}
	if($(this).attr("data") == "HEAD") {
		$(".mhead").attr("checked", true);
	}
});
$("#box2>span").click(function() {
	$("#box2 .ra01").removeClass("ra02");
	$(this).find(".ra01").addClass("ra02");
	if($(this).attr("data") == "包含匹配内容") {
		$(".mconton").attr("checked", true);
	} else {
		$(".mnoconton").attr("checked", true);
	}
});
$("#box3>span").click(function() {
	$("#box3 .ra01").removeClass("ra02");
	$(this).find(".ra01").addClass("ra02");
	if($(this).attr("data") == "填写故障处理人") {
		$(".alertman").attr("checked", true);
	} else {
		$(".alertgroup").attr("checked", true);
	}
});
//表单提交
$("#btnSubmit").click(function() {
	if($('input[name="itemname"]').val() == "") {
		layer2.un().show(function(el) {
			el.pan.find("p").html("监控项目名称不能为空！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return false;
	}
	if(!$('input[name="url"]').val().match(/http:\/\/.+/)) {
		layer2.un().show(function(el) {
			el.pan.find("p").html("请填写正确的状态页面URL地址！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return false;
	}
	$('form[name="form1"]').submit();
});


//创建分类
var baseHtml = '<a class="on" href="javascript:void(0)">无</a>';
var classHtml = '<a href="javascript:void(0)" data="{$id}">{$name}</a>';
$("#createGroup").click(function(e) {
	e.preventDefault();
	layer1.un().show(function(el) {
		el.pan.find(".namedata").val("");
		el.pan.find(".namedata").focus();
	});
});
$("#panels1").find(".title01>a").click(function() {
	layer1.hide();
});
$("#panels1").find(".btnCreateGroup").click(function() {
	var name = $("#panels1").find(".namedata").val();
	if(name == "") {
		layer2.un().show(function(el) {
			el.pan.find("p").html("名称不能为空！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(/<(.*)>.*<\/\1>|<(.*) \/>/.test(name)) {
		layer2.un().show(function(el) {
			el.pan.find("p").html("分类名称不能为html代码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(name.length > 16) {
		layer2.un().show(function(el) {
			el.pan.find("p").html("分类名称不能超过16个字！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	$.get(base + "/serviceitemgroup/save.do", {"gname": name}, function(results) {
		results = eval("(" + results + ")");
		if(results.status == 1) {
			layer1.hide();
			layer2.un().show(function(el) {
				el.pan.find("p").html("创建成功");
				el.pan.find(".button01").hide().eq(0).show();
			});
			layer2.on("ensure", function() {
				$.get(base + "/serviceitemgroup/list.do", {}, function(results) {
					results = eval("(" + results + ")");
					var classData = [], data = results.data;
					if(results.status == 1) {
						for(var i = 0; i < data.length; i++ ) {
							classData.push(classHtml.tpl(data[i]));
						}
					}
					$("#open001").html(baseHtml + classData.join(""));
				});
			});
		} else {
			layer1.hide();
			layer2.un().show(function(el) {
				el.pan.find("p").html(results.data);
				el.pan.find(".button01").hide().eq(0).show();
			});
			return;
		}
	});
});
//创建报警组
var baseAlertHtml = '<a class="on" href="javascript:void(0)">无</a>';
var classAlertHtml = '<a href="javascript:void(0)" data="{$id}">{$groupName}</a>';
$("#btnAlertGroup").click(function() {
	layer3.un().show(function(el) {
		el.pan.find(".namedata").val("");
		el.pan.find(".namedata").focus();
	});
	layer3.on("submit", function() {
		var name = $("#panels3").find(".namedata").val();
		$.get(base + "/user/addmonitorgroup.do", {"gname": name}, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				layer3.hide();
				layer2.un().show(function(el) {
					el.pan.find("p").html("创建成功");
					el.pan.find(".button01").hide().eq(0).show();
				});
				layer2.on("ensure", function() {
					$.get(base + "/monitorgroup/list.do", {}, function(results) {
						results = eval("(" + results + ")");
						var classData = [], data = results.data;
						if(results.status == 1) {
							for(var i = 0; i < data.length; i++ ) {
								classData.push(classAlertHtml.tpl(data[i]));
							}
						}
						$("#open002").html(baseAlertHtml + classData.join(""));
					});
				});
			} else {
				layer3.hide();
				layer2.un().show(function(el) {
					el.pan.find("p").html(results.data);
					el.pan.find(".button01").hide().eq(0).show();
				});
				return;
			}
		});
	});
});
$("#panels3").find(".btnCreateGroup").click(function() {
	var name = $("#panels3").find(".namedata").val();
	if(name == "") {
		layer2.un().show(function(el) {
			el.pan.find("p").html("名称不能为空！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(/<(.*)>.*<\/\1>|<(.*) \/>/.test(name)) {
		layer2.un().show(function(el) {
			el.pan.find("p").html("分组名称不能为html代码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(name.length > 16) {
		layer2.un().show(function(el) {
			el.pan.find("p").html("分组名称不能超过16个字！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	layer3.submit();
});
//添加新故障处理人
$("#panels4").find(".btnCreateGroup").click(function() {
	if($(".guzhangMenName").val() == "") {
		layer2.un().show(function(el) {
			el.pan.css({"zIndex":"12"});
			el.pan.find("p").html('故障处理人不能为空！');
			el.pan.find(".button01").hide().eq(0).show();
		});
		return;
	}
	if(!$(".phoneNumber").val().match(/^13[0-9]{9}$|15[01256789]{1}[0-9]{8}$|18[0356789]{1}[0-9]{8}$/)) {
		layer2.un().show(function(el) {
			el.pan.css({"zIndex":"12"});
			el.pan.find("p").html('请输入正确的手机号码！');
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(!$(".emailAddress").val().match(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/)) {
		layer2.un().show(function(el) {
			el.pan.css({"zIndex":"12"});
			el.pan.find("p").html('请输入正确的邮箱！');
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	layer4.submit();
});
$("#jianAddMen").click(function(e) {
	var gid = $("#alertG").val();
	if(!gid.match(/^\d+$/)) {
		layer2.un().show(function(el) {
			el.pan.css({"zIndex":"12"});
			el.pan.find("p").html('请选择报警组');
			el.pan.find(".button01").hide().eq(0).show();
		});
		return;
	}
	$("#panels4 table").eq(0).find("input").val("");
	$("#panels4").find(".btnCreateGroup").val("提交");
	layer4.un().show(function(el) {
		el.pan.find("h4>b").html("添加新故障处理人");
	});
	layer4.on("submit", function() {
		var o = {
			"name": $(".guzhangMenName").val(),
			"cellphone" : $(".phoneNumber").val(),
			"email": $(".emailAddress").val()
		};
		$.get(base + "/user/addmonitor.do", o, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				var o = {
					"gid": gid,
					"data": results.data.id + "@1,2,4"
				};
				$("#box002 tr").each(function(n, el) {
					if($(el).attr("data")) {
						o.data += ";" + $(el).attr("data") + "@" + $(el).attr("resData");
					}
				});
				$.get(base + "/config/reconfigmonitorgroup.do", o, function(results) {
					results = eval("(" + results + ")");
					layer2.un().show(function(el) {
						el.pan.css({"zIndex":"12"});
						el.pan.find("p").html('添加成功！');
						el.pan.find(".button01").hide().eq(0).show();
					});
					layer2.on("ensure", function() {
						var o = {"gid": gid}
						setAlertGroup(o);
					});
				});
			} else {
				layer2.un().show(function(el) {
					el.pan.css({"zIndex":"12"});
					el.pan.find("p").html(results.data);
					el.pan.find(".button01").hide().eq(0).show();
				});
				layer2.un();
			}
		});
	});
});
//添加已有故障处理人
var alertNamResults = {};
$("#jianAddAMen").click(function() {
	var gid = $("#alertG").val();
	if(!gid.match(/^\d+$/)) {
		layer2.un().show(function(el) {
			el.pan.css({"zIndex":"12"});
			el.pan.find("p").html('请选择报警组');
			el.pan.find(".button01").hide().eq(0).show();
		});
		return;
	}
	layer5.un().show(function(el) {
		setPageAlready(1);
	});
});
$("#panels5").delegate("span", "click", function() {
	if($(this).hasClass("ch01")) {
		$(this).removeClass("ch01").addClass("ch02");
		$("#panels5").attr("data", parseInt($("#panels5").attr("data")) + 1);
		alertNamResults[$(this).parent().attr("data")] = $(this).parent().attr("data") + "@1,2,4";
	} else if($(this).hasClass("ch02")) {
		$(this).removeClass("ch02").addClass("ch01");
		$("#panels5").attr("data", parseInt($("#panels5").attr("data")) - 1);
		delete alertNamResults[$(this).parent().attr("data")];
	}
});
$("#panels5").delegate(".btnCreateGroup", "click", function() {
	if($("#panels5").attr("data") == "0") {
		layer2.un().show(function(el) {
			el.pan.css({"zIndex":"12"});
			el.pan.find("p").html("请选择故障处理人");
			el.pan.find(".button01").hide().eq(0).show();
		});
	} else {
		var gid = $("#alertG").val();
		if(!gid.match(/^\d+$/)) {
			layer2.un().show(function(el) {
				el.pan.css({"zIndex":"12"});
				el.pan.find("p").html('请选择报警组');
				el.pan.find(".button01").hide().eq(0).show();
			});
			return;
		}
		var o = {
			"gid": gid,
			"data": ""
		}
		$.each(alertNamResults, function(n, el) {
			o.data = o.data + el + ";";
		});
		$("#box002 tr").each(function(n, el) {
			if($(el).attr("data") && $(el).attr("resData")) {
				o.data += $(el).attr("data") + "@" + $(el).attr("resData") + ";";
			}
		});
		o.data = o.data.replace(/^;|;$/, "");
		$.get(base + "/config/reconfigmonitorgroup.do", o, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				layer5.hide();
				layer2.un().show(function(el) {
					el.pan.find("p").html("修改报警组设置成功");
					el.pan.find(".button01").hide().eq(0).show();;
				});
				layer2.on("ensure", function() {
					var o = {"gid": gid};
					setAlertGroup(o);
				});
			}
		});
	}
});

function setPageAlready(n) {
	var html ='<td width="50%" data="{$id}"><strong class="username" style="display:block;float:left;width:90px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">{$name}</strong><span class="{$className}" style="display:block;width:20px;height:20px;float:left;"></span></td>';
	var htmlFooter = '<tr><td colspan="2"><input class="btnCreateGroup button01" style="margin-left:142px;" name="" type="button" value="提交"></td></tr>';
	setPageAlready.curPage = n;
	$.get(base + "/config/getmonitors.do", {"p": setPageAlready.curPage}, function(results) {
		results = eval("(" + results + ")");
		var resData = [];
		if(results.status == 1) {
			if(results.data.monitors.length < 10){
				setPageAlready.curPage = 0;
			}
			$.each(results.data.monitors, function(n, el) {
				if(alertNamResults[el.id]) {
					el.className = "ch02";
				} else {
					el.className = "ch01";
				}
				if(n%2 == 0) {
					resData.push("<tr>");
					resData.push(html.tpl(el));
				}
				if(n%2 == 1) {
					resData.push(html.tpl(el));
					resData.push("</tr>");
				}
			});
			$("#panels5").find("tbody.pan").html(resData.join("") + htmlFooter);
		}
	});
}
$("#btnChangePage").click(function() {
	var n = setPageAlready.curPage + 1;
	setPageAlready(n);
});
//根据报警组获取报警成员
select01.base ='<tr><td class="fx_teltd">&nbsp;</td><td class="fx_teltd">手机短信</td><td class="fx_teltd">邮件</td><td class="fx_teltd">盛大有你</td></tr>';

select01.pan = [];
select01.pan.push('<tr data="{$id}" resData={$resData}>');
select01.pan.push('	<td>{$name}</td>');
select01.pan.push('	<td><span class="{$chaName1}" data="1"></span></td>');
select01.pan.push('	<td><span class="{$chaName2}" data="2"></span></td>');
select01.pan.push('	<td><span class="{$chaName3}" data="4"></span></td>');
select01.pan.push('</tr>');
select01.pan = select01.pan.join("");
select01.on("change", function(el) {
	var o = {"gid": el.selectedValue};
	setAlertGroup(o);
});
if($("#alertG").val().match(/\d+/)) {
	var o = {"gid": $("#alertG").val()};
	setAlertGroup(o);
}
function setAlertGroup(o) {
	$.get(base + "/groupmonitor/list.do", o, function(results) {
		results = eval("(" + results + ")");
		var html = [];
		if(results.status == 1) {
			$.each(results.data, function(n,  el) {
				if(el.mgm.alarmChannel == 1) {
					el.monitor.chaName1 = "ch02";
					el.monitor.chaName2 = "ch01";
					el.monitor.chaName3 = "ch01";
					el.monitor.resData = "1";
				} else if(el.mgm.alarmChannel == 2) {
					el.monitor.chaName1 = "ch01";
					el.monitor.chaName2 = "ch02";
					el.monitor.chaName3 = "ch01";
					el.monitor.resData = "2";
				} else if(el.mgm.alarmChannel == 3) {
					el.monitor.chaName1 = "ch02";
					el.monitor.chaName2 = "ch02";
					el.monitor.chaName3 = "ch01";
					el.monitor.resData = "1,2";
				} else if(el.mgm.alarmChannel == 4) {
					el.monitor.chaName1 = "ch01";
					el.monitor.chaName2 = "ch01";
					el.monitor.chaName3 = "ch02";
					el.monitor.resData = "4";
				} else if(el.mgm.alarmChannel == 5) {
					el.monitor.chaName1 = "ch02";
					el.monitor.chaName2 = "ch01";
					el.monitor.chaName3 = "ch02";
					el.monitor.resData = "1,4";
				} else if(el.mgm.alarmChannel == 6) {
					el.monitor.chaName1 = "ch01";
					el.monitor.chaName2 = "ch02";
					el.monitor.chaName3 = "ch02";
					el.monitor.resData = "2,4";
				} else if(el.mgm.alarmChannel == 7) {
					el.monitor.chaName1 = "ch02";
					el.monitor.chaName2 = "ch02";
					el.monitor.chaName3 = "ch02";
					el.monitor.resData = "1,2,4";
				} else {
					el.monitor.chaName1 = "ch01";
					el.monitor.chaName2 = "ch01";
					el.monitor.chaName3 = "ch01";
					el.monitor.resData = "0";
				}
				html.push(select01.pan.tpl(el.monitor));
			});
		}
		$("#box002").html(select01.base + html.join(""));
	});
};
//默认报警组
var moren = $("#moren");
if(moren.attr("data") == "1") {
	moren.find("span").eq(0).addClass("ch02");
}
if(moren.attr("data") == "2") {
	moren.find("span").eq(1).addClass("ch02");
}
if(moren.attr("data") == "3") {
	moren.find("span").eq(0).addClass("ch02");
	moren.find("span").eq(1).addClass("ch02");
}
if(moren.attr("data") == "4") {
	moren.find("span").eq(2).addClass("ch02");
}
if(moren.attr("data") == "5") {
	moren.find("span").eq(0).addClass("ch02");
	moren.find("span").eq(2).addClass("ch02");
}
if(moren.attr("data") == "6") {
	moren.find("span").eq(1).addClass("ch02");
	moren.find("span").eq(2).addClass("ch02");
}
if(moren.attr("data") == "7") {
	moren.find("span").eq(0).addClass("ch02");
	moren.find("span").eq(1).addClass("ch02");
	moren.find("span").eq(2).addClass("ch02");
}
</script>
</body>
</html>
