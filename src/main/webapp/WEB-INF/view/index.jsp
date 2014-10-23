<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%><%@ page import="com.chengyi.eagleeye.network.nginx.*,com.chengyi.eagleeye.network.redis.*,com.chengyi.eagleeye.network.ping.*,com.chengyi.eagleeye.util.CommonUtil,com.chengyi.eagleeye.network.http.HttpResult,java.text.NumberFormat,java.util.*,com.chengyi.eagleeye.util.*,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
%><%
Map<Long, Object[]> rateMap = (Map<Long, Object[]>) request.getAttribute("rateMap");
Map<String, Long> statusMap = (Map<String, Long>) request.getAttribute("statusMap");

List<BreakDownHistory> alarms = (List<BreakDownHistory>) request.getAttribute("alarms");
Map<Long, Item> itemMap = (Map<Long, Item>) request.getAttribute("itemMap");

List<BreakDownHistory> notResumeList = (List<BreakDownHistory>) request.getAttribute("notResumeList");
Map<Long, Item> notResumeItemMap = (Map<Long, Item>) request.getAttribute("notResumeItemMap");

List<Item> downingItems = (List<Item>) request.getAttribute("downingItems");
List<Item> instableItems = (List<Item>) request.getAttribute("instableItems");

Long normalCount = statusMap.get(Long.toString(ServerStatus.OK)) == null ? 0L : statusMap.get(Long.toString(ServerStatus.OK));
normalCount += statusMap.get(Long.toString(ServerStatus.UNKNOWN)) == null ? 0L : statusMap.get(Long.toString(ServerStatus.UNKNOWN));
Long downCount = statusMap.get(Long.toString(ServerStatus.DOWN)) == null ? 0L : statusMap.get(Long.toString(ServerStatus.DOWN));
Long instableCount = statusMap.get(Long.toString(ServerStatus.INSTABLE)) == null ? 0L : statusMap.get(Long.toString(ServerStatus.INSTABLE));
Long totalCount = normalCount + downCount + instableCount;

