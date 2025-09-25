<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDateTime" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Help Page</title>
</head>
<body>
<h1>This is the help page</h1>
<%-- <%
String name = (String) request.getAttribute("name");
Integer rollNumber = (Integer) request.getAttribute("rollnumber");
LocalDateTime  time = (LocalDateTime) request.getAttribute("time");
%> --%>
<h1>Name is ${name}</h1>
<h1>Roll Number is ${rollnumber}</h1>
<h1>The time is ${time}</h1>
<hr>

<ul>
  <c:forEach var="item" items="${marks}">
    <h1>${item}</h1>   
  </c:forEach>
</ul>
</body>
</html>