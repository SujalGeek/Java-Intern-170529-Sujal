<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Success Page</title>
</head>
<body>
<h1>Name: ${student.name}</h1>
<h1>ID: ${student.id}</h1>
<h1>DOB: ${student.date}</h1>
<h1>Gender: ${student.gender}</h1>
<h1>Type: ${student.type}</h1>
<ul>
<c:forEach var="sub" items="${student.subjects}">
    <li>${sub}</li>
</c:forEach>
</ul>
</body>
</html>