String nowtimeStr = DateUtil.friendlyTimestamp(new Date());
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="/graphics/style.css" type="text/css" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script language="JavaScript" src="/graphics/FusionCharts.js"></script>
<title>监控系统概述</title>
</head>
<body>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
         <div class="w_960 pt">
           <div class="w_left">
           <div class="w_210 bc">
              <div class="title"><h4>个人信息</h4></div>
              <ul class="list">
                  <li>用户名：<%=loginUser.getName() %></li> 
                  <li style=" background-color:#093154; height:auto; color:#e69934; padding:5px;">
                  <% if (CollectionUtils.isEmpty(downingItems) && CollectionUtils.isEmpty(instableItems)) {
                    if (totalCount == 0) {%>
                    	<font color="#A5D8FF">你好，欢迎登录监控系统，你没有可用的监控项目，<br/></font><a href="#" id="createItem" style="float: none;display:inline;color: #E69934;">点此处添加监控项目</a>
                    <%} else {%>
                    	你好，所有项目都运行良好！<br/>
                    <%}%>
                  <% } else {
                	  for (Item item: downingItems) {%>
                		  <%=item.getName() %> is down!<br/>
                	      <%
                	  }
                	  for (Item item: instableItems) {%>
                	      <%=item.getName() %> is instable!<br/>
                	  	  <%
                	  }
                  }%>
                  </li>
              </ul>
             </div>
              <div class="w_210 mt">
              <div class="title"><h4>常见问题</h4><p class="fr pr"><span><a href="/help/help01.jsp">更多</a></span></p> </div>
              <ul class="list">
                  <li><span class="point">&nbsp;</span><a href="/help/help01.jsp" target="_help">1、什么是可用率？</a></li> 
                  <li><span class="point">&nbsp;</span><a href="/help/help02.jsp" target="_help">2、什么是响应时间？</a></li>
                   <li><span class="point">&nbsp;</span><a href="/help/help07.jsp" target="_help">3、告警消息有哪些类型？</a></li> 
                  <li><span class="point">&nbsp;</span><a href="/help/help08.jsp" target="_help">4、如何设置监控项目？</a></li>
                  <li><span class="point">&nbsp;</span><a href="/help/help04.jsp" target="_help">5、什么是监控分类？</a></li>
              </ul>
             </div>
             
          </div>
          <div class="w_right">
          <div class="w_735">
              <div class="title01">
              <h4 class="fl">监控项目状态统计</h4>
              <%-- 
              <p class="fr pr"><span><a href="/monitor/index.do">所有监控项目</a></span></p>--%>
              </div>
              <%
	      		String strXML="";
			    Double normalRate= 0.0, downRate= 0.0, unstableRate = 0.0;
				if (totalCount > 0) {
				    strXML += "<chart caption='运行状态统计 [" + nowtimeStr + "]' baseFontColor='0088FF' bgColor='2F2F2F,2F2F2F' bgAlpha='100,100' use3DLighting='0' animation='1'>";
				       				
				    strXML += "<set label='DOWN' value='" + downCount +"' color='BF0000' link='/alarm/notresume.do'/>";
				    strXML += "<set label='OK' value='" + normalCount + "' color='008900' />";
				    strXML += "<set label='INSTABLE' value='" + instableCount + "' color='F2AC0C' link='/alarm/notresume.do'/>";
				   
				    strXML += "<styles>";
				    strXML += "<definition>";
				    strXML += "<style name='CaptionFont' type='FONT' size='12' bold='1' />";
				    strXML += "<style name='LabelFont' type='FONT' color='2E4A89' bgColor='FFFFFF' bold='1' />";
				    strXML += "<style name='ToolTipFont' type='FONT' bgColor='2E4A89' borderColor='2E4A89' />";
				    strXML += "</definition>";
				    
				    strXML += "<definition>";
				    strXML += "<apply toObject='CAPTION' styles='CaptionFont' />";
				    strXML += "<apply toObject='DATALABELS' styles='LabelFont' />";
				    strXML += "<apply toObject='TOOLTIP' styles='ToolTIpFont' />";
				    strXML += "</definition>";
				    strXML += "</styles>";
				    

			     
				    strXML += "</chart>";

				   	normalRate = CommonUtil.get2pDouble(normalCount * 100. / totalCount);
				   	downRate = CommonUtil.get2pDouble(downCount * 100. / totalCount);
				   	// unstableRate = 100 - normalRate - downRate;
				   	unstableRate = CommonUtil.get2pDouble(instableCount * 100. / totalCount);
				    
				} else {
					 strXML = "<chart caption='运行状态统计 [" + nowtimeStr + "]' baseFontColor='FFFFFF' bgColor='2F2F2F,2F2F2F' bgAlpha='100,100' use3DLighting='0' animation='1' palette='0' showLabels='0' showValues='0' showToolTip='0' showPercentInToolTip='0' pieRadius='180'>";
					 strXML += "<set displayValue='没有可用的监控项目' value='1' color='00759B'  />";
					 strXML += "</chart>";
				}
				
				// System.out.println(strXML + "\n");
              %>
              <table class="tu" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 0px;margin-top: 10px;">
               <tr><td colspan="2">
               	 <jsp:include page="/fushionchartxt/FusionChartsHTMLRenderer.jsp" flush="true">
                 <jsp:param name="chartSWF" value="/fushionchartxt/Pie3D-3.2.swf?PBarLoadingText=loading..." />
                 <jsp:param name="strURL" value="" />
                 <jsp:param name="strXML" value="<%=strXML %>" />
                 <jsp:param name="chartId" value="myFirst" />
                 <jsp:param name="chartWidth" value="740" />
                 <jsp:param name="chartHeight" value="250" />
                 <jsp:param name="debugMode" value="false" />
                 <jsp:param name="wMode" value="Opaque" />
                 <jsp:param name="style" value="margin-bottom: -15px;" />
                 </jsp:include>
               </td>
               </tr>  
               <tr align="center" bgcolor="2F2F2F">
               <%
               System.out.println("statusMap:" + statusMap);
               Long httpOkCount = statusMap.get(Item.TYPE_HTTP + "-" + ServerStatus.OK); if (httpOkCount == null) httpOkCount = 0L;
               httpOkCount += statusMap.get(Item.TYPE_HTTP + "-" + ServerStatus.UNKNOWN) == null ? 0L : statusMap.get(Item.TYPE_HTTP + "-" + ServerStatus.UNKNOWN);
               Long httpDownCount = statusMap.get(Item.TYPE_HTTP + "-" + ServerStatus.DOWN); if (httpDownCount == null) httpDownCount = 0L;
               Long httpInstableCount = statusMap.get(Item.TYPE_HTTP + "-" + ServerStatus.INSTABLE); if (httpInstableCount == null) httpInstableCount = 0L;
               
               Long pingOkCount = statusMap.get(Item.TYPE_PING + "-" + ServerStatus.OK); if (pingOkCount == null) pingOkCount = 0L;
               pingOkCount += statusMap.get(Item.TYPE_PING + "-" + ServerStatus.UNKNOWN) == null ? 0L : statusMap.get(Item.TYPE_PING + "-" + ServerStatus.UNKNOWN);
               Long pingDownCount = statusMap.get(Item.TYPE_PING + "-" + ServerStatus.DOWN); if (pingDownCount == null) pingDownCount = 0L;
               Long pingInstableCount = statusMap.get(Item.TYPE_PING + "-" + ServerStatus.INSTABLE); if (pingInstableCount == null) pingInstableCount = 0L;
               
               Long nginxOkCount = statusMap.get(Item.TYPE_NGINX + "-" + ServerStatus.OK); if (nginxOkCount == null) nginxOkCount = 0L;
               nginxOkCount += statusMap.get(Item.TYPE_NGINX + "-" + ServerStatus.UNKNOWN) == null ? 0L : statusMap.get(Item.TYPE_NGINX + "-" + ServerStatus.UNKNOWN);
               Long nginxDownCount = statusMap.get(Item.TYPE_NGINX + "-" + ServerStatus.DOWN); if (nginxDownCount == null) nginxDownCount = 0L;
               Long nginxInstableCount = statusMap.get(Item.TYPE_NGINX + "-" + ServerStatus.INSTABLE); if (nginxInstableCount == null) nginxInstableCount = 0L;
               
               Long redisOkCount = statusMap.get(Item.TYPE_REDIS + "-" + ServerStatus.OK); if (redisOkCount == null) redisOkCount = 0L;
               redisOkCount += statusMap.get(Item.TYPE_REDIS + "-" + ServerStatus.UNKNOWN) == null ? 0L : statusMap.get(Item.TYPE_REDIS + "-" + ServerStatus.UNKNOWN);
               Long redisDownCount = statusMap.get(Item.TYPE_REDIS + "-" + ServerStatus.DOWN); if (redisDownCount == null) redisDownCount = 0L;
               Long redisInstableCount = statusMap.get(Item.TYPE_REDIS + "-" + ServerStatus.INSTABLE); if (redisInstableCount == null) redisInstableCount = 0L;
               
			   Long webOkCount = httpOkCount + pingOkCount;
               Long webDownCount = httpDownCount + pingDownCount;
               Long webInstableCount = httpInstableCount + pingInstableCount;
               
               Long serviceOkCount = nginxOkCount + redisOkCount;
               Long serviceDownCount = nginxDownCount + redisDownCount;
               Long serviceInstableCount = nginxInstableCount + redisInstableCount;
               
			   %>
               <td align="center"></br><font color="0088FF" size="1.8"><span >网站监控项目：OK(<%=webOkCount%>), &nbsp;&nbsp;Down(<%=webDownCount%>), &nbsp;&nbsp;INSTABLE(<%=webInstableCount%>)</span></font></br>&nbsp;</td>
               <td align="center"></br><font color="0088FF" size="1.8"><span >常见服务监控项目：OK(<%=serviceOkCount%>), &nbsp;&nbsp;Down(<%=serviceDownCount%>), &nbsp;&nbsp;INSTABLE(<%=serviceInstableCount%>)</span></font></br>&nbsp;</td>
               </tr>
               <tr align="center" >
               <td colspan="2" style="padding-top: 20px; padding-bottom: 10px;"><font color="0088FF">正常: <%=normalRate%>%, &nbsp;&nbsp;故障 <%=downRate%>%, &nbsp;&nbsp;不稳定 <%=unstableRate%>%</font></td>
               </tr>
              </table>
          </div>  
          <div class="w_735 mt">
              <div class="title01">
              <h4 class="fl">Http监控项目平均响应时间</h4>
              </div>
              <%
	      		 String strXML2="";
                 strXML2 += "<chart caption='Http项目平均响应时间统计' yAxisName='时间(ms)' baseFont='Microsoft YaHei' maxColWidth='75' yAxisMaxValue='10' chartLeftMargin='10' chartBottomMargin='0' chartRightMargin='0' baseFontSize='12' baseFontColor='0088FF' bgColor='2F2F2F' use3DLighting='0' animation='1' canvasBgColor='2F2F2F' divLineIsDashed='1' divLineColor='404040'>";
				 				
                 String[] colorArr = {"F6BD0F", "AFD8F8", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE"};
                 int colorPos = 0;
                 Iterator it = rateMap.entrySet().iterator();
                 boolean hasHttpItem = false;
                 while (it.hasNext()) {
                	   Map.Entry entry = (Map.Entry) it.next();
                	   Object key = entry.getKey();
                	   Object[] arr = (Object[]) rateMap.get(key);
                	   Item item = (Item) arr[0];
                	   if (item != null && item.getType() == Item.TYPE_HTTP) {
                		   hasHttpItem = true;
                		   Double value = (Double) arr[1];
                    	   strXML2 += "<set label='"+CommonUtil.encodeXmlString(item.getName())+"' value='" + value +"' color='" + colorArr[colorPos] + "'/>";
                    	   
                    	   colorPos++;
                    	   if (colorPos >= colorArr.length) colorPos = 0;
                	   }
                 }
                 
                 strXML2 += "<trendlines>";
                 strXML2 += "<line startvalue='10' color='8BBA00' thickness='1' isTrendZone='0'/>";
                 strXML2 += "</trendlines>";
               
                 strXML2 += "</chart>";
                 System.out.println("strXML2:" + strXML2);
              %>
              <table class="tu" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 25px;margin-top: 30px;">
              <% if (hasHttpItem) { %>
               <tr><td>
				 <jsp:include page="/fushionchartxt/FusionChartsHTMLRenderer.jsp" flush="true"> 
                 <jsp:param name="chartSWF" value="/fushionchartxt/Column3D-3.2.swf?PBarLoadingText=loading..." /> 
                 <jsp:param name="strURL" value="" />
                 <jsp:param name="strXML" value="<%=strXML2%>" />
                 <jsp:param name="chartId" value="myFirst" />
                 <jsp:param name="chartWidth" value="740" />
                 <jsp:param name="chartHeight" value="350" />
                 <jsp:param name="debugMode" value="false" />
                 </jsp:include>
			   </td></tr>
			   
              <% } else { %>
               <tr><td>
				 没有可用的监控项目，<a id="createItem2" href="/item/new.do" style="color: #E69934;">点此处添加监控项目</a>
			   </td></tr>
			   
              <% } %>
              </table>
          </div>
          
          <div class="w_735 mt">
              <div class="title01">
              <h4 class="fl">Ping监控项目平均响应时间</h4>
              </div>
              <%
	      		 strXML2="";
                 strXML2 += "<chart caption='Ping项目平均响应时间统计' yAxisName='时间(ms)' baseFont='Microsoft YaHei' maxColWidth='75' yAxisMaxValue='10' chartLeftMargin='10' chartBottomMargin='0' chartRightMargin='0' baseFontSize='12' baseFontColor='0088FF' bgColor='2F2F2F' use3DLighting='0' animation='1' canvasBgColor='2F2F2F' divLineIsDashed='1' divLineColor='404040'>";
				 
                 colorPos = 0;
                 it = rateMap.entrySet().iterator();
                 boolean hasPingItem = false;
                 while (it.hasNext()) {
                	   Map.Entry entry = (Map.Entry) it.next();
                	   Object key = entry.getKey();
                	   Object[] arr = (Object[]) rateMap.get(key);
                	   Item item = (Item) arr[0];
                	   if (item != null && item.getType() == Item.TYPE_PING) {
                		   hasPingItem = true;
                		   Double value = (Double) arr[1];
                    	   strXML2 += "<set label='"+CommonUtil.encodeXmlString(item.getName())+"' value='" + value +"' color='" + colorArr[colorPos] + "'/>";
                    	   
                    	   colorPos++;
                    	   if (colorPos >= colorArr.length) colorPos = 0;
                	   }
                 }
                 
                 strXML2 += "<trendlines>";
                 strXML2 += "<line startvalue='10' color='8BBA00' thickness='1' isTrendZone='0'/>";
                 strXML2 += "</trendlines>";
               
                 strXML2 += "</chart>";
                 System.out.println("strXML2:" + strXML2);
              %>
              <table class="tu" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 25px;margin-top: 30px;">
              <% if (hasPingItem) { %>
               <tr><td>
				 <jsp:include page="/fushionchartxt/FusionChartsHTMLRenderer.jsp" flush="true"> 
                 <jsp:param name="chartSWF" value="/fushionchartxt/Column3D-3.2.swf?PBarLoadingText=loading..." /> 
                 <jsp:param name="strURL" value="" />
                 <jsp:param name="strXML" value="<%=strXML2%>" />
                 <jsp:param name="chartId" value="myFirst" />
                 <jsp:param name="chartWidth" value="740" />
                 <jsp:param name="chartHeight" value="350" />
                 <jsp:param name="debugMode" value="false" />
                 </jsp:include>
			   </td></tr>
			   
              <% } else { %>
               <tr><td>
				 没有可用的监控项目，<a id="createItem2" href="/item/new.do" style="color: #E69934;">点此处添加监控项目</a>
			   </td></tr>
			   
              <% } %>
              </table>
          </div>
          
          <div class="w_735 mt">
              <div class="title01">
              <h4 class="fl">最近7天故障记录</h4>
              <p class="fr pr"><span><a href="/alarm/history.do">更多</a></span></p>
              </div>
              <table class="gray" cellpadding="0" cellspacing="0" border="0">
               <tr class="tit">
                  <td>项目名称</td>
                  <td>监控类型</td>
                  <td>开始时间</td>
                  <td>恢复时间</td>
                  <td>故障时长</td>
                  <td>故障类型</td>
                  <td>是否已发送报警消息</td>
              </tr>
              <% if (CollectionUtils.isEmpty(alarms)) {%>
			  <tr >
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
              </tr>
              <% } else {
            	  for (BreakDownHistory alarm : alarms) {
						long createTime = alarm.getCreateTime();
						String date = DateUtil.friendlyTimestamp(new Date(createTime));
						
						Item item = itemMap.get(alarm.getItemId());
						String itemName = "-", itemUrl = "-", serverIp = "";
						if (item != null && item.getName() != null) {
							itemName = item.getName();
						}
						if (item != null && item.getUri() != null) {
							itemUrl = item.getUri();
						}
						if (alarm.getServerIp() != null) serverIp = alarm.getServerIp();
						
						String startTimeStr = DateUtil.friendlyTimestamp(new Date(alarm.getCreateTime())), endTimeStr = "&nbsp;", totalTimeStr = "&nbsp;";
						long endTime = alarm.getEndTime();
						if (endTime != 0L) {
							endTimeStr = DateUtil.friendlyTimestamp(new Date(endTime));
							totalTimeStr = ApplicaRuntime.timeDiff(startTimeStr, endTimeStr, "yyyy-MM-dd HH:mm:ss");
						}
						String errorTypeStr = "&nbsp;", itemType = "&nbsp;", citemName = "&nbsp;";
						if (item != null) {
							if (item.getType() == Item.TYPE_HTTP) {
								errorTypeStr = new HttpResult().getContentByErrorNo(alarm.getErrorType());
							} else if (item.getType() == Item.TYPE_PING) {
								errorTypeStr = new PingResult().getContentByErrorNo(alarm.getErrorType());
							} else if (item.getType() == Item.TYPE_NGINX) {
								errorTypeStr = new NginxResult().getContentByErrorNo(alarm.getErrorType());
							} else if (item.getType() == Item.TYPE_REDIS) {
								errorTypeStr = new RedisResult().getContentByErrorNo(alarm.getErrorType());
							}
							itemType = Item.getTypeName(item.getType());
							citemName = item.getName();
						}
              			boolean isSendAlarm = alarm.isSendAlarm();
             %>
             <tr>
                  <td>
                  <a class="none" href="<%=ApplicaRuntime.getItemFpByItemId(item)%>" target="_fp"><%=citemName%><%
					if (StringUtils.isNotEmpty(serverIp)) {
						out.print("[" + serverIp + "]");
					}
					%></a>
				  </td>
				  <td><%=itemType%></td>
                  <td><%=startTimeStr%></td>
                  <td><%=endTimeStr %></td>
                  <td><%=totalTimeStr %></td>
                  <td><%=errorTypeStr %></td>
                  <td><%=(isSendAlarm ? "已发送" : "未发送") %></td>
              </tr>
              
              	<% } 
              }%>
 
              </table>
          </div>
          
          
          
          <div class="w_735 mt">
              <div class="title01">
              <h4 class="fl">尚未恢复项目</h4>
              <p class="fr pr"><span><a href="/alarm/notresume.do">更多</a></span></p>
              </div>
              <table class="gray" cellpadding="0" cellspacing="0" border="0">
              <tr class="tit">
                  <td>项目名称</td>
                  <td>监控类型</td>
                  <td>开始时间</td>
                  <td>持续时间</td>
                  <td>故障类型</td>
                  <td>是否已发送报警消息</td>
              </tr>
              <% if (CollectionUtils.isEmpty(notResumeList)) {%>
			  <tr >
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
              </tr>
              <% } else {
            	  for (BreakDownHistory alarm : notResumeList) {
						long createTime = alarm.getCreateTime();
						String date = DateUtil.friendlyTimestamp(new Date(createTime));
						
						Item item = notResumeItemMap.get(alarm.getItemId());
						if (item == null) continue;
						
						String itemName = "-", itemUrl = "-", serverIp = "";
						if (item != null && item.getName() != null) {
							itemName = item.getName();
						}
						if (item != null && item.getUri() != null) {
							itemUrl = item.getUri();
						}
						if (alarm.getServerIp() != null) serverIp = alarm.getServerIp();
						
						String startTimeStr = DateUtil.friendlyTimestamp(new Date(alarm.getCreateTime())), endTimeStr = "&nbsp;", totalTimeStr = "&nbsp;";
						long endTime = System.currentTimeMillis();
						String duration = ApplicaRuntime.fuzzyTimeDiff(alarm.getCreateTime(), endTime);
						if (item.getStatus() == Item.STATUS_FREEZED) {
							duration = "已暂停";
						}
						String errorTypeStr = "&nbsp;", itemType = "&nbsp;";
						if (item != null) {
							if (item.getType() == Item.TYPE_HTTP) {
								errorTypeStr = new HttpResult().getContentByErrorNo(alarm.getErrorType());
							} else if (item.getType() == Item.TYPE_PING) {
								errorTypeStr = new PingResult().getContentByErrorNo(alarm.getErrorType());
							} else if (item.getType() == Item.TYPE_NGINX) {
								errorTypeStr = new NginxResult().getContentByErrorNo(alarm.getErrorType());
							} else if (item.getType() == Item.TYPE_REDIS) {
								errorTypeStr = new RedisResult().getContentByErrorNo(alarm.getErrorType());
							}
							itemType = Item.getTypeName(item.getType());
						}
						
            			boolean isSendAlarm = alarm.isSendAlarm();
              %>
              <tr >
                  <td>
					<a class="none" href="<%=ApplicaRuntime.getItemFpByItemId(item)%>" target="_fp"><%=item.getName()%><%
					if (StringUtils.isNotEmpty(serverIp)) {
						out.print("[" + serverIp + "]");
					}
					%></a>
				  </td>
				  <td><%=itemType%></td>
                  <td><%=startTimeStr%></td>
                  <td><%=duration %></td>
                  <td><%=errorTypeStr %></td>
                  <td><%=(isSendAlarm ? "已发送" : "未发送") %></td>
              </tr>
              	<% }
              
              }%>
              </table>
          </div>
          </div>
      </div>
    </div>
</div>
<p><br/></p>

<div id="panels5" class="tanchu" style="display:none;">
	<ul class="lv_tab">
		<li id="web"><a class="on" href="#">网站监控</a></li>
		<li id="service"><a href="#">服务性监控</a></li>
	</ul>
	
	<div class="lvtab_con">
	    <div class="tab_con01">
	    	<a class="close" href="#">x</a>
	    	<p>请选择您创建的类别：</p>
			<ul>
		        <li><a href="/item/new.do">Http</a></li>
		        <li><a href="/item/newping.do">Ping</a></li> 
		    </ul>
	    </div>

		<div class="tab_con02" style="display:none;">
			<a class="close" href="#">x</a>
			<p>请选择您创建的类别：</p>
			<ul>
	       	 	<li><a href="/nginxitem/new.do">Nginx</a></li>
	       	 	<li><a href="/redisitem/new.do">Redis</a></li>
	    	</ul>
	    </div>
	</div>
</div>


<script type="text/javascript">
var layer5 = new popLayer($("#panels5"), 1);//创建项目的弹出层
//创建监控项目
$("#createItem,#createItem2").click(function(e) {
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