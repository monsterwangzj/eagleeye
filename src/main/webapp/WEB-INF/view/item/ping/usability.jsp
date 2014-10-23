<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%><%@ page import="com.chengyi.eagleeye.model.assist.HttpOption,net.sf.json.JSONObject,com.chengyi.eagleeye.util.CommonUtil,java.text.NumberFormat,java.util.*,com.chengyi.eagleeye.model.*,org.apache.commons.collections.CollectionUtils" 
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

String last = (String) request.getAttribute("last");
if (last == null) last = "";
String figure1Title = "总体可用率", figure2Title = "";
if (last.equals("") || last.equals("0")) {
	figure1Title = "今日" + figure1Title;
	figure2Title = "今日每小时可用率";
} else if (last.equals("1")) {
	figure1Title = "昨日" + figure1Title;
	figure2Title = "昨日每小时可用率";
}else if (last.equals("7")) {
	figure1Title = "最近7天" + figure1Title;
	figure2Title = "最近7天可用率";
}else if (last.equals("15")) {
	figure1Title = "最近15天" + figure1Title;
	figure2Title = "最近15天可用率";
}else if (last.equals("30")) {
	figure1Title = "最近30天" + figure1Title;
	figure2Title = "最近30天可用率";
}
figure1Title = item.getName() + " " + figure1Title;
// JSONObject options = JSONObject.fromObject(item.getOptions());

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="/graphics/style.css" type="text/css" />
<script type="text/javascript" src="/js/jquery-1.11.0.min.js"></script>

<script src="/js/highstock.js"></script>

<title>Ping项目监控-可用性统计</title>
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
String dataurl = "/pingitem/detailusability2.do?itemId=" + item.getId();
if (StringUtils.isNotEmpty(last) && !last.equals("0")) {
	dataurl += "&last=" + last;
}

%>
<script type="text/javascript">
Highcharts.setOptions({ 
    global: { useUTC: false  } 
});

