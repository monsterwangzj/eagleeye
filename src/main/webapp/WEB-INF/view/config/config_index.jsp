<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chengyi.eagleeye.model.*,java.util.*" %><% 

List<Monitor> monitors = (List<Monitor>) request.getAttribute("monitors");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");

String pageLinkBase = "/config/index.do?p=";
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<title>监控系统 设置</title>
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
                  <li class="current"><span class="point">&nbsp;</span><a href="/config/index.do">故障处理人</a></li>
                 <li><span class="point">&nbsp;</span><a href="/config/alarmgroup.do">报警组</a></li>
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
              <h4 class="fl">故障信息设置 &gt; <b>故障处理人</b></h4>
              <p id="jianAddMen" class="fr pr"><span><img src="/styles/images/add01.gif" /></span><span><a href="javascript:void(0);">添加故障处理人</a></span></p>
              </div>
              <table id="box01" class="gray" cellpadding="0" cellspacing="0" border="0">   <tr class="tit">
                  <!--  <td>负责人</td>-->
                  <td>故障处理人</td>
                  <td>手机号码</td>
                  <td>邮件地址</td>
                  <td>盛大有你</td>
                  <td colspan="2">操作</td>
              </tr>
              <%
				if (CollectionUtils.isEmpty(monitors)) { %>
					<tr>
	                  <td>-</td>
	                  <td>-</td>
	                  <td>-</td>
	              	  <td>-</td>
	                  <td></td>
	                  <td>-</td>
	                  <td>-</td>
	              </tr>
				<%} else {
					for (Monitor monitor: monitors) {%>
					<tr data="<%=monitor.getId()%>">
	                  <!--  <td><%=monitor.getName()%></td>-->
	                  <td><%=monitor.getName()%></td>
	                  <td><%=monitor.getCellphone()%></td>
	              	  <td><%=monitor.getEmail()%></td>
	                  <td><%=monitor.getCellphone()%></td>
	                  <td><a class="write" href="javascript:void(0);" title="修改"></a></td>
	                  <td><a class="delete" href="javascript:void(0);" title="删除"></a></td>
	              </tr>
					<%}
				}
			  %>
            </table>
            <%if (CollectionUtils.isNotEmpty(monitors) && totalPage > 1) {%>
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
            <%}%>

          </div> 
          
        </div>
      </div>
    </div>
</div>
<div id="panels1" class="w_right" style="width:360px;overflow:hidden;display:none;">
	<div class="w_735" style="width:358px;">
    	<div class="title01">
     		<h4 class="fl">设置 &gt; <b>添加故障处理人</b></h4>
     		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     		<!--<p class="fr pr"><span><img src="/styles/images/add01.gif"></span><span><a href="#">创建监控项目</a></span></p>-->
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
         	<tbody>
         		<tr>
             		<td width="72">故障处理人：</td>
             		<td colspan="2"><input id="guzhangMenName" type="text" style="width:218px;"><font style="margin-left:10px;color:#f00;">*</font></td>
         		</tr>
         		<tr>
             		<td width="72">手机号码：</td>
             		<td colspan="2"><input id="phoneNumber" type="text" style="width:218px;"><font style="margin-left:10px;color:#f00;">*</font></td>
         		</tr>
         		<tr>
             		<td width="72">邮件地址：</td>
             		<td colspan="2"><input id="emailAddress" type="text" style="width:218px;"><font style="margin-left:10px;color:#f00;">*</font></td>
         		</tr>
         		<tr>
         			<td colspan="3" style="height:25px;"></td>
         		</tr>
         		<tr>
             		<td></td>
             		<td width="264" colspan="2"><input id="btnCreateGroup" class="button01" name="" type="button" value="提交"></td>
         		</tr>
     		</tbody>
    	</table>
    </div>
</div>
<div id="panels2" class="w_right" style="width:300px;display:none;">
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
<div id="panels3" class="w_right" style="width:300px;display:none;">
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
	layer1 = new popLayer($("#panels1"), 2),//创建分组的弹层
	layer2 = new popLayer($("#panels2"), 1),//消息框
	layer3 = new popLayer($("#panels3"), 1);//消息框
