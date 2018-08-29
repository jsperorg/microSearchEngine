<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'error.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <center>
    	<form action="s" method="get">
    		<input type="text" name="w" style="width:250px;height:26px;font-family:Arial;font-size:15px;padding-top:3px;"/>
    		<input type="submit" value="搜索" style="width:50px;height:27px;"/>
    	</form>
    </center>
    <hr>
    <pre>
    抱歉没找到您要搜索的内容。可能是下面的原因导致搜索失败：
    1.暂时还没有你要搜索的内容。
    2.您输入的词语可能不符合相关法律法规，不予以显示。
    </pre>
  </body>
</html>
