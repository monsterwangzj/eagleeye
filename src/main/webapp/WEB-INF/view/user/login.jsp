<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><% 
String msg = (String) request.getAttribute("message");
System.out.println(msg);
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<title>监控系统登录</title>
</head>
<body>
<div class="bg">
    <div class="con">
        <div class="b_logo"><img src="/styles/images/b_logo.jpg" /></div>
        
        <div class="w_650">
        	<form action="/login/signin.do" method="post" name="form1">
            <table cellpadding="0" cellspacing="0" border="0">
            	<%if (msg != null && !msg.equals("")) {%>
            		<tr><td colspan="4"><font color="red"><%=msg%></font></td></tr>
            	<%}%>
                <tr>
                    <td>用户名：</td>
                    <td><input name="username" type="text" /></td>
                    <td>密码：</td>
                    <td><input name="password" type="password" /></td>
                </tr>  
                <tr><td colspan="4"></td></tr>  
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td><a href="#"><img style=" margin-left:90px;" src="/styles/images/but_login.png" onclick="document.form1.submit()"/></a></td>
                </tr>
            </table>
            </form>
            
        </div>
    </div>
</div>
<script type="text/javascript">
document.onkeydown = function(e) {
	var e = e || window.event;
	if(e.keyCode == 13) {
		document.form1.submit();
	}
}
</script>
</body>
</html>