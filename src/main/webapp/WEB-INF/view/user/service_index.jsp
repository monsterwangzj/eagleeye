<%@page import="net.sf.json.JSONObject"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ 
page import="java.util.List,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils,java.util.HashMap,java.util.Map" 
%><%
List<ServiceItemGroup> itemGroups = (List<ServiceItemGroup>) request.getAttribute("serviceItemGroups");
List<Item> items = (List<Item>) request.getAttribute("items");
HashMap<Long, Long> itemGroupMap = (HashMap<Long, Long>) request.getAttribute("serviceItemGroupMap");
HashMap<Long, Long> itemTypeMap = (HashMap<Long, Long>) request.getAttribute("itemTypeMap");

Long currentPage = (Long) request.getAttribute("currentPage");
Long totalPage = (Long) request.getAttribute("totalPage");

Long gid = (Long) request.getAttribute("gid");
String tid = (String) request.getAttribute("tid");
String pageTitle = "全部项目";
if (gid != null) {
	pageTitle = "所选项目";
}
System.out.println("itemGroupMap:" + itemGroupMap);
System.out.println("itemTypeMap:" + itemTypeMap);

String pageLinkBase = null;
if (tid == null) {
	if (gid == null) {
		pageLinkBase = "/monitor/service.do?p=";
	} else {
		pageLinkBase = "/monitor/service.do?g=" + gid + "&p=";
	}
} else {
	pageLinkBase = "/monitor/service.do?tid=" + tid + "&p=";
}

