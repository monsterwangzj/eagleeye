<%@ page contentType="text/html;charset=GBK"%>
<%@ page import="java.util.Date,java.text.SimpleDateFormat"%>
<h1>Statϵͳ�ڴ�״̬���<%=com.ku6.eagleeye.util.NetUtil.getLocalIp()%></h1>
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
    String rTime = day + " �� " + hour + " Сʱ " + minute + " ����";


%>
ϵͳ����ʱ�䣺<%=startupDate%><br>
ϵͳ����ʱ�䣺<%=rTime%><br><br>

JVM�������ڴ�: <%=Runtime.getRuntime().maxMemory()/1024/1024%>MB<br>
JVM��ǰռ���ڴ�: <%=Runtime.getRuntime().totalMemory()/1024/1024%>MB <br>
Ӧ�ó���ռ���ڴ�: <%=Runtime.getRuntime().totalMemory()/1024/1024 - Runtime.getRuntime().freeMemory()/1024/1024 %>MB <br>
JVMʣ���ڴ�: <%=Runtime.getRuntime().freeMemory()/1024/1024%>MB<br>
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


