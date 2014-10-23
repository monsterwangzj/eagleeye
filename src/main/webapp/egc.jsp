<%@ page contentType="text/html;charset=GBK"%>
<%@ page import="java.util.Date,java.text.SimpleDateFormat"%>
<h1>Stat系统内存状态监控<%=com.ku6.eagleeye.util.NetUtil.getLocalIp()%></h1>
<hr>
<br>
<% Long startupTime = (Long) application.getAttribute("startupTime");
   if (startupTime == null) {
     startupTime = new Long(System.currentTimeMillis());
	 application.setAttribute("startupTime", startupTime);
   }
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date(startupTime.longValue());
    String startupDate = format.format(date);
    long runningTime = System.currentTimeMillis() - startupTime.longValue();
    long day = runningTime / (24 * 60 * 60 * 1000);
    long hour = runningTime % (24 * 60 * 60 * 1000) / (60 * 60 * 1000);
    long minute = runningTime % (24 * 60 * 60 * 1000) % (60 * 60 * 1000) / (60 * 1000);
    String rTime = day + " 天 " + hour + " 小时 " + minute + " 分钟";


%>
系统启动时间：<%=startupDate%><br>
系统运行时间：<%=rTime%><br><br>

JVM最大可用内存: <%=Runtime.getRuntime().maxMemory()/1024/1024%>MB<br>
JVM当前占用内存: <%=Runtime.getRuntime().totalMemory()/1024/1024%>MB <br>
应用程序占用内存: <%=Runtime.getRuntime().totalMemory()/1024/1024 - Runtime.getRuntime().freeMemory()/1024/1024 %>MB <br>
JVM剩余内存: <%=Runtime.getRuntime().freeMemory()/1024/1024%>MB<br>
<%
StringBuffer sb=new StringBuffer(); 
int count =Thread.activeCount();
Thread threads[]=new Thread[count]; 
Thread.enumerate(threads); sb.append("<br>------- Threads " + count + "  -------<br>"); 
for(int i=0; i < threads.length; i++) {
    sb.append("<br>#").append(i).append(":<br> "); 
    StackTraceElement e[]=threads[i].getStackTrace(); 
    for (int j=0; j <e.length; j++) {
        sb.append(e[j]);  
        sb.append("<br>"); 
    } 
    sb.append("\n"); }
 sb.append("------- Threads -------<br>"); out.println(sb.toString()); %> 