// rateMap
Map<Long, Double[]> rateMap = (Map<Long, Double[]>) request.getAttribute("rateMap");

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<title>监控系统 <%=pageTitle%></title>
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<style>
table.gray td.font-left{text-align:left;}
</style>
</head>
<body>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="w_960 pt">
           <div class="w_left">
             <div class="w_210 bc">
              <div class="title"><h4>服务性能监控</h4></div>
              <ul id="deleteGroup" class="list01">
                  <li data="Nginx" <%if (tid != null && tid.equals("nginx")) {out.print(" class='current'");} %>>
            	      <span class="point">&nbsp;</span>
            		  <a href="/monitor/service.do?tid=nginx">Nginx (<%=(itemTypeMap.get(new Long(Item.TYPE_NGINX)) == null ? 0 : itemTypeMap.get(new Long(Item.TYPE_NGINX)))%>)</a>
            	  </li>
            	  
            	  
            	  <li data="Resin" <%if (tid != null && tid.equals("redis")) {out.print(" class='current'");} %>>
            		  <span class="point">&nbsp;</span>
            		  <a href="/monitor/service.do?tid=redis">Redis (<%=(itemTypeMap.get(new Long(Item.TYPE_REDIS)) == null ? 0 : itemTypeMap.get(new Long(Item.TYPE_REDIS)))%>)</a>
            	  </li>
            	 
            	  <%--
            	  <li data="Apache" <%if (tid != null && tid.equals("apache")) {out.print(" class='current'");} %>>
            		  <span class="point">&nbsp;</span>
            		  <a href="/monitor/service.do?tid=apache">Apache (<%=(itemTypeMap.get(new Long(Item.TYPE_APACHE)) == null ? 0 : itemTypeMap.get(new Long(Item.TYPE_APACHE)))%>)</a>
            	  </li>
            	  <li data="Resin" <%if (tid != null && tid.equals("resin")) {out.print(" class='current'");} %>>
            		  <span class="point">&nbsp;</span>
            		  <a href="/monitor/service.do?tid=resin">Resin (<%=(itemTypeMap.get(new Long(Item.TYPE_RESIN)) == null ? 0 : itemTypeMap.get(new Long(Item.TYPE_RESIN)))%>)</a>
            	  </li>
            	  
            	  <li data="Tomcat">
            		  <span class="point">&nbsp;</span>
            		  <a href="#">Tomcat (2)</a>
            	  </li>
            	  <li data="Memcache">
            		  <span class="point">&nbsp;</span>
            		  <a href="#">Memcache (2)</a>
            	  </li>
            	  
            	  <li data="MongoDB">
            		  <span class="point">&nbsp;</span>
            		  <a href="#">MongoDB (2)</a>
            	  </li>
            	  <li data="MySQL">
            		  <span class="point">&nbsp;</span>
            		  <a href="#">MySQL (2)</a>
            	  </li> --%>
             </ul>
             </div>
             &nbsp;<br/>
             <div class="w_210 bc">
              <div class="title"><h4>服务监控分类</h4></div>
              <ul id="createGroup" class="list no">
                  <li><span><img src="/styles/images/add.gif" /></span><a href="/serviceitemgroup/new.do">创建自定义分类</a></li>
              </ul>
              <ul id="deleteGroup" class="list01">
              	<% if (CollectionUtils.isEmpty(itemGroups)) { %>
              		<li><span class="point">&nbsp;</span><a href="#">暂无分类</a></li>
              	<% } else { %><%--
              		<li><span class="point">&nbsp;</span><a href="/monitor/index.do">全部 (12)</a></li> --%>
              		<%for (ServiceItemGroup itemGroup: itemGroups) { %>
              		    
              			<li data="<%=itemGroup.getId()%>">
              				<span class="point">&nbsp;</span>
              				<a href="/monitor/service.do?g=<%=itemGroup.getId()%>"><%=itemGroup.getName()%> (<%=itemGroupMap.get(itemGroup.getId())%>)</a>
              				<a class="set" href="/serviceitemgroup/edit.do?gid=<%=itemGroup.getId()%>" data="<%=itemGroup.getName() %>" title="修改">&nbsp;</a>
              				<a class="de" title="删除" href="javascript:void(0)"></a>
              			</li>
              		<%}
               } %>
              </ul>
             </div>
            
          </div>
          <div class="w_right">
          <div class="w_735">
              <div class="title01">
              <h4 class="fl">监控 &gt; <b><%=pageTitle%></b></h4>
              <p class="fr pr" id="createItem"><span><img src="/styles/images/add01.gif" /></span><span><a href="/item/new.do">创建监控项目</a></span></p>
              </div>
              <table id="box01" class="gray" cellpadding="0" cellspacing="0" border="0">   <tr class="tit">
                  <td width="25%">项目名称</td>
                  <td width="13%">项目类型</td>
                  <td width="37%">URL</td>
                  <td width="10%">监控频率</td>

                  <td colspan="8" width="10%">操作</td>
              </tr>
              <% if (CollectionUtils.isEmpty(items)) { %>
			  <tr>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td><a class="pause" style="display:none" href="javascript:void(0)"></a></td>
                  <td><a class="editor" style="display:none" href="javascript:void(0)"></a></td>
                  <td><a class="delete" style="display:none" href="javascript:void(0)"></a></td>
              </tr>
			  <% } else {
				  	for (Item item: items) { 
				  	Double[] ratearr = rateMap.get(item.getId());
				  	String rate1 = "-", avgResponse = "-";
				  	if (ratearr != null && ratearr.length == 2) {
				  		rate1 = ratearr[0] + "%";
				  		if (ratearr[1] != null)
				  			avgResponse = ratearr[1] + "ms";
				  	}
				  	
				  	String url = item.getUri();
				  	if (StringUtils.isEmpty(url)) {
				  		String options = item.getOptions();
				  		if (StringUtils.isNotEmpty(options)) {
				  			JSONObject jobj = JSONObject.fromObject(options);
				  			url = jobj.getString("redisServerIp") + ":" + jobj.getString("redisServerPort");
				  		}
				  	}
				  	int length = url.length();
				  	
				  	String itemType = "";
				  	String linkUrl = "/item/usability2.do?itemId=" + item.getId();
				  	if (item.getType() == Item.TYPE_NGINX) {
				  		linkUrl = "/nginxitem/summary.do?itemId=" + item.getId();
				  		itemType = "nginx";
				  	} else if (item.getType() == Item.TYPE_REDIS) {
				  		linkUrl = "/redisitem/summary.do?itemId=" + item.getId();
				  		itemType = "redis";
				  	}
				  	%>
				  	<tr data="<%=item.getId()%>">
	                  <td class="name">
	                      <div><a class="none" href="<%=linkUrl%>" target="_self"><%=item.getName()%></a></div>
	                  </td>
	                  <td><%=itemType%></td>
	                  <td class="url"><div><%=url%></div></td>
	                  <td><%=item.getMonitorFreq()%>s</td>
	                  
	                  <% if (item.getStatus() == Item.STATUS_NORMAL) {%>
	                 	 <td><a class="pause" href="javascript:void(0)" title="暂停"></a></td>
	                  <% } else {%>
	                 	 <td><a class="over" href="javascript:void(0)" title="启动监控"></a></td>
	                  <%} %>
	                  
	                  <%
	                  String editLink = "/nginxitem/edit.do?id=" + item.getId();
	                  if (item.getType() == Item.TYPE_REDIS) {
	                	  editLink = "/redisitem/edit.do?id=" + item.getId();
					  }
					  %>
	                  <td><a class="editor" href="<%=editLink%>" title="编辑"></a></td>
	                  <td><a class="delete" href="javascript:void(0)" title="删除"></a></td>
	                </tr>
				  	<% }
			  } %>
              </table>
              <%if (CollectionUtils.isNotEmpty(items) && totalPage > 1) {%>
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
<div id="panels1" class="w_right" style="width:360px;overflow:hidden;display:none;">
	<div class="w_735" style="width:358px;">
    	<div class="title01">
     		<h4 class="fl">监控 &gt; <b>创建项目分类</b></h4>
     		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     		<!--<p class="fr pr"><span><img src="/styles/images/add01.gif"></span><span><a href="#">创建监控项目</a></span></p>-->
     	</div>
     	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
         	<tbody>
         		<tr>
             		<td width="72">组名称：</td>
             		<td colspan="2"><input id="namedata" type="text" style="width:218px;"/></td>
         		</tr>
         		<tr>
         			<td colspan="3" style="height:25px;"></td>
         		</tr>
         		<tr>
             		<td></td>
             		<td width="264" colspan="2"><input id="btnCreateGroup" class="button01" name="" type="button" value="创建分类"/></td>
         		</tr>
     		</tbody>
    	</table>
    </div>
</div>
<div id="panels2" class="w_right" style="width:360px;display:none;">
	<div class="w_735" style="width:358px;">
    	<div class="title01">
      		<h4 class="fl">监控 &gt; <b>编辑项目分类</b></h4>
      		<a href="javascript:void(0);" style="margin-right:6px;float:right;">关闭</a>
     		<!--<p class="fr pr"><span><img src="/styles/images/add01.gif"></span><span><a href="#">创建监控项目</a></span></p>-->
      	</div>
      	<table class="small" cellpadding="0" cellspacing="0" border="0" style="width:320px;margin:32px auto;overflow:hidden;">
        	<tbody><tr>
              	<td width="16%">组名称：</td>
              	<td colspan="2"><input id="groupName1" name="groupName" type="text" value="V系统" /></td>
          	</tr>
          	<tr>
          		<td colspan="3" style="height:25px;"></td>
          	</tr>
          	<tr>
              <td></td>
              <td width="164"><input id="gdelete" class="button01" name="" type="button" value="删除分类" /></td>
              <td width="264"><input id="gchange" class="button01" name="" type="button" value="修改分类" /></td>
          	</tr>
      	</tbody>
     </table>
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
      		<input class="button01" name="" type="button" value="确定" />
      		<input class="button01" name="" type="button" value="取消" />
      	</div>
  </div>
</div>
<div id="panels4" class="w_right" style="width:300px;display:none;">
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

<div id="panels5" class="tanchu" style="display:none;">
	<ul class="lv_tab">
		<li id="web"><a href="#">网站监控</a></li>
		<li id="service"><a class="on" href="#">服务性监控</a></li>
	</ul>
	
	<div class="lvtab_con">
	    <div class="tab_con01" style="display:none;">
	    	<a class="close" href="#">x</a>
	    	<p>请选择您创建的类别：</p>
			<ul>
		        <li><a href="/item/new.do">Http</a></li>
		        <li><a href="/item/newping.do">Ping</a></li> 
		    </ul>
	    </div>

		<div class="tab_con02" >
			<a class="close" href="#">x</a>
			<p>请选择您创建的类别：</p>
			<ul>
	       	 	<li><a href="/nginxitem/new.do">Nginx</a></li>
	       	 	<li><a href="/redisitem/new.do">Redis</a></li>
	       	 	<%-- 
	       	 	<li><a href="#">Apache</a></li>
	       	 	<li><a href="#">Resin</a></li>
	       	 	<li><a href="#">Tomcat</a></li>
		        <li><a href="#">Memcache</a></li>
		        <li><a href="#">MongoDB</a></li>
		        <li><a href="#">MySQL</a></li>--%>
	    	</ul>
	    </div>
	</div>
</div>


<script type="text/javascript">
var base = "http://" + location.host,//Ajax请求的基地址
	layer1 = new popLayer($("#panels1")),//创建分类的弹层
	layer2 = new popLayer($("#panels2")),//修改分类的弹层
	layer3 = new popLayer($("#panels3"), 1),//消息框
	layer4 = new popLayer($("#panels4"), 1);//消息框
	layer5 = new popLayer($("#panels5"), 1);//创建项目的弹出层
//关闭和消息层
$("#panels3").find(".title01>a").click(function() {
	layer3.hide();
});
$("#panels3").find(".button01").eq(0).click(function() {
	layer3.ensure();
});
$("#panels3").find(".button01").eq(1).click(function() {
	layer3.cancel();
});
$("#panels4").find(".title01>a").click(function() {
	layer4.hide();
});
$("#panels4").find(".button01").eq(0).click(function() {
	layer4.ensure();
});
//当前分类样式高亮
var gid = query("g");
$("#deleteGroup>li").each(function(n, el) {
	if($(el).attr("data")== gid) {
		$(el).addClass("current");
	}
});
//创建分类
$("#createGroup>li").click(function(e) {
	e.preventDefault();
	layer1.un().show(function(el) {
		el.pan.find("#namedata").val("");
		el.pan.find("#namedata").focus();
	});
});
$("#panels1").find(".title01>a").click(function() {
	layer1.hide();
});
$("#btnCreateGroup").click(function() {
	var name = $("#namedata").val();
	if(name == "") {
		layer3.un().show(function(el) {
			el.pan.find("p").html("名称不能为空！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(/<(.*)>.*<\/\1>|<(.*) \/>/.test(name)) {
		layer3.un().show(function(el) {
			el.pan.find("p").html("分类名称不能为html代码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(name.length > 16) {
		layer3.un().show(function(el) {
			el.pan.find("p").html("分类名称不能超过16个字！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	$.get(base + "/serviceitemgroup/save.do", {"gname": name}, function(results) {
		results = eval("(" + results + ")");
		if(results.status == 1) {
			layer1.hide();
			layer3.un().show(function(el) {
				el.pan.find("p").html("创建成功");
				el.pan.find(".button01").hide().eq(0).show();
			});
			layer3.on("ensure", function() {
				location.reload();
			});
		} else {
			layer1.hide();
			layer3.un().show(function(el) {
				el.pan.find("p").html(results.data);
				el.pan.find(".button01").hide().eq(0).show();
			});
			return;
		}
	});
});
//修改分类
$("#deleteGroup a.set").click(function(e) {
	var self = this;
	e.preventDefault();
	layer2.gid = query("gid", $(this).attr("href")),
	layer2.name =$(this).attr("data");
	layer2.pan.find("#groupName1").val("");
	layer2.un().show(function(o) {
		$("#groupName1").val($(self).attr("data"));
		$("#groupName1").focus();
	});
});
$("#deleteGroup a.de").click(function(e) {
	var gid = query("gid", $(this).parent().find("a.set").attr("href"));
	e.preventDefault();
	layer3.un().show(function(el) {
		el.pan.find("p").html("是否要删除该分类？");
		el.pan.find(".button01").show();
	});
	layer3.on("ensure", function() {
		$.get(base + "/serviceitemgroup/del.do", {"gid": gid}, function(results) {
			results = eval("(" + results + ")");
			if(results.status == 1) {
				layer4.un().show(function(el) {
					el.pan.find("p").html("删除分类成功");
					el.pan.find(".button01").hide().eq(0).show();
				});
				layer4.on("ensure", function() {
					location.reload();
				});
				return;
			} else {
				layer3.un().show(function(el) {
					el.pan.find("p").html(results.data);
					el.pan.find(".button01").hide().eq(0).show();;
				});
			}
		});
	});
});
$("#panels2").find(".title01>a").click(function() {
	layer2.hide();
});
$("#gchange").click(function() {
	var name = $("#groupName1").val();
	if(name == "") {
		layer3.un().show(function(el) {
			el.pan.find("p").html("请输入分类名称！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(/<(.*)>.*<\/\1>|<(.*) \/>/.test(name)) {
		layer3.un().show(function(el) {
			el.pan.find("p").html("分类名称不能为html代码！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	if(name.length > 16) {
		layer3.un().show(function(el) {
			el.pan.find("p").html("分类名称不能超过16个字！");
			el.pan.find(".button01").hide().eq(0).show();;
		});
		return;
	}
	$.get(base + "/serviceitemgroup/update.do", {"gname": name, "gid": layer2.gid}, function(results) {
		results = eval("(" + results + ")");
		if(results.status == 1) {
			layer2.hide();
			layer3.un().show(function(el) {
				el.pan.find("p").html("修改分类成功");
				el.pan.find(".button01").hide().eq(0).show();
			});
			layer3.on("ensure", function() {
				location.reload();
			});
		} else {
			layer2.hide();
			layer3.un().show(function(el) {
				el.pan.find("p").html(results.data);
				el.pan.find(".button01").hide().eq(0).show();
			});
			return;
		}
	});
});
$("#gdelete").click(function() {
	$.get(base + "/serviceitemgroup/del.do", {"gid": layer2.gid}, function(results) {
		results = eval("(" + results + ")");
		if(results.status == 1) {
			layer2.hide();
			layer3.un().show(function(el) {
				el.pan.find("p").html("删除分类成功");
				el.pan.find(".button01").hide().eq(0).show();;
			});
			layer3.on("ensure", function() {
				location.reload();
			});
			return;
		} else {
			layer3.un().show(function(el) {
				el.pan.find("p").html(results.data);
				el.pan.find(".button01").hide().eq(0).show();;
			});
		}
	});
});
//项目操作
$("#box01").delegate("a", "click", function(e) {
	var id = $(this).parent().parent().parent().attr("data");
	if (!id) {
		id = $(this).parent().parent().attr("data");
	}
	if(id) {
		if($(this).hasClass("delete")) {
			layer3.un().show(function(el) {
				el.pan.find("p").html("是否删除监控项？");
				el.pan.find(".button01").show();
			});
			layer3.on("ensure", function() {
				$.get(base + "/item/delete.do", {"id": id}, function(results) {
					results = eval("(" + results + ")");
					if(results.status == 1) {
						layer4.un().show(function(el) {
							el.pan.find("p").html("删除项目成功");
							el.pan.find(".button01").hide().eq(0).show();
						});
						layer4.on("ensure", function() {
							location.reload();
						});
					} else {
						layer4.un().show(function(el) {
							el.pan.find("p").html(results.data);
							el.pan.find(".button01").hide().eq(0).show();;
						});
						layer4.un();
					}
				});
			});
		}
		if($(this).hasClass("pause")) {
			layer3.un().show(function(el) {
				el.pan.find("p").html("是否暂停监控项？");
				el.pan.find(".button01").show();
			});
			layer3.on("ensure", function() {
				$.get(base + "/item/freeze.do", {"id": id}, function(results) {
					results = eval("(" + results + ")");
					if(results.status == 1) {
						layer4.un().show(function(el) {
							el.pan.find("p").html("暂停项目成功");
							el.pan.find(".button01").hide().eq(0).show();
						});
						layer4.on("ensure", function() {
							location.reload();
						});
					} else {
						layer4.un().show(function(el) {
							el.pan.find("p").html(results.data);
							el.pan.find(".button01").hide().eq(0).show();
						});
						layer4.un();
					}
				});
			});
		}
		if($(this).hasClass("over")) {
			layer3.un().show(function(el) {
				el.pan.find("p").html("是否启动监控项？");
				el.pan.find(".button01").show();
			});
			layer3.on("ensure", function() {
				$.get(base + "/item/enable.do", {"id": id}, function(results) {
					results = eval("(" + results + ")");
					if(results.status == 1) {
						layer4.un().show(function(el) {
							el.pan.find("p").html("监控项目已经启动");
							el.pan.find(".button01").hide().eq(0).show();
						});
						layer4.on("ensure", function() {
							location.reload();
						});
					} else {
						layer4.un().show(function(el) {
							el.pan.find("p").html(results.data);
							el.pan.find(".button01").hide().eq(0).show();
						});
						layer4.un(s);
					}
				});
			});
		}
	} else {
		layer3.un().show(function(el) {
			el.pan.find("p").html("项目为空！");
			el.pan.find(".button01").hide().eq(0).show();
		});
	}
});

//创建监控项目
$("#createItem").click(function(e) {
	e.preventDefault();
	layer5.un().show(function(el) {
	});
});
$("#panels5").find("li").mouseover(function(e) {
	e.preventDefault();
	$(this).children("a").addClass("on");
	
	if ($(this).attr("id") == 'web') {
		$(".tab_con01").show();
		$(".tab_con02").hide();
		
		$("#service").children("a").removeClass("on");
	} else if ($(this).attr("id") == 'service') {
		$(".tab_con01").hide();
		$(".tab_con02").show();
		
		$("#web").children("a").removeClass("on");
	}
	
});
$("#panels5").find("li").mouseout(function(e) {
	e.preventDefault();
	var id = $(this).attr("id");
	if (id == 'web' || id == 'service') {
		return;
	} else {
		$(this).children("a").removeClass("on");	
	}
	
});
$(".lv_tab").find("li").click(function(e) {
	e.preventDefault();
	if ($(this).attr("id") == 'web') {
		$(".tab_con01").show();
		$(".tab_con02").hide();
	} else if ($(this).attr("id") == 'service') {
		$(".tab_con01").hide();
		$(".tab_con02").show();
	}
});
$(".close").click(function(e) {
	e.preventDefault();
	layer5.un().hide(function(el) {
	});
});
</script>
</body>
</html>