//layer1层关闭
$("#panels1 a").click(function() {
	layer1.hide();
});
$("#panels2").find("a").click(function() {
	layer2.hide();
});
$("#panels2").find(".button01").eq(0).click(function() {
	layer2.ensure();
});
$("#panels2").find(".button01").eq(1).click(function() {
	layer2.hide();
});
$("#panels3").find("a").click(function() {
	layer3.hide();
});
$("#panels3").find(".button01").eq(0).click(function() {
	layer3.ensure();
});
$("#panels3").find(".button01").eq(1).click(function() {
	layer3.hide();
});
//添加故障处理人
$("#btnCreateGroup").click(function() {
	if($("#guzhangMenName").val() == "") {
		layer2.un().show(function(el) {
			el.pan.find("p").html('故障处理人不能为空！');
			el.pan.find(".button01").hide().eq(0).show();
		});
		return;
	}
	if(!$("#phoneNumber").val().match(/^13[0-9]{9}$|15[01256789]{1}[0-9]{8}$|18[02356789]{1}[0-9]{8}$/)) {
		layer2.un().show(function(el) {
			el.pan.find("p").html('请输入正确的手机号码！');
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(!$("#emailAddress").val().match(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/)) {
		layer2.un().show(function(el) {
			el.pan.find("p").html('请输入正确的邮箱！');
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	layer1.submit();
});
$("#jianAddMen").click(function(e) {
	$("#panels1 table").eq(0).find("input").val("");
	$("#btnCreateGroup").val("提交");
	layer1.un().show(function(el) {
		el.pan.find("h4>b").html("添加故障处理人");
	});
	layer1.on("submit", function() {
		var o = {
			"name": $("#guzhangMenName").val(),
			"cellphone" : $("#phoneNumber").val(),
			"email": $("#emailAddress").val()
		};
		$.get(base + "/user/addmonitor.do", o, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				layer2.un().show(function(el) {
					el.pan.find("p").html('添加成功！');
					el.pan.find(".button01").hide().eq(0).show();
				});
				layer2.on("ensure", function() {
					location.reload();
				});
			} else {
				layer2.un().show(function(el) {
					el.pan.find("p").html(results.data);
					el.pan.find(".button01").hide().eq(0).show();
				});
				layer2.un();
			}
		});
	});
});
//修改故障处理人
$("#box01").delegate("a", "click", function(e) {
	var self = this, _parent = $(self).parent().parent();
	if($(this).hasClass("write")) {
		layer1.un().show(function(el) {
			el.pan.find("h4>b").html("修改故障处理人");
			el.pan.find("#guzhangMenName").val(_parent.find("td").eq(0).html());
			el.pan.find("#phoneNumber").val(_parent.find("td").eq(1).html());
			el.pan.find("#emailAddress").val(_parent.find("td").eq(2).html());
			el.pan.find("#guzhangMenName").focus();
		});
		layer1.on("submit", function() {
			var o = {
				"id": $(self).parent().parent().attr("data"),
				"name": $("#guzhangMenName").val(),
				"cellphone" : $("#phoneNumber").val(),
				"email": $("#emailAddress").val(),
				"weixin": $("#weixinNumber").val()
			};
			$.get(base + "/user/updatemonitor.do", o, function(results) {
				results = eval("(" + results + ")");
				if(results.status == 1) {
					layer2.un().show(function(el) {
						el.pan.find("p").html('修改成功！');
						el.pan.find(".button01").hide().eq(0).show();
					});
					layer2.on("ensure", function() {
						location.reload();
					});
				} else {
					layer2.un().show(function(el) {
						el.pan.find("p").html(results.data);
						el.pan.find(".button01").hide().eq(0).show();
					});
					layer2.un();
				}
			});
		});
	};
	if($(this).hasClass("delete")) {
		layer2.un().show(function(el) {
			el.pan.find("p").html('确认删除这条记录吗？');
			el.pan.find(".button01").show();
		});
		layer2.on("ensure", function() {
			var o = {"id": $(self).parent().parent().attr("data")};
			$.get(base + "/user/delmonitor.do", o, function(results) {
				results = eval("(" + results + ")");
				if(results.status == 1) {
					layer3.un().show(function(el) {
						el.pan.find("p").html('删除成功！');
						el.pan.find(".button01").hide().eq(0).show();
					});
					layer3.on("ensure", function() {
						location.reload();
					})
				} else {
					layer3.un().show(function(el) {
						el.pan.find("p").html(results.data);
						el.pan.find(".button01").hide().eq(0).show();
					});
				}
			});
		});
	}
})
</script>
</body>
</html>
