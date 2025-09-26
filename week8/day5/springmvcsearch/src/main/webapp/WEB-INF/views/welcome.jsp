<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome Page</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" 
      rel="stylesheet"
      integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" 
      crossorigin="anonymous">
</head>
<body class="bg-light">
    
    <div class="container mt-5">
        <div class="alert alert-success text-center" role="alert">
            <h1 class="display-4">Welcome, ${name}!</h1>
            <p class="lead">Your request was processed successfully by the Spring MVC Controller.</p>
        </div>
        
        <div class="text-center">
            <a href="javascript:history.back()" class="btn btn-primary">Go Back</a>
        </div>
    </div>

</body>
</html>