<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Registration</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background: linear-gradient(120deg, #89f7fe, #66a6ff);
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
    }

    .form-container {
        background: white;
        padding: 30px;
        border-radius: 12px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        width: 400px;
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
        color: #333;
    }

    label {
        font-weight: bold;
        display: block;
        margin-top: 10px;
    }

    input, select {
        width: 100%;
        padding: 10px;
        margin-top: 6px;
        border: 1px solid #ccc;
        border-radius: 6px;
    }

    .checkbox-group {
        margin-top: 10px;
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
    }

    .checkbox-group label {
        display: flex;
        align-items: center;
        gap: 6px; /* space between checkbox and text */
        font-weight: normal;
    }

    button, input[type="submit"], input[type="reset"] {
        margin-top: 20px;
        width: 100%;
        background: #66a6ff;
        border: none;
        padding: 12px;
        color: white;
        font-size: 16px;
        border-radius: 6px;
        cursor: pointer;
    }

    button:hover, input[type="submit"]:hover, input[type="reset"]:hover {
        background: #4a90e2;
    }
</style>
</head>
<body>
    <div class="form-container">
        <h2>Registration Form</h2>
        <form action="RegisterServlet" method="post" enctype="multipart/form-data">
            <label>First Name:</label>
            <input type="text" name="firstname" required>

           <label>Password:</label>
<input type="password" name="password" value="<%= request.getParameter("password") != null ? request.getParameter("password") : "" %>">
<% if(request.getAttribute("passwordError") != null) { %>
    <span style="color:red;"><%= request.getAttribute("passwordError") %></span>
<% } %>


         <label>Email:</label>
<input type="email" name="email" value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
<% if(request.getAttribute("emailError") != null) { %>
    <span style="color:red;"><%= request.getAttribute("emailError") %></span>
<% } %>

            <label>Gender:</label>
            <select name="gender" required>
                <option value="">--Select--</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
            </select>

            <label>Courses:</label>
            <div class="checkbox-group">
                <label><input type="checkbox" name="courses" value="Java"> Java</label>
                <label><input type="checkbox" name="courses" value="Python"> Python</label>
                <label><input type="checkbox" name="courses" value="C++"> C++</label>
            </div>

            <div class="checkbox-group">
                <label><input type="checkbox" name="terms" value="yes" required> I agree to Terms and Conditions</label>
            </div>

<label>Profile Image:</label>
    <input type="file" name="profileImage" accept="image/*">
    <% if(request.getAttribute("imageError") != null) { %>
        <span style="color:red;"><%= request.getAttribute("imageError") %></span>
    <% } %>
    
            <input type="submit" value="Register">
            <input type="reset" value="Reset">
        </form>
    </div>
</body>
</html>
