<%@ page language="java" contentType="text/html;charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/styles/control.css" type="text/css" rel="stylesheet" />
<title>什么是响应时间？</title>
</head>
<body>
	<div class="bg01">
		<div class="con">
			<%@ include file="/WEB-INF/view/common/header.jsp"%>

			<div class="w_960 pt">
				<div class="w_left">
					<%@ include file="/WEB-INF/view/common/questions.jsp"%>
				</div>
				<div class="w_right">
					<div class="w_735">
						<div class="title01">
							<h4 class="fl">什么是响应时间？</h4>
						</div>
						<div class="help_word">
							<p>从用户对站点或服务器发送请求开始，一直到目标内容下载到用户端，这段时间就是响应时间。任何时候我们都希望响应时间越短越好，这意味着用户可以更快的访问您的站点或服务器。</p>
							<p>&nbsp;</p>
							<p>
								对于网页/HTTP类型的站点监控，响应时间只针对网页本身，包括了从DNS解析、与网站服务器建立网络连接、网站服务器处理到下载网页内容等多个环节，监控系统详细记录了每次的检查快照，您可以通过这些数据来分析如何优化性能。
							</p>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
