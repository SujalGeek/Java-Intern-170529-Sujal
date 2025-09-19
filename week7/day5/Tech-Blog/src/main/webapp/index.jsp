<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%@ page import="com.tech.blog.helper.ConnectionProvider" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>TechBlog</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<link href='css/style.css' rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

</head>
<body>
	<%@include file="normal_navbar.jsp"  %>
	
	<!--// banner  -->
<!-- Banner -->
<div class="container-fluid p-0 m-0">
  <div class="bg-dark text-light p-5 mb-4 rounded-3 shadow-sm">
    <div class="container py-5">
      <h1 class="display-2 fw-bold text-warning">Welcome, friends</h1>
      <h2 class="text-info fw-semibold">Tech Blog</h2>
      <p class="lead mt-3">
        A programming language differs from a natural language in many ways – 
        especially intent. A natural language is intended for communicating 
        between people, while a programming language is intended to allow 
        people to control a computer.
      </p>
      
      <!-- Buttons -->
      <a href="register_page.jsp" class="btn btn-warning btn-lg me-3 shadow">
        <i class="bi bi-rocket-takeoff-fill me-2"></i> Start! It's Free
      </a>
      
      <a href="login_page.jsp" class="btn btn-outline-light btn-lg shadow">
        <i class="bi bi-box-arrow-in-right me-2"></i> Login
      </a>
    </div>
  </div>
</div>
<br>
<div class="container my-5">
  <div class="row g-4">
    <!-- Card 1 -->
    <div class="col-md-4">
      <div class="card h-100 shadow-sm border-0">
        <div class="card-body">
          <h5 class="card-title text-primary fw-bold">Java Programming</h5>
          <p class="card-text">
            Learn Java from scratch! Build applications, work with OOP concepts,
            and explore advanced features step by step.
          </p>
          <a href="https://www.w3schools.com/java/" class="btn btn-outline-primary" target="_blank">
            <i class="bi bi-book me-2"></i> Read More
          </a>
        </div>
      </div>
    </div>

    <!-- Card 2 -->
    <div class="col-md-4">
      <div class="card h-100 shadow-sm border-0">
        <div class="card-body">
          <h5 class="card-title text-success fw-bold">Python Programming</h5>
          <p class="card-text">
            Master Python for web development, data science, and AI with easy-to-follow
            tutorials and projects.
          </p>
          <a href="https://www.w3schools.com/python/" class="btn btn-outline-success" target="_blank">
            <i class="bi bi-book me-2"></i> Read More
          </a>
        </div>
      </div>
    </div>

    <!-- Card 3 -->
    <div class="col-md-4">
      <div class="card h-100 shadow-sm border-0">
        <div class="card-body">
          <h5 class="card-title text-danger fw-bold">C++ Programming</h5>
          <p class="card-text">
            Dive into C++ for competitive programming, system programming,
            and performance-critical applications.
          </p>
          <a href="https://www.geeksforgeeks.org/c-plus-plus/" class="btn btn-outline-danger" target="_blank">
            <i class="bi bi-book me-2"></i> Read More
          </a>
        </div>
      </div>
    </div>
  </div>

  <!-- Second Row of 3 Cards -->
  <div class="row g-4 mt-4">
    <!-- Card 4 -->
    <div class="col-md-4">
      <div class="card h-100 shadow-sm border-0">
        <div class="card-body">
          <h5 class="card-title text-warning fw-bold">JavaScript</h5>
          <p class="card-text">
            Become a front-end expert by mastering JavaScript, ES6+, and modern frameworks like React.
          </p>
          <a href="https://www.w3schools.com/js/" class="btn btn-outline-warning" target="_blank">
            <i class="bi bi-code-slash me-2"></i> Read More
          </a>
        </div>
      </div>
    </div>

    <!-- Card 5 -->
    <div class="col-md-4">
      <div class="card h-100 shadow-sm border-0">
        <div class="card-body">
          <h5 class="card-title text-info fw-bold">C Programming</h5>
          <p class="card-text">
            Understand the fundamentals of programming with C and learn how modern
            languages evolved from it.
          </p>
          <a href="https://www.w3schools.com/c/c_intro.php" class="btn btn-outline-info" target="_blank">
            <i class="bi bi-code-square me-2"></i> Read More
          </a>
        </div>
      </div>
    </div>

    <!-- Card 6 -->
    <div class="col-md-4">
      <div class="card h-100 shadow-sm border-0">
        <div class="card-body">
          <h5 class="card-title text-secondary fw-bold">PHP Programming</h5>
          <p class="card-text">
            Explore backend development with PHP and build dynamic websites and applications.
          </p>
          <a href="https://www.w3schools.com/php/" class="btn btn-outline-secondary" target="">
            <i class="bi bi-terminal-dash me-2"></i> Read More
          </a>
        </div>
      </div>
    </div>
  </div>
</div>



  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    
</body>
</html>