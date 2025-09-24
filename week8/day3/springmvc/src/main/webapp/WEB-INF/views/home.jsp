<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.List" %>
<html>
<head>
    <title>Home Page</title>
</head>
<body>
<h1>${message}</h1>
<h2>URL: /home</h2>
<%
String name=(String)request.getAttribute("name");
Integer id = (Integer) request.getAttribute("id");
List<String> friends = (List<String>) request.getAttribute("f");
%>
<h1>Name is ${name}</h1>
<h1>Id is ${id}</h1>
<%
for(String s:friends)
{
	%>
<h1><%=s %></h1>
<%
}
%>
</body>
</html>
