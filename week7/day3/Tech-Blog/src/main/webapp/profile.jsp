<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import="com.tech.blog.entities.User" %>
<%@ page import="com.tech.blog.entities.Category" %>
<%@ page import="com.tech.blog.entities.Message" %>
<%@ page import="com.tech.blog.dao.PostDao" %>
<%@ page import="com.tech.blog.helper.ConnectionProvider" %>
<%@ page import="java.util.*" %>
<%@ page errorPage="error_page.jsp" %>

<%
    // Get current user from session
    User user = (User) session.getAttribute("currentUser");

    // If user is not logged in, redirect to login page
    if (user == null) {
        response.sendRedirect("login_page.jsp");
        return; // Stop further execution
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Profile Page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<link href='css/style.css' rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

</head>
<body>
<%
    Message msg = (Message) session.getAttribute("message");
    if (msg != null) {
%>
    <div class="alert <%= msg.getCssClass() %> alert-dismissible fade show" role="alert">
        <%= msg.getContent() %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<%
        session.removeAttribute("message"); // remove so it doesn’t show again on refresh
    }
%>

 	<nav class="navbar navbar-expand-lg navbar-dark primary-background">
  <div class="container-fluid">
    <a class="navbar-brand" href="index.jsp">
    <span class="fa fa-asterisk"></span>Tech Blog</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link active" aria-current="page" href="#">
          <span class="	fa fa-bed"></span>
          Learn Coding</a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <span class="fa fa-check-square">
          </span>
            Categories
          </a>
          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            <li><a class="dropdown-item" href="#">Programming Language</a></li>
            <li><a class="dropdown-item" href="#">Project Implementation</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">Data Structure</a></li>
          </ul>
        </li>
        
        <li class="nav-item">
          <a class="nav-link" href="#">
          <span class="fa fa-address-book-o">
          </span>
          Contact</a>
        </li>
        
         <li class="nav-item">
          <a class="nav-link" href="#" data-bs-toggle="modal" data-bs-target="#add-post-modal">
          <span class="fa fa-address-book-o">
          </span>
          Do Post</a>
        </li>
         
      </ul>
      <ul class="navbar-nav ms-auto">
   <li class="nav-item">
     <a class="nav-link" href="#!" data-bs-toggle="modal" data-bs-target="#profile-modal">
    <i class="bi bi-person-circle me-2"></i>
    <%= user.getName() %>
</a>
   </li>
   <li class="nav-item">
      <a class="nav-link" href="LogoutServlet">
         <i class="bi bi-box-arrow-right me-2"></i>
         Logout
      </a>
   </li>
</ul>
    </div>
  </div>
</nav>

<!-- profile mode -->

<!-- Button trigger modal -->

<!-- Modal -->
<div class="modal fade" id="profile-modal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header primary-background text-white text-center">
        <h5 class="modal-title" id="exampleModalLabel"> TechBlog </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="container text-center">
<img src="<%= request.getContextPath() %>/pics/<%= user.getProfile() %>" alt="Profile Image" class="img-fluid rounded-circle" style="border-radius:50%; max-width:150px;;">
        <h5 class="modal-title mt-3" id="exampleModalLabel">
        <%= user.getName() %>
        </h5>
        
        <!-- Details -->
       <div id="profile-details">
        <table class="table">

  <tbody>
    <tr>
      <th scope="row">ID: </th>
      <td><%= user.getId() %></td>
    </tr>
    <tr>
      <th scope="row">Email: </th>
      <td><%=user.getEmail() %></td>
    </tr>
    <tr>
      <th scope="row">Gender</th>
      <td><%= user.getGender() %></td>
    </tr>
    <tr>
      <th scope="row">Status</th>
      <td><%= user.getAbout() %></td>
    </tr>
    <tr>
      <th scope="row">Registered on</th>
      <td><%= user.getDateTime().toString() %></td>
    </tr>

  </tbody>
</table>
        </div>
        
        
        <!-- Profile workload  -->
<!-- Profile Edit Section -->
<!-- Profile Edit Section -->
<div id="profile-edit">
    <h4>Edit Profile</h4>
    <form action="EditServlet" method="post" enctype="multipart/form-data">
        
        <!-- User ID (readonly) -->
        <div class="row mb-3">
            <label for="userid" class="col-sm-4 col-form-label">User ID</label>
            <div class="col-sm-8">
                <input type="text" class="form-control" id="userid" name="userid" 
                       value="<%= user.getId() %>" readonly>
            </div>
        </div>

        <!-- Username -->
        <div class="row mb-3">
            <label for="username" class="col-sm-4 col-form-label">Username</label>
            <div class="col-sm-8">
                <input type="text" class="form-control" id="username" name="username" 
                       value="<%= user.getName() %>" required>
            </div>
        </div>

        <!-- Email -->
        <div class="row mb-3">
            <label for="useremail" class="col-sm-4 col-form-label">Email</label>
            <div class="col-sm-8">
                <input type="email" class="form-control" id="useremail" name="useremail" 
                       value="<%= user.getEmail() %>" required>
            </div>
        </div>

        <!-- Password -->
        <div class="row mb-3">
            <label for="password" class="col-sm-4 col-form-label">Password</label>
            <div class="col-sm-8">
                <input type="password" class="form-control" id="password" name="password" placeholder="Enter new password">
            </div>
        </div>

        <!-- Gender (readonly) -->
        <div class="row mb-3">
            <label for="gender" class="col-sm-4 col-form-label">Gender</label>
            <div class="col-sm-8">
                <input type="text" class="form-control" id="gender" name="gender" 
                       value="<%= user.getGender() %>" readonly>
            </div>
        </div>

        <!-- About -->
        <div class="row mb-3">
            <label for="about" class="col-sm-4 col-form-label">About</label>
            <div class="col-sm-8">
                <textarea class="form-control" id="about" name="about" rows="3"><%= user.getAbout() %></textarea>
            </div>
        </div>

        <!-- Profile Picture -->
        <div class="row mb-3">
            <label for="profilePic" class="col-sm-4 col-form-label">Profile Picture</label>
            <div class="col-sm-8">
                <input type="file" class="form-control" id="profilePic" name="profilePic" accept="image/*">
            </div>
        </div>

        <!-- Submit -->
        <div class="d-flex justify-content-end">
            <button type="submit" class="btn btn-success">Save Changes</button>
        </div>
    </form>
</div>

        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
       <button id="toggle-profile-button" type="button" class="btn btn-primary">Edit</button>
      </div>
    </div>
  </div>
</div>


<!-- add post model  -->


<!-- Modal -->
<div class="modal fade" id="add-post-modal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header bg-primary text-white">
        <h5 class="modal-title">Create a New Post</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="add-post-form" action="AddPostServlet" method="post" enctype="multipart/form-data">
        <div class="modal-body p-4">
          <div class="mb-3">
  <label class="form-label">Category</label>
  <select class="form-select" name="catId" required>
    <option value="" selected disabled>-- Select Category --</option>
    <%
      PostDao postd = new PostDao(ConnectionProvider.getConnection());
      ArrayList<Category> list = postd.getAllCategories();
      for (Category c : list) {
    %>
        <option value="<%= c.getCid() %>"><%= c.getName() %></option>
    <%
      }
    %>
  </select>
</div>
          <div class="mb-3">
            <label class="form-label">Post Title</label>
            <input type="text" class="form-control" name="pTitle" placeholder="Enter post title" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Content</label>
            <textarea class="form-control" name="pContent" rows="5" placeholder="Write your content..."></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Program Code (optional)</label>
            <textarea class="form-control" name="pCode" rows="5" placeholder="Paste your code here..."></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Upload Picture</label>
            <input type="file" class="form-control" name="pPic" accept="image/*">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-success">Publish Post</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://accounts.google.com/gsi/client" async defer></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function(){
    $("#profile-edit").hide(); // start hidden

    $("#toggle-profile-button").click(function(){
        if($("#profile-details").is(":visible")){
            $("#profile-details").hide();
            $("#profile-edit").show();
            $(this).text("Back");
        } else {
            $("#profile-edit").hide();
            $("#profile-details").show();
            $(this).text("Edit");
        }
    });
});
</script>

<!-- now add post js -->
<script>
$(document).ready(function (e){
	alert("loaded..")
$("#add-post-form").on("submit", function(event){
	event.preventDefault();
	console.log("you have clicked on submit...");
	let form = new FormData(this);
	$.ajax({
		url:"AddPostServlet",
		type:"POST",
		data:form,
		success: function(data,textStatus,jqXHR){
			
		},
		error: function(jgXHR,textStatus,errorThrown)
		{
		},
		processData: false,
		contentType: false
	})
})
});
</script>
</body>
</html>
