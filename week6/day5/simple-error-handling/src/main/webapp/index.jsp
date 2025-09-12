<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" errorPage="error.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Module Project | Home Page</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
  <div class="row">
    <div class="col-md-6 offset-md-3">
      <div class="card shadow">
        <div class="card-header bg-dark text-white text-center">
          <h3>Provide me two numbers</h3>
        </div>
        <form action="result.jsp" method="post">
          <div class="card-body bg-secondary">
            <div class="form-group mb-3">
              <input type="number" name="n1" class="form-control" placeholder="Enter n1" required />
            </div>
            <div class="form-group mb-3">
              <input type="number" name="n2" class="form-control" placeholder="Enter n2" required />
            </div>
          </div>
          <div class="card-footer text-center bg-dark text-white">
            <button type="submit" class="btn btn-outline-light">Divide</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
</body>
</html>
