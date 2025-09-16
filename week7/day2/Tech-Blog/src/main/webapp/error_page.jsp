<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error Occurred</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            color: #333;
            text-align: center;
            padding: 50px;
        }
        .container {
            display: inline-block;
            padding: 30px;
            border-radius: 10px;
            background-color: #ffe6e6;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            font-size: 48px;
            color: #d9534f;
        }
        p {
            font-size: 18px;
            margin: 15px 0;
        }
        img {
            width: 200px;
            margin-bottom: 20px;
        }
        a {
            display: inline-block;
            padding: 10px 20px;
            margin-top: 20px;
            background-color: #d9534f;
            color: #fff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
        }
        a:hover {
            background-color: #c9302c;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Replace the src with your own image path if you want -->
        <img src="img/error-img.jpg" alt="Oops! Error">
        <h1>Oops! Something went wrong.</h1>
        <p><strong>Error Message:</strong> <%= exception != null ? exception.getMessage() : "Unknown Error" %></p>
        <p><strong>Exception Type:</strong> <%= exception != null ? exception.getClass().getName() : "N/A" %></p>
        <a href="index.jsp">Go Back Home</a>
    </div>
</body>
</html>