$(function() {
	$.getJSON('<%=dataurl%>', function(data) {
		if (data.data.totalUsability == 0 && data.data.totalUnUsablity == 0) {
			 new Highcharts.Chart({
				chart: {
		            plotBackgroundColor: null,
		            plotBorderWidth: null,
		            plotShadow: false,
		            backgroundColor: '#ececec',
		            borderRadius: 2,
		            
		            renderTo: 'container',
		            events: {
		                load: function () {
		                    var ren = this.renderer,
		                    colors = Highcharts.getOptions().colors;
		                    
		                    ren.label('　该时间范围内暂无可用率统计!　', 240, 130).attr({r: 5,width: 190,fill: colors[2]}).css({color: 'white',fontWeight: 'bold'}).add();
		                }
		            }
		        }, title: {
		            text: '<%=figure1Title%>',
		            style: {
		            	fontFamily: '宋体',
		            	fontSize: '13px',
		            	font: 'bold 13px "Trebuchet MS", Verdana, sans-serif',
		            	color: 'black'
		            }
		        }, exporting: {
		        	enabled: false
		        }, credits: {
		        	enabled: false
		        }
			});
			 
			new Highcharts.Chart({
				chart: {
	                type: 'spline',
	                borderRadius: 0.5,
		            
		            renderTo: 'container2',
		            events: {
		                load: function () {
		                    var ren = this.renderer,
		                    colors = Highcharts.getOptions().colors;
		                    
		                    ren.label('　该时间范围内暂无可用率统计!　', 240, 140).attr({r: 5,width: 190,fill: colors[2]}).css({color: 'white',fontWeight: 'bold'}).add();
		                }
		            }
				}, title: {
	            	text : '<%=figure2Title%>',
	           		style: {
	   	            	font: 'bold 13px "Trebuchet MS", Verdana, sans-serif',
	   	            	color: '#0088FF'
	   	            },
	   	            x: +10 //center
	            }, exporting: {
		        	enabled: false
		        }, credits: {
		        	enabled: false
				}
			});

		} else {
			// Build the PIE chart
		    $('#container').highcharts({
		        chart: {
		            plotBackgroundColor: null,
		            plotBorderWidth: null,
		            plotShadow: false,
		            backgroundColor: '#ececec',
		            borderRadius: 2
		        },
		        title: {
		            text: '<%=figure1Title%>',
		            style: {
		            	fontFamily: '宋体',
		            	fontSize: '13px',
		            	font: 'bold 13px "Trebuchet MS", Verdana, sans-serif',
		            	color: 'black'
		            }
		        },
		        tooltip: {
		    	    pointFormat: '<b>{point.percentage:.2f}%</b>'
		        },
		        plotOptions: {
		            pie: {
		                allowPointSelect: true,
		                cursor: 'pointer',
		                dataLabels: {
		                    enabled: true,
		                    color: '#000000',
		                    connectorColor: '#000000',
		                    formatter: function() {
		                        return '<b>'+ this.point.name +'</b>: '+ this.point.y +' %';
		                    }
		                },
		        		startAngle: 90,
		        		size: 210,
		        		shadow: true,
		        		showInLegend: true,
		        		colors: ['#BF0000','#008900']
		            }
		        },
		        series: [{
		            type: 'pie',
		            name: '可用率',
		            data: [
		                ['不可用', data.data.totalUnUsablity ],
		                ['可用', data.data.totalUsability ]
		            ]
		        }],
		        legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            borderWidth: 0
		        },
		        exporting: {
		        	enabled: false
		        },
		        credits: {
		        	 enabled: false
		        }
		    });
			
			
		 	// http://www.highcharts.com/demo/spline-irregular-time
	        $('#container2').highcharts({
	            chart: {
	                type: 'spline',
	                backgroundColor: '#ececec',
	                borderRadius: 0.5
	            },
	            title: {
	            	text : '<%=figure2Title%>',
	           		style: {
	   	            	font: 'bold 13px "Trebuchet MS", Verdana, sans-serif',
	   	            	color: '#0088FF'
	   	            },
	   	            x: +10 //center
	            },
	            xAxis: {
	                type: 'datetime',
	                dateTimeLabelFormats: {
	                	day: '%m/%e',
	                    month: '%e.%b',
	                    year: '%b'
	                },
	                tickPixelInterval:80
	            },
	            yAxis: {
					showFirstLabel: true,
					showLastLabel: true,
					min: 0, 
					max: 125,
					labels: {
						aligh: 'right',
						x:-5,
						y:3,
						formatter: function() {
	                        return this.value +'%';
	                    }
					},
					title: {
	                    text: '可用率 (%)'
	                    //enabled: false
	                },
					linewidth: 2,
					offset: 10
	            },
	            tooltip: {
	                formatter: function() {
	                	return Highcharts.dateFormat('%Y-%m-%d %H:00', this.x) +' '+ this.y + "%";
	                }
	            },
	            plotOptions: {
	            	series: {
	                    marker: {
	                        radius: 3,  //曲线点半径，默认是4
	                        symbol: 'circle' //曲线点类型："circle", "square", "diamond", "triangle","triangle-down"，默认是"circle"
	                    }
	                }
		        },
	            series: [{
	                data: data.data.data,
	                showInLegend: false,
	                color: '#2AD62A'
	            }],
	            exporting: {
	            	enabled: false
	            },
	            credits: {
	            	 enabled: false
	            }
	        });
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
                  <li class="current"><span class="point">&nbsp;</span><a href="#">可用性统计</a></li> 
                  <li><span class="point">&nbsp;</span><a href="/pingitem/responsetime2.do?itemId=<%=item.getId()%>">响应时间统计</a></li>
                  <li class="noline"><span class="point">&nbsp;</span><a href="/pingitem/dailystat2.do?itemId=<%=item.getId()%>">每日统计</a></li>
              </ul>
             </div>
             
          </div>
          <div class="w_right">
          <div class="w_735">
              <div class="title01">
	              <h4 class="fl">可用性统计</h4>
	              <p class="fr pr">
	              	<span><a <%=( last.equals("0") ? "class=\"on\"" : "")%> href="/pingitem/usability2.do?itemId=<%=item.getId()%>">今日</a></span>
	              	<span><a <%=( last.equals("1") ? "class=\"on\"" : "")%> href="/pingitem/usability2.do?itemId=<%=item.getId()%>&last=1">昨日</a></span>
	              	<span><a <%=( last.equals("7") ? "class=\"on\"" : "")%> href="/pingitem/usability2.do?itemId=<%=item.getId()%>&last=7">最近7天</a></span>
	              	<span><a <%=( last.equals("15") ? "class=\"on\"" : "")%> href="/pingitem/usability2.do?itemId=<%=item.getId()%>&last=15">最近15天</a></span>
	              	<span><a <%=( last.equals("30") ? "class=\"on\"" : "")%> href="/pingitem/usability2.do?itemId=<%=item.getId()%>&last=30">最近30天</a></span>
	              	<span class="date_ch"><a <%=( last.equals("-1") ? "class=\"on\"" : "")%> href="javascript:void(0)"></a></span>
	              	</p>
              </div>
              <table class="tu" cellpadding="0" cellspacing="20" border="0" style="width: 730px; margin-top: 0px; margin-bottom: 0px;">
			    <tr>
			     <td valign="top" class="text" align="center" >
			     <div id="container" style="width: 700px; height: 280px; margin: 0 auto; "></div>
			     </td>
			    </tr>

			   <tr>
			     <td valign="top" class="text" align="center" >
			     	<div id="container2" style="width: 700px; height: 335px; "></div>
			     </td>
			   </tr>
			    
              </table>
          </div>    
          </div>
      </div>
    </div>
</div>
<p><br/></p>

<script type="text/javascript" src="/js/calendar.js"></script>
<script type="text/javascript">
/*
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
		location.href = base + "/item/usability.do?itemId=" + itemId + "&last=-1&start=" + arr[0] + "&end=" + arr[1];
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
*/
</script>
</body>
</html>