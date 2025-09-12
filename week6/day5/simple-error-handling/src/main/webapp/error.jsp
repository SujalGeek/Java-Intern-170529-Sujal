<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error Page</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
  <div class="row">
    <div class="col-md-6 offset-md-3">
      <div class="card shadow">
        <div class="card-header bg-danger text-white text-center">
          <h3>Error Occurred</h3>
        </div>
        <div class="card-body">
          <p><strong>Message:</strong> <%= exception.getMessage() %></p>
          <p>Please make sure you are dividing by a valid number.</p>
        </div>
        <div class="card-footer text-center">
          <a href="index.jsp" class="btn btn-dark">Go Back</a>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
