<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chengyi.eagleeye.model.*,java.util.*" %><% 

List<MonitorGroup> userGroups = (List<MonitorGroup>) request.getAttribute("monitorGroups");
HashMap<Long, Long> monitorGroupMap = (HashMap<Long, Long>) request.getAttribute("monitorGroupMap");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");

String pageLinkBase = "/config/alarmgroup.do?p=";
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
              <h4 class="fl">故障信息设置 &gt; <b>报警组</b></h4>
              <p id="box01" class="fr pr"><span><img src="/styles/images/add01.gif"></span><span><a href="#">创建报警组</a></span></p>
              </div>
              <table id="box02" cellspacing="0" cellpadding="0" border="0" class="gray">   
              <tbody>
              <tr class="tit">
                  <td>报警组名称</td>
                  <td colspan="3">操作</td>
              </tr>
              <% 
              if (CollectionUtils.isEmpty(userGroups)) { %>
					<tr>
                  		<td>暂无数据</td>
                   		<td><a href="javascript:void(0)">编辑成员</a></td>
                   		<td><a href="javascript:void(0)">修改名称</a></td>
                  		<td><a href="javascript:void(0)">删除报警组</a></td>
              		</tr>
				<% } else {
					for (MonitorGroup userGroup : userGroups) {%>
						<tr data="<%=userGroup.getId()%>" disable="<%=userGroup.getType()%>">
			            	<td><%=userGroup.getGroupName()%></td>
			                <td width="25%"><a href="/config/setmonitorgroup.do?gid=<%=userGroup.getId()%>" class="none">编辑成员[<%=monitorGroupMap.get(userGroup.getId())%>人]</a></td>
			                <td width="20%"><%if (userGroup.getType() == (byte)1) {%>
			                	<span style="color:gray;">修改名称</span>
			                <%} else {%>
			                	<a href="javascript:void(0)" class="none">修改名称</a>
			                <% }%></td>
			                <td width="20%">
			                	<%if (userGroup.getType() == (byte)1) {%>
				                	<span style="color:gray;">删除报警组</span>
				                <%} else {%>
				                	<a href="javascript:void(0)" class="none">删除报警组</a>
				                <% }%>
			                </td>
			            </tr>
					<%}
				}%>
            </tbody></table>
            
            <%if (CollectionUtils.isNotEmpty(userGroups) && totalPage > 1) {%>
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
     		<h4 class="fl">故障信息设置 &gt; <b>创建报警组</b></h4>
     		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     		<!--<p class="fr pr"><span><img src="/styles/images/add01.gif"></span><span><a href="#">创建监控项目</a></span></p>-->
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
         	<tbody>
         		<tr>
             		<td width="72">报警组名称：</td>
             		<td colspan="2"><input id="namedata" type="text" style="width:218px;"></td>
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

<script type="text/javascript" src="/js/common.js"></script>
<script>
var base = "http://" + location.host,//Ajax请求的基地址
	layer1 = new popLayer($("#panels1"), 2),//创建分组的弹层
	layer2 = new popLayer($("#panels2"), 1),//创建分组的弹层
	layer3 = new popLayer($("#panels3"), 1);//创建分组的弹层
$("#box01").click(function() {
	layer1.un().show(function(el) {
		el.pan.find("#namedata").val("");
		el.pan.find("h4>b").html("创建报警组");
		el.pan.find("#namedata").focus();
	});
	layer1.on("submit", function() {
		var name = $("#namedata").val();
		$.get(base + "/user/addmonitorgroup.do", {"gname": name}, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				layer1.hide();
				layer2.un().show(function(el) {
					el.pan.find("p").html("创建成功");
					el.pan.find(".button01").hide().eq(0).show();
				});
				layer2.on("ensure", function() {
					location.reload();
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
});
$("#panels1").find(".title01>a").click(function() {
	layer1.hide();
});
$("#panels2").find(".title01>a").click(function() {
	layer2.hide();
});
$("#panels2").find(".button01").eq(0).click(function() {
	layer2.ensure();
});
$("#panels2").find(".button01").eq(1).click(function() {
	layer2.hide();
});
$("#panels3").find(".title01>a").click(function() {
	layer3.hide();
});
$("#panels3").find(".button01").eq(0).click(function() {
	layer3.ensure();
});
$("#panels3").find(".button01").eq(1).click(function() {
	layer3.hide();
});
$("#btnCreateGroup").click(function() {
	var name = $("#namedata").val();
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
	layer1.submit();
});
//删除报警组
$("#box02").delegate("a", "click", function(e) {
	var self = this,
		_parent = $(self).parent().parent();
	if($(this).html() == "修改名称") {
		layer1.un().show(function(el) {
			el.pan.find("#namedata").val(_parent.find("td").eq(0).html());
			el.pan.find("h4>b").html("修改报警组名称");
			el.pan.find("#namedata").focus();
		});
		layer1.on("submit", function() {
			var name = $("#namedata").val(),
				gid=$(self).parent().parent().attr("data");
			$.get(base + "/user/renamemonitorgroup.do", {"gname": name, "gid": gid}, function(results) {
				results = eval("(" + results + ")");
				if(results.status == 1) {
					layer1.hide();
					layer2.un().show(function(el) {
						el.pan.find("p").html("修改成功");
						el.pan.find(".button01").hide().eq(0).show();
					});
					layer2.on("ensure", function() {
						location.reload();
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
	}
	if($(this).html() == "删除报警组") {
		layer2.un().show(function(el) {
			el.pan.find("p").html("是否删除该报警组？");
			el.pan.find(".button01").show();
		});
		layer2.on("ensure", function() {
			var name = $("#namedata").val(),
				gid=$(self).parent().parent().attr("data");
			$.get(base + "/user/delmonitorgroup.do", {"gname": name, "gid": gid}, function(results) {
				results = eval("(" + results + ")");
				if(results.status == 1) {
					layer3.un().show(function(el) {
						el.pan.find("p").html("删除成功");
						el.pan.find(".button01").hide().eq(0).show();
					});
					layer3.on("ensure", function() {
						location.reload();
					});
				} else {
					layer3.un().show(function(el) {
						el.pan.find("p").html(results.data);
						el.pan.find(".button01").hide().eq(0).show();
					});
				}
			});
		});
	}
});
</script>
</body>
</html>
