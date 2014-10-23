<%@ page contentType="text/html;charset=UTF-8"%><%@page import="com.chengyi.eagleeye.model.User,org.apache.commons.lang.StringUtils"%><%
response.setHeader("Cache-Control", "no-cache, must-revalidate");
response.setHeader("Pragma", "no-cache");

String uri = request.getRequestURI();
if (uri == null) uri = "";
%>
<!-- head Start -->
              <div class="w_210 bc">
              <div class="title"><h4>常见问题</h4><p class="fr pr"><span></span></p> </div>
              <ul class="help_list">
                  <li <% if (uri.contains("help01")) out.print("class=\"current\""); %>><span class="point">&nbsp;</span><a href="/help/help01.jsp" target="_self">什么是可用率？</a></li> 
                  <li <% if (uri.contains("help02")) out.print("class=\"current\""); %>><span class="point">&nbsp;</span><a href="/help/help02.jsp" target="_self">什么是响应时间？</a></li>
                  <li <% if (uri.contains("help03")) out.print("class=\"current\""); %>><span class="point">&nbsp;</span><a href="/help/help03.jsp" target="_self">什么是http监控？</a></li>
                  <li <% if (uri.contains("help04")) out.print("class=\"current\""); %> class="noline"><span class="point">&nbsp;</span><a href="/help/help04.jsp">如何创建项目分类，项目分类有何作用？</a></li>
                  <li <% if (uri.contains("help05")) out.print("class=\"current\""); %>><span class="point">&nbsp;</span><a href="/help/help05.jsp" target="_self">什么是监控项？</a></li> 
                  <li <% if (uri.contains("help06")) out.print("class=\"current\""); %>><span class="point">&nbsp;</span><a href="/help/help06.jsp" target="_self">什么是报警组？</a></li>
                  <li <% if (uri.contains("help07")) out.print("class=\"current\""); %>class="noline"><span class="point">&nbsp;</span><a href="/help/help07.jsp" target="_self">报警方式有哪些？</a></li>
                  <li <% if (uri.contains("help08")) out.print("class=\"current\""); %>class="noline"><span class="point">&nbsp;</span><a href="/help/help08.jsp" target="_self">如何设置监控项目？</a></li>
                  <li <% if (uri.contains("help09")) out.print("class=\"current\""); %>class="noline"><span class="point">&nbsp;</span><a href="/help/help09.jsp" target="_self">如何编辑报警组成员？</a></li>
                  <li <% if (uri.contains("help10")) out.print("class=\"current\""); %>class="noline"><span class="point">&nbsp;</span><a href="/help/help10.jsp" target="_self">为什么在火狐浏览器下图表不显示？</a></li>
              </ul>
             </div>
<!-- head End -->