<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome to Servlet Crash</title>
<link rel="stylesheet" href=" <%= application.getContextPath()  %>/css/style.css" />
</head>
<body>
	<div class="container">
	<%@ include file="menu.jsp" %>
	<h1>
		Welcome to Servlet Crash !!
	</h1>
	<p>This is very information thing in the servlet!! </p>
	
	</div>
</body>
</html>