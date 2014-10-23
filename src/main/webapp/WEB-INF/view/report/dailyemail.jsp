<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ 
page import="java.util.List,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils,org.apache.commons.lang.StringUtils,java.util.HashMap,java.util.Map,com.chengyi.eagleeye.util.*,com.chengyi.eagleeye.patrol.*" 
%><%
List<Item> items = (List<Item>) request.getAttribute("items");
List<MessageStat> mss = (List<MessageStat>) request.getAttribute("mss");

String servername = request.getServerName();
int port = request.getServerPort();

String title = AutoReportProcessor.getEmailTitle();

System.out.println("servername:" + servername + ", port:" + port + ", title:" + title);
%><style>a:link,a:visited{color:#a5d8ff; text-decoration:none;outline:none; }
a:hover{color:#a5d8ff; text-decoration: underline;}
a:active {star:expression(this.onFocus=this.blur());} </style><h5>Hi,天会: <p/>
&nbsp;&nbsp;以下是监控系统自动发送的监控日报，你可以通过它快速了解昨日系统的性能和可用率概况。也可以<a href="http://monitor.ku6.com/" target="_blank">登录系统</a>来了解更多的信息。</h5><table cellpadding="0" cellspacing="0" border="0" style="width: 1050px; background-color: #CCDDEE;">
<tr class="tit"><td width="20%" ><h5><%=title%></h5></td></tr>
</table><table cellpadding="0" cellspacing="0" border="1" style="width: 1048px;">
			  <tr>
                  <td width="17%">项目名称</td>
                  <td width="38%">URL</td>
                  <td width="8%">监控频率</td>
                  <td width="9%">可用率</td>
                  <td width="10%">平均响应时间</td>
                  <td width="9%">故障次数</td>
                  <td width="9%">故障总时长</td>
              </tr>
              <% if (CollectionUtils.isEmpty(items)) { %>
			  <tr>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
                  <td>-</td>
              </tr>
			  <% } else {
				  	for (int i =0;i<items.size();i++) {
				  		Item item = items.get(i);
				  		MessageStat message = mss.get(i);
				  		
				  		String aratio = "-";
				  		double availableRatio = 0.;
				  		if (message.getTotalAccessCount() > 0) {
					  		availableRatio = (message.getSuccAccessCount() * 100.) / message.getTotalAccessCount(); 
					  		availableRatio = CommonUtil.get2pDouble(availableRatio);
					  		aratio = availableRatio + "%";
				  		}
				  		
				  		String aResponse = "-";
				  		if (message.getSuccAccessCount() > 0) {
							double avgResponse = (message.getSuccAccessCostTime() * 1. ) / message.getSuccAccessCount(); 
							avgResponse = CommonUtil.get2pDouble(avgResponse);
							aResponse = avgResponse + "ms";
						}
						
						String realColor = "#CCFF00";
						char color = '2';
						int percentage = 100;
						if (availableRatio == 100) {
							color = '2';
							realColor = "#CCFF00";
							percentage = 100;
						} else if (availableRatio >= 99.99) {
							color = '3';
							realColor = "#CCFF00";
							percentage = 98;
						} else if (availableRatio >= 99.9) {
							color = '4';
							realColor = "#CCFF00";
							percentage = 95;
						} else if (availableRatio >= 99.) {
							color = '5';
							realColor = "#CCFF00";
							percentage = 90;
						} else if (availableRatio >= 90.) {
							color = '6';
							realColor = "#CCFF00";
							percentage = 80;
						} else if (availableRatio >= 80.) {
							color = '7';
							realColor = "#CCFF00";
							percentage = 70;
						} else if (availableRatio >= 70.) {
							color = '8';
							realColor = "#CCFF00";
							percentage = 60;
						} else if (availableRatio >= 60.) {
							color = '9';
							realColor = "#CCFF00";
							percentage = 50;
						} else if (availableRatio >= 50.) {
							color = 'a';
							realColor = "#CCFF00";
							percentage = 40;
						} else if (availableRatio >= 40.) {
							color = 'b';
							realColor = "#CCFF00";
							percentage = 30;
						} else if (availableRatio >= 30.) {
							color = 'c';
							realColor = "#CCFF00";
							percentage = 20;
						} else if (availableRatio >= 20.) {
							color = 'd';
							realColor = "#CCFF00";
							percentage = 15;
						} else if (availableRatio >= 10.) {
							color = 'e';
							realColor = "#CCFF00";
							percentage = 10;
						} else if (availableRatio >= 0.) {
							color = 'f';
							realColor = "#CCFF00";
							percentage = 5;
						}
						String bgcolor = "#" + color + "FFFFF";
						bgcolor = "#F1FAFA";
						
						//percentage = (int) (availableRatio * 2 - 100);
						//if (percentage < 0) percentage = 0;
				  	%>
				  	<tr data="<%=item.getId()%>" style="background-color: #393632; color: #FFFFFF; font-size: 13px; a-link">
	                  <td ><%=item.getName()%></td>
	                  <td style="color:white; "><%=item.getUri()%></td>
	                  <td><%=item.getMonitorFreq()%>s</td>
	                  <td height="26px"><%=(aratio)%><br/>
	                  	<%if (!aratio.equals("-")) {%>
	                  		<table width="100%" border="0px" cellspacing="0" cellpadding="0" height="6px">
		                  		<tr height="4">
		                  		<%if (percentage == 100) {%>
		                  			<td width="<%=percentage%>%" colspan="2" bgcolor="#CCFF00"></td>
		                  		<%} else {%>
			                  		<td width="<%=percentage%>%" bgcolor="#CCFF00"></td>
			                  		<td width="<%=(100-percentage)%>%" bgcolor="#CC0000"></td>
		                  		<%} %>
		                  		</tr>
		                  	</table>
	                  	<%} %>
	                  	</td>
	                  <td><%=(aResponse)%></td>
	                  <td>0</td>
	                  <td>?</td>
	                </tr>
				  	<% }
			  } %>
              </table><h5>
Email 通知次数:1
短信通知次数:2
由你通知次数:3

是否需要退订每日报告邮件？ 请访问 http://monitor.ku6.com/forgotme.do
祝您使用愉快！</h5>