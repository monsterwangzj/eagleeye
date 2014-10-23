<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="com.chengyi.eagleeye.model.assist.HttpOption,java.util.Map,com.chengyi.eagleeye.util.CommonUtil,com.chengyi.eagleeye.util.ApplicaRuntime,net.sf.json.JSONObject,java.text.NumberFormat,java.util.*,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
%><%
Item item = (Item) request.getAttribute("item");
String status = "";
if (item.getStatus() == (byte) 1) {
	status = "开启";
} else if (item.getStatus() == (byte) 0) {
	status = "冻结";
} else if (item.getStatus() == (byte) -1) {
	status = "其它";
}

String monitorFreq = "";
if (item.getMonitorFreq() < 60) {
	monitorFreq = item.getMonitorFreq() + " 秒钟";
} else {
	double freq = item.getMonitorFreq() * 1. / 60;
	NumberFormat nf = NumberFormat.getInstance();
	monitorFreq = nf.format(freq) + " 分钟";
}

ArrayList<String> keyList = (ArrayList<String>) request.getAttribute("keyList");
ArrayList<JSONObject> jsonList = (ArrayList<JSONObject>) request.getAttribute("jsonList");
int[] classCountArr = (int[]) request.getAttribute("classCountArr");
System.out.println("keyList:" + keyList);
System.out.println("jsonList:" + jsonList);
System.out.println("classCountArr:" + classCountArr);

int classCountTotal = 0;
for (int k = 0;k<classCountArr.length;k++) {
	System.out.println("count:" + classCountArr[k]);
	classCountTotal += classCountArr[k];
}
if (classCountTotal == 0) classCountTotal = 1;

String last = (String) request.getAttribute("last");
if (last == null) last = "";

Boolean isHourUnit = (Boolean) request.getAttribute("isHourUnit");

Double totalMinTime = (Double) request.getAttribute("totalMinTime");
Double totalMaxTime = (Double) request.getAttribute("totalMaxTime");
Double totalAvgTime = (Double) request.getAttribute("totalAvgTime");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="/graphics/style.css" type="text/css" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>
<script src="/js/highcharts.js"></script>
<script src="/js/modules/exporting.js"></script>
<script language="JavaScript" src="/graphics/FusionCharts.js"></script>
<title>监控系统 创建项目分类</title>
<style>
#ccontainer td{
	color:#000;
	background:#fff;
}
#ccontainer th{
	color:#000;
}
</style>
<%
StringBuilder keys = new StringBuilder();
StringBuilder key2s = new StringBuilder();
StringBuilder data1s = new StringBuilder();
StringBuilder data2s = new StringBuilder();
StringBuilder data3s = new StringBuilder();
if (keyList != null && keyList.size() > 0) {
	 Iterator<String> keyIt = keyList.iterator();
	 while (keyIt.hasNext()) {
		 String label = keyIt.next();
		 if (isHourUnit) {
			 key2s.append("'").append(ApplicaRuntime.formatStatTimeByHour(label+"00")).append("',");
		 } else {
			 key2s.append("'").append(ApplicaRuntime.formatStatTimeByDay(label+"00")).append("',");
		 }
		 
		 if (label.endsWith("00")) {
			 label = label.substring(0, 4) + "/" + label.substring(4, 6) + "/" + label.substring(6, 8);
		 } else {
			 label = label.substring(8, 10) + ":00";
		 }
	     keys.append("'").append(label).append("',");
	 }

	 for (int i = 0;i<jsonList.size();i++) {
		 JSONObject jobj = jsonList.get(i);
		 if (jobj != null) {
			 data1s.append(jobj.getDouble("min")).append(",");
		 } else {
			 data1s.append("null").append(",");
		 }
		 
	 }
        
     for (int i = 0;i<jsonList.size();i++) {
		 JSONObject jobj = jsonList.get(i);
		 if (jobj != null) {
			 data2s.append(jobj.getDouble("avg")).append(",");
		 } else {
			 data2s.append("null").append(",");
		 }
		 
	 }

    for (int i = 0;i<jsonList.size();i++) {
		 JSONObject jobj = jsonList.get(i);
		 if (jobj != null) {
			 data3s.append(jobj.getDouble("max")).append(",");
		 } else {
			 data3s.append("null").append(",");
		 }
		
	}
    
	keys = keys.deleteCharAt(keys.toString().length() - 1);
	data1s = data1s.deleteCharAt(data1s.toString().length() - 1);
	data2s = data2s.deleteCharAt(data2s.toString().length() - 1);
	data3s = data3s.deleteCharAt(data3s.toString().length() - 1);
	
	System.out.println("keys:" + keys);
	System.out.println("key2s:" + key2s);
	System.out.println("data1s:" + data1s);
	System.out.println("data2s:" + data2s);
	System.out.println("data3s:" + data3s);
}
%>
<script type="text/javascript">
$(function () {
		var xnames = [<%=keys%>];
		var xnamesTooltip = [<%=key2s%>];
        $('#container2').highcharts({
        	chart: {
        		borderRadius: 0,
        		backgroundColor: '#f4f4f4'
        	},
            title: {
                text: '响应时间统计(ResponseTime Stat)',
                style: {
                	font: 'bold 13px "Trebuchet MS", Verdana, sans-serif',
                	color: '#0088FF'
                },
                x: +10
            },
            xAxis: {
            	type: 'datetime',
            	title: {
                    text: '时间'
                },
                labels: {
                    //rotation: -90,
                    //align: 'top',
                    style: {
                        color: '#0088FF',
                        font: '12px "黑体","宋体"'
                    },
                    formatter: function () {
                        return xnames[this.value];
                    }
                }
            },
            yAxis: {
                title: {
                    text: '响应时间(ms)'
                },
                min:0,
                plotLines: [{
                    value: 0,
                    width: 1
                }]
            },
            tooltip: {
            	formatter: function() {  
            		return '<b>'+ this.series.name +'</b><br/>'+xnamesTooltip[this.x]+' '+ this.y + "ms";  
            	}
            },
            series: [
                {
	                name: '最小响应时间',
	                data: [<%=data1s%>],
	                color: '#1D8BD1',
		            marker: {
	           			radius:4
	            	}
                },
            	{
                    name: '平均响应时间',
                    data: [<%=data2s%>],
                    color: '#F1683C',
    	            marker: {
               			radius:4
    	            }
                },
                {
                    name: '最大响应时间',
                    data: [<%=data3s%>],
                    color: '#2AD62A',
    	            marker: {
               			radius:4
    	            }
                }
            ],
            legend: {
            	enabled: true
            },
            exporting: {
            	enabled: false
            },
            credits: {
            	 enabled: false
            }
        });
});
</script>
					
