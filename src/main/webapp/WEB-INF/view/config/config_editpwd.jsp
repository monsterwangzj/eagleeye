<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chengyi.eagleeye.model.*,java.util.*" %><% 

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<title>监控系统 设置</title>
<style>
table.t_set td input{
	color:#fff;
}
.up_success{
	height:50px;
	line-height:50px;
	text-indent:43px;
	background:url(/styles/images/icon1.png) no-repeat 0px 8px;
}
</style>
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
</head>
<body>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="w_960 pt">
           <div class="w_left">
              <div class="w_210 bc">
              <div class="title"><h4>故障信息设置</h4></div>
              <ul class="list">
                  <li><span class="point">&nbsp;</span><a href="/config/index.do">故障处理人</a></li>
                 <li ><span class="point">&nbsp;</span><a href="/config/alarmgroup.do">报警组</a></li>
              </ul>
             </div>
             
             <div class="w_210 mt">
              <div class="title"><h4>个人信息设置</h4></div>
              <ul class="list">
                  <li ><span class="point">&nbsp;</span><a href="/config/editprofile.do">修改个人信息</a></li> 
                  <li class="current"><span class="point">&nbsp;</span><a href="/config/editpwd.do">修改密码</a></li>
              </ul>
             </div>
             
          </div>

          <div class="w_right">
			  <div class="w_735">
              <div class="title01">
              <h4 class="fl">修改个人信息</h4>
              </div>
              <div class="help_word">
               <table class="t_set" cellpadding="0" cellspacing="0" width="600" border="0">
  <tr>
    <td width="110">当前登录密码：</td>
    <td width="390"><input id="currentPasswd" name="currentPasswd" type="password" value=""/></td>
  </tr>
  <tr>
    <td>新密码：</td>
    <td><input id="newPasswd" name="newPasswd" type="password" value=""/></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td style=" font-size:12px; color:#2d6898;">密码必须为6-16位，且至少包含英文、数字和符号中的两种。<br />
      新密码不能与旧密码相同。</td>
  </tr>
  <tr>
    <td>确认新密码：</td>
    <td><input id="newPasswd2" name="newPasswd2" type="password" value=""/></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><input id="subBtn" class="button" name="" type="button" value="保 存" /></td>
  </tr>
</table>

              </div>
              
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
<script type="text/javascript">
var base = "http://" + location.host,
	layer1 = new popLayer($("#panels1"), 1);//消息框
$("#panels1").find(".title01>a").click(function() {
	layer1.hide();
});
$("#panels1").find(".button01").eq(0).click(function() {
	layer1.ensure();
});
$("#subBtn").click(function() {
	var o = {};
	if($("#currentPasswd").val() == "") {
		layer1.un().show(function(el) {
			el.pan.find("p").html("请输入当前登录密码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if($("#newPasswd").val() == "") {
		layer1.un().show(function(el) {
			el.pan.find("p").html("请输入新登录密码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if($("#newPasswd2").val() == "") {
		layer1.un().show(function(el) {
			el.pan.find("p").html("请再次输入新登录密码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if($("#newPasswd").val() != $("#newPasswd2").val()) {
		layer1.un().show(function(el) {
			el.pan.find("p").html("新密码输入不一致！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	o.currentPasswd = $("#currentPasswd").val();
	o.newPasswd = $("#newPasswd").val();
	o.newPasswd2 = $("#newPasswd2").val();
	$.get(base + "/config/updatepwd.do", o, function(results) {
		results = eval("(" + results + ")");
		if(results.status == 1) {
			$(".help_word").html('<p class="up_success">密码修改成功</p>');
		} else {
			layer1.un().show(function(el) {
				el.pan.find("p").html(results.data);
				el.pan.find(".button01").hide().eq(0).show();;
			});
		}
	});
});
</script>
</body>
</html>
