<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!doctype html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<title>Complex Form</title>

	<!-- Bootstrap 5 CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" 
	      rel="stylesheet"
	      integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" 
	      crossorigin="anonymous">
</head>
<body style="background: #e2e2e2;">

	<div class="container mt-4">
		<div class="row">
			<div class="col-md-6 offset-md-3">
				<div class="card shadow-sm">
					<div class="card-body">
						<h3 class="text-center mb-4">Complex Form</h3>
<div class="alert alert-danger" role="alert">
 <form:errors path="student.*"></form:errors>
</div>

						<form action="handleform" method="post">

							<!-- Name -->
							<div class="mb-3">
								<label for="name" class="form-label">Your Name</label>
								<input name="name" type="text" class="form-control" id="name" placeholder="Enter Name">
							</div>

							<!-- ID -->
							<div class="mb-3">
								<label for="id" class="form-label">Your Id</label>
								<input name="id" type="text" class="form-control" id="id" placeholder="Enter ID">
							</div>

							<!-- DOB -->
							<div class="mb-3">
								<label for="dob" class="form-label">Your DOB</label>
								<input name="date" type="date" class="form-control" id="dob" placeholder="dd/mm/yyyy">
							</div>

							<!-- Subjects -->
							<div class="mb-3">
								<label for="subjects" class="form-label">Select Courses</label>
								<select name="subjects" id="subjects" class="form-select" multiple>
									<option>Java</option>
									<option>Python</option>
									<option>C++</option>
									<option>Spring</option>
									<option>Hibernate</option>
								</select>
							</div>

							<!-- Gender -->
							<div class="mb-3">
								<label class="form-label d-block">Select Gender</label>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="radio" name="gender" id="male" value="male">
									<label class="form-check-label" for="male">Male</label>
								</div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="radio" name="gender" id="female" value="female">
									<label class="form-check-label" for="female">Female</label>
								</div>
							</div>

							<!-- Type -->
							<div class="mb-3">
								<label for="type" class="form-label">Select Type</label>
								<select class="form-select" name="type" id="type">
									<option value="oldstudent">Old Student</option>
									<option value="normalstudent">Normal Student</option>
								</select>
							</div>
								
								<div class="card mb-3">
  <div class="card-body">
    <p class="mb-2 text-center">Your Address</p>
    <div class="mb-3">
      <input name="address.street" type="text" class="form-control" placeholder="Enter Street" />
    </div>
    <div class="mb-3">
      <input name="address.city" type="text" class="form-control" placeholder="Enter City" />
    </div>
  </div>
</div>

							<!-- Submit -->
							<div class="d-grid">
								<button type="submit" class="btn btn-primary">Submit</button>
							</div>

						</form>

					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Bootstrap 5 Bundle JS (includes Popper) -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" 
	        integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" 
	        crossorigin="anonymous"></script>
</body>
</html>
