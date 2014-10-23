<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ 
page import="java.util.List,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<title>监控系统 创建项目分类</title>
</head>
<body><%
List<ItemGroup> itemGroups = (List<ItemGroup>) request.getAttribute("itemGroups");
%>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="w_960 pt">
           <div class="w_left">
              <div class="w_210 bc">
              <div class="title"><h4>按分组查看</h4></div>
              <ul class="list">
	               <% if (CollectionUtils.isEmpty(itemGroups)) { %>
	              		<li><span class="point">&nbsp;</span><a href="#">暂无分组</a></li>
	              	<% } else { 
	              		for (ItemGroup itemGroup: itemGroups) { %>
	              			<li><span class="point">&nbsp;</span><a href="#"><%=itemGroup.getName() %></a></li>
	              		<%}
	               } %>
              </ul>
             </div>
          </div>
          <div class="w_right">
          <div class="w_735">
              <div class="title01">
              <h4 class="fl">监控 &gt; <b>创建项目分组</b></h4>
              <p class="fr pr"><span><img src="/styles/images/add01.gif" /></span><span><a href="#">创建监控项目</a></span></p>
              </div>
              <table class="small" cellpadding="0" cellspacing="0" border="0">
                  <tr>
                      <td width="72">组名称：</td>
                      <td colspan="2"><input id="namedata" name="" type="text" /></td>
                  </tr>
                  <tr>
                  	<td colspan="3"></td>
                  </tr>
                  <tr>
                      <td></td><%--colspan="2" 
                      <td width="164"><input class="button01" name="" type="button" value="删除分组" /></td> --%>
                      <td width="264" colspan="2"><input class="button01" name="" type="button" value="创建分组" /></td>
                  </tr>
              </table>
          </div>    
          </div>
      </div>
    </div>
</div>

<script>
(function() {
	var base = "http://" + location.host;
	$(".button01").click(function() {
		AjaxSend();
	});
	$(document).keydown(function(e) {
		if(e.keyCode == 13) {
			AjaxSend();
		}
	});
	function AjaxSend() {
		var name = $("#namedata").val();
		if(name == "") {
			alert("请输入分组名称！");
			return;
		}
		if(/<(.*)>.*<\/\1>|<(.*) \/>/.test(name)) {
			alert("分组名称不能为html代码！");
			return;
		}
		if(name.length > 16) {
			alert("分组名称不能超过16个字！");
			return;
		}
		$.get(base + "/itemgroup/save.do", {"gname": name}, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				if(confirm("创建成功，是否返回监控页面？")){
					location.href = document.referrer;
				}
			} else {
				alert(results.data);
			}
		});
	}
})();
</script>
</body>
</html>
