<%@ page session="true" %>
<%
    String email = (String) session.getAttribute("userEmail");
    if (email == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
    <h2>Welcome, <%= email %>!</h2>
    <form action="logout" method="post">
        <input type="submit" value="Logout">
    </form>
</body>
</html>

