<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.tech.blog.entities.Message" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login Page</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link href='css/style.css' rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

<style>
  body {
    background: linear-gradient(135deg, #1d2b64, #f8cdda);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }

  .card {
    border: none;
    border-radius: 15px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    animation: fadeInUp 0.8s ease-in-out;
  }

  .card-header {
    background: #0d6efd;
    color: white;
    text-align: center;
    font-size: 1.3rem;
    font-weight: bold;
    border-top-left-radius: 15px;
    border-top-right-radius: 15px;
  }

  .btn-primary {
    width: 100%;
    border-radius: 25px;
    transition: all 0.3s ease;
  }

  .btn-primary:hover {
    background-color: #0056b3;
    transform: scale(1.05);
  }

  @keyframes fadeInUp {
    from {
      transform: translateY(40px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }

  .google-btn {
    margin-top: 15px;
    text-align: center;
  }
</style>
</head>
<body>
<!-- Navbar Include -->
<%@include file="normal_navbar.jsp" %>

<!-- Alert Message -->
<%
Object obj = session.getAttribute("message");
if (obj != null) {
    if (obj instanceof com.tech.blog.entities.Message) {
        com.tech.blog.entities.Message msg = (com.tech.blog.entities.Message) obj;
%>
        <div class="alert <%= msg.getCssClass() %> alert-dismissible fade show m-3" role="alert">
            <%= msg.getContent() %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
<%
    } else if (obj instanceof String) {
%>
        <div class="alert alert-info alert-dismissible fade show m-3" role="alert">
            <%= obj %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
<%
    }
    session.removeAttribute("message");
}
%>

<!-- Login Form -->
<main class="d-flex align-items-center justify-content-center" style="min-height: 80vh">
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card">
          <div class="card-header">
            <i class="bi bi-box-arrow-in-right me-2"></i> Login Here
          </div>
          <div class="card-body p-4">
            <form action="LoginServlet" method="post">
              <div class="mb-3">
                <label for="exampleInputEmail1" class="form-label">Email address</label>
                <input name="email" required type="email" class="form-control" id="exampleInputEmail1" placeholder="Enter your email">
              </div>
              <div class="mb-3">
                <label for="exampleInputPassword1" class="form-label">Password</label>
                <input name="password" required type="password" class="form-control" id="exampleInputPassword1" placeholder="Enter your password">
              </div>
              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="exampleCheck1">
                <label class="form-check-label" for="exampleCheck1">Remember me</label>
              </div>
              <button type="submit" class="btn btn-primary shadow w-100 mb-2">
                <i class="bi bi-door-open-fill me-2"></i> Submit
              </button>
            </form>
            <div class="text-center mt-2">
              <span>Don't have an account? </span>
              <a href="register_page.jsp" class="text-primary">Sign Up</a>
            </div>

            <!-- Google Sign-In button at the bottom -->
            <div class="google-btn">
              <div id="g_id_onload"
                   data-client_id="61271818010-uacdkgt9pef25bs1f5ff3u9kk00tm8lm.apps.googleusercontent.com"
                   data-context="signin"
                   data-ux_mode="redirect"
                   data-login_uri="http://localhost:8080/Tech-Blog/GoogleAuthServlet"
                   data-auto_select="true">
              </div>

              <div class="g_id_signin"
                   data-type="standard"
                   data-shape="rectangular"
                   data-theme="filled_blue"
                   data-text="signin_with"
                   data-size="large"
                   data-logo_alignment="left">
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<script src="https://accounts.google.com/gsi/client" async defer></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</body>
</html>