</head>
<body>
<div class="bg01">
    <div class="con">
      <%@ include file="/WEB-INF/view/common/header.jsp"%>
      <div class="w_b mt">
      <table class="tongji" width="930" border="1">
  <tr>
    <td width="65" height="33">项目名称：</td>
    <td width="423" class="fb"><%=item.getName() %></td>
    <td width="69">服务状态：</td>
    <td width="341"><%=status%>项目</td>
  </tr>
  <tr>
    <td class="noline">URL：</td>
    <td class="noline"><%=item.getUri() %></td>
    <td class="noline">监控频率：</td>
    <td class="noline"><%=monitorFreq %></td>
  </tr>
  <tr>
  	<%
  	//JSONObject optionJSON = JSONObject.fromObject(item.getOptions());
  			
  	%>
    <td class="noline">监控类型：</td>
    <td class="noline">PING</td>
  </tr>
</table>
      
      </div>
      <div class="w_960 pt">
           <div class="w_left">
              <div class="w_210 bc">
              <div class="title"><h4>监控信息</h4></div>
              <ul class="list">
                  <li><span class="point">&nbsp;</span><a href="/pingitem/usability2.do?itemId=<%=item.getId()%>">可用性统计</a></li> 
                  <li class="current"><span class="point">&nbsp;</span><a href="#">响应时间统计</a></li>
                  <li class="noline"><span class="point">&nbsp;</span><a href="/pingitem/dailystat2.do?itemId=<%=item.getId()%>">每日统计</a></li>
              </ul>
             </div>
             
          </div>
          <div class="w_right">
          <div class="w_735">
              <div class="title01">
              <h4 class="fl">响应时间统计</h4>
              <p class="fr pr">
              		<span><a <%=( last.equals("0") ? "class=\"on\"" : "")%> href="/pingitem/responsetime2.do?itemId=<%=item.getId()%>">今日</a></span>
              		<span><a <%=( last.equals("1") ? "class=\"on\"" : "")%> href="/pingitem/responsetime2.do?itemId=<%=item.getId()%>&last=1">昨日</a></span>
              		<span><a <%=( last.equals("7") ? "class=\"on\"" : "")%> href="/pingitem/responsetime2.do?itemId=<%=item.getId()%>&last=7">最近7天</a></span>
              		<span><a <%=( last.equals("15") ? "class=\"on\"" : "")%> href="/pingitem/responsetime2.do?itemId=<%=item.getId()%>&last=15">最近15天</a></span>
              		<span><a <%=( last.equals("30") ? "class=\"on\"" : "")%> href="/pingitem/responsetime2.do?itemId=<%=item.getId()%>&last=30">最近30天</a></span>
              		<span class="date_ch"><a href="javascript:void(0)" <%=( last.equals("-1") ? "class=\"on\"" : "")%> href="javascript:void(0)"></a></span>
              </p>
              </div>
      <table class="tu" cellpadding="0" cellspacing="0" border="0" style="width: 730px; margin-top: 10px; margin-bottom: 20px;">
      		<tr><td align="center">
      		<% if (keyList != null && keyList.size() > 0) { %>
	      		<table><tr align="center">
	      		<td class="time02" height="50" style="">最小响应时间<br/><span><%=totalMinTime%>ms</span>&nbsp;&nbsp;</td>
	      		<td class="time03" style="">平均响应时间<br/><span><%=totalAvgTime%>ms</span>&nbsp;&nbsp;</td>
	      		<td class="time01" style="">最大响应时间<br/><span><%=totalMaxTime%>ms</span>&nbsp;&nbsp;</td>
	      		</tr></table>
      		<% } %>
      		</td></tr>
      		<tr>
				<td align="center">
					<div id="container2" style="width: 700px; height: 400px; "></div>
				</td>
			</tr>
      </table>
      
              <table class="tu" cellpadding="0" cellspacing="0" border="0" style="margin-top: 20px;">
               <tr><td>
              <table class="gray fl w_320" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td class="blank" colspan="2">各时段平均响应时间</td>
                </tr>
              <tr class="tit">
                  <td>日期</td>
                  <td>平均响应时间</td>
              </tr>
              <%
              for (int i = 0;i<keyList.size();i++) {
				  String key = keyList.get(i); 
            	  JSONObject jobj = jsonList.get(i);
            	  String label = key.substring(0, 4) + "/" + key.substring(4, 6) + "/" + key.substring(6, 8) + " " + key.substring(8) + ":00";
            	  
            	  String avgTimeStr = "-";
            	  if (jobj != null) {
            		  avgTimeStr = jobj.getDouble("avg") + "ms";
            	  }
				  
				  %>
				  <tr>
	                  <td><%=label%></td>
	                  <td><%=avgTimeStr%></td>
	              </tr>
				  <%
				 }
			  %>
              
              </table>
              <table class="gray fr w_320" cellpadding="0" cellspacing="0" border="0">
              <tr>
                  <td class="blank" colspan="3">平均响应时间分布</td>
              </tr>
              <tr class="tit">
                  <td>响应时间范围</td>
                  <td>次数</td>
                  <td>百分比</td>
              </tr>
              <tr>
                  <td>0-1ms</td>
                  <td><%=classCountArr[0] %></td>
                  <td><%=CommonUtil.get2pDouble(classCountArr[0] * 100. / classCountTotal)%>%</td>
              </tr>
              <tr>
                  <td>1-10ms</td>
                  <td><%=classCountArr[1] %></td>
                  <td><%=CommonUtil.get2pDouble(classCountArr[1] * 100. / classCountTotal)%>%</td>
              </tr>
              <tr>
                  <td>10-100ms</td>
                  <td><%=classCountArr[2] %></td>
                  <td><%=CommonUtil.get2pDouble(classCountArr[2] * 100. / classCountTotal)%>%</td>
              </tr>
              <tr>
                  <td>100-1000ms</td>
                  <td><%=classCountArr[3] %></td>
                  <td><%=CommonUtil.get2pDouble(classCountArr[3] * 100. / classCountTotal)%>%</td>
              </tr>
              <tr>
                  <td>&gt;1000ms</td>
                  <td><%=classCountArr[4] %></td>
                  <td><%=CommonUtil.get2pDouble(classCountArr[4] * 100. / classCountTotal)%>%</td>
              </tr>
            </table>
            </td></tr>  
              </table>
          </div>    
          </div>
      </div>
    </div>
</div>
<p><br/></p>
<script type="text/javascript" src="/js/calendar.js"></script>
<script type="text/javascript">
var base = "http://" + location.host,//Ajax请求的基地址
	car = new Calendar("body"),
	o = $("span.date_ch").offset(),
	itemId = query("itemId"),
	data = {};
o.top += 33;
o.left -= 271;
car.setPosition(o);
car.on("choose", function(e){
	var arr = [];
	if(e.status == "1") {
		data[e.data] = 1;
	}
	if(e.status == "0") {
		delete data[e.data];
	}
	for(var s in data) {
		arr.push(s);
	}
	arr.sort();
	if(arr.length == 2) {
		location.href = base + "/item/responsetime.do?itemId=" + itemId + "&last=-1&start=" + arr[0] + "&end=" + arr[1];
	}
});
var isShow = true;
$("span.date_ch").click(function(e) {
	e.stopPropagation();
	if(isShow == true) {
		car.show();
		isShow = false;
	} else {
		car.hide();
		isShow = true;
	}
	
});
$("body").click(function() {
	isShow = true;
	car.hide();
});
</script>
</body>
</html>