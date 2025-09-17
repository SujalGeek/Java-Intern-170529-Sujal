<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register Page</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link href='css/style.css' rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

<style>
  body {
    background: linear-gradient(135deg, #667eea, #764ba2);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }

  .card {
    border: none;
    border-radius: 15px;
    box-shadow: 0px 8px 25px rgba(0,0,0,0.2);
    animation: fadeInDown 0.9s ease-in-out;
  }

  .card-header {
    background: #0d6efd;
    color: #fff;
    text-align: center;
    font-size: 1.4rem;
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

  @keyframes fadeInDown {
    from {
      transform: translateY(-40px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }
</style>
</head>
<body>
<%@include file="normal_navbar.jsp" %>
<main class="d-flex align-items-center justify-content-center" style="height: 100vh">
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card">
          <div class="card-header text-center">
            <i class="bi bi-person-plus-fill me-2"></i> Register Here
          </div>
          <div class="card-body p-4">
            
            <!-- Alert box for errors or success -->
            <% String message = (String) request.getAttribute("message"); %>
            <% String error = (String) request.getAttribute("errorMessage"); %>
            <% if(message != null) { %>
              <div class="alert alert-success alert-dismissible fade show" role="alert">
                <%= message %>
              </div>
            <% } else if(error != null) { %>
              <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <%= error %>
              </div>
            <% } %>

            <form id="reg_form" action="RegisterServlet" method="post">
              <!-- Username -->
              <div class="mb-3">
                <label class="form-label">Username</label>
                <input type="text" name="user_name" class="form-control" placeholder="Enter your username">
              </div>

              <!-- Email -->
              <div class="mb-3">
                <label class="form-label">Email address</label>
                <input type="email" name="user_email" class="form-control" placeholder="Enter your email">
              </div>

              <!-- Password -->
              <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" name="user_password" class="form-control" placeholder="Enter your password">
              </div>

              <!-- Confirm Password -->
              <div class="mb-3">
                <label class="form-label">Confirm Password</label>
                <input type="password" name="confirmpassword" class="form-control" placeholder="Re-enter your password">
              </div>

	<div class="mb-3">
    <label class="form-label">Gender</label><br>
    <div class="form-check form-check-inline">
      <input class="form-check-input" type="radio" name="gender" id="male" value="male">
      <label class="form-check-label" for="male">Male</label>
    </div>
    <div class="form-check form-check-inline">
      <input class="form-check-input" type="radio" name="gender" id="female" value="female">
      <label class="form-check-label" for="female">Female</label>
    </div>
  </div>

  <!-- About Yourself -->
  <div class="mb-3">
    <label class="form-label" >About Yourself</label>
    <textarea class="form-control" name="about" rows="3" placeholder="Enter something about yourself"></textarea>
  </div>
	
              <!-- Checkbox -->
              <div class="mb-3 form-check">
                <input type="checkbox" name="checkbox" class="form-check-input" id="termsCheck">
                <label class="form-check-label" for="termsCheck">
                  I agree to the <a href="terms.jsp">terms & conditions</a>
                </label>
              </div>

              <!-- Submit -->
              <button type="submit" class="btn btn-primary shadow">
                <i class="bi bi-person-check-fill me-2"></i> Register
              </button>
            </form>
            
            <div id="pleaseWait" class="alert alert-info text-center mt-3" style="display:none;">
  <i class="bi bi-arrow-repeat"></i> Please wait, redirecting...
</div>

          </div>
          <div class="card-footer text-center text-muted">
            Already have an account? <a href="login_page.jsp">Login</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function() {
  $('#reg_form').on('submit', function() {
      // Show the "please wait" message
      $('#pleaseWait').show();
      // Disable submit button to prevent double click
      $(this).find('button[type="submit"]').prop('disabled', true);
  });

  // Auto-hide alert messages after 3 seconds
  setTimeout(function(){
    $('.alert').slideUp();
  }, 3000);

});

</script>
</body>
</html>
