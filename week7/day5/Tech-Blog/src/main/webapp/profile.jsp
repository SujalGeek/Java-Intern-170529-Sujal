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
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Profile Page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet" type="text/css" />
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
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<%
        session.removeAttribute("message");
    }
%>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark primary-background">
  <div class="container-fluid">
    <a class="navbar-brand" href="index.jsp">
      <span class="fa fa-asterisk"></span> Tech Blog
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link active" href="#"><span class="fa fa-bed"></span> Learn Coding</a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
            <span class="fa fa-check-square"></span> Categories
          </a>
          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            <li><a class="dropdown-item" href="#">Programming Language</a></li>
            <li><a class="dropdown-item" href="#">Project Implementation</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">Data Structure</a></li>
          </ul>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#"><span class="fa fa-address-book-o"></span> Contact</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#" data-bs-toggle="modal" data-bs-target="#add-post-modal">
            <span class="fa fa-plus-circle"></span> Do Post
          </a>
        </li>
      </ul>
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <a class="nav-link" href="#!" data-bs-toggle="modal" data-bs-target="#profile-modal">
            <i class="bi bi-person-circle me-2"></i> <%= user.getName() %>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="LogoutServlet">
            <i class="bi bi-box-arrow-right me-2"></i> Logout
          </a>
        </li>
      </ul>
    </div>
  </div>
</nav>

<!-- Profile Modal -->
<div class="modal fade" id="profile-modal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header primary-background text-white">
        <h5 class="modal-title">TechBlog</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="container text-center">
          <img src="<%= request.getContextPath() %>/pics/<%= user.getProfile() %>" 
               alt="Profile Image" class="img-fluid rounded-circle" style="max-width:150px;">
          <h5 class="mt-3"><%= user.getName() %></h5>

          <!-- Profile Details -->
          <div id="profile-details">
            <table class="table">
              <tbody>
                <tr><th>ID:</th><td><%= user.getId() %></td></tr>
                <tr><th>Email:</th><td><%= user.getEmail() %></td></tr>
                <tr><th>Gender:</th><td><%= user.getGender() %></td></tr>
                <tr><th>Status:</th><td><%= user.getAbout() %></td></tr>
                <tr><th>Registered on:</th><td><%= user.getDateTime().toString() %></td></tr>
              </tbody>
            </table>
          </div>

          <!-- Profile Edit -->
          <div id="profile-edit">
            <h4>Edit Profile</h4>
            <form action="EditServlet" method="post" enctype="multipart/form-data">
              <div class="mb-3">
                <label>User ID</label>
                <input type="text" class="form-control" name="userid" value="<%= user.getId() %>" readonly>
              </div>
              <div class="mb-3">
                <label>Username</label>
                <input type="text" class="form-control" name="username" value="<%= user.getName() %>" required>
              </div>
              <div class="mb-3">
                <label>Email</label>
                <input type="email" class="form-control" name="useremail" value="<%= user.getEmail() %>" required>
              </div>
              <div class="mb-3">
                <label>Password</label>
                <input type="password" class="form-control" name="password" placeholder="Enter new password">
              </div>
              <div class="mb-3">
                <label>Gender</label>
                <input type="text" class="form-control" name="gender" value="<%= user.getGender() %>" readonly>
              </div>
              <div class="mb-3">
                <label>About</label>
                <textarea class="form-control" name="about"><%= user.getAbout() %></textarea>
              </div>
              <div class="mb-3">
                <label>Profile Picture</label>
                <input type="file" class="form-control" name="profilePic" accept="image/*">
              </div>
              <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-success">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        <button id="toggle-profile-button" class="btn btn-primary">Edit</button>
      </div>
    </div>
  </div>
</div>

<!-- Main Content -->
<main>
  <div class="container mt-4">
    <div class="row">
      <div class="col-md-4">
        <!-- Categories -->
        <div class="list-group">
         <a href="#" class="list-group-item list-group-item-action active category-link" data-cid="0">All Posts</a>

          <%
            PostDao d = new PostDao(ConnectionProvider.getConnection());
            ArrayList<Category> sidebarCategories = d.getAllCategories();
            for(Category cc: sidebarCategories) {
          %>
            <a href="#" class="list-group-item list-group-item-action category-link" data-cid="<%= cc.getCid() %>"><%= cc.getName() %></a>
          <%
            }
          %>
        </div>
      </div>

<div class="col-md-8">
  <!-- Posts Section -->
<div id="loader" class="text-center" style="display:none;">
    <i class="fa fa-refresh fa-spin"></i>
    <h3 class="mt-2">Loading...</h3>
</div>

<div class="row" id="post-container"></div>
</div>

</main>

<!-- Add Post Modal -->
<div class="modal fade" id="add-post-modal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header bg-primary text-white">
        <h5 class="modal-title">Create a New Post</h5>
        <button class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="add-post-form" action="AddPostServlet" method="post" enctype="multipart/form-data">
        <div class="modal-body">
          <div class="mb-3">
            <label>Category</label>
            <select class="form-select" name="catId" required>
              <option value="" disabled selected>-- Select Category --</option>
              <%
                PostDao postd = new PostDao(ConnectionProvider.getConnection());
                ArrayList<Category> modalCategories = postd.getAllCategories();
                for (Category c : modalCategories) {
              %>
                <option value="<%= c.getCid() %>"><%= c.getName() %></option>
              <%
                }
              %>
            </select>
          </div>
          <div class="mb-3">
            <label>Post Title</label>
            <input type="text" class="form-control" name="pTitle" required>
          </div>
          <div class="mb-3">
            <label>Content</label>
            <textarea class="form-control" name="pContent" rows="5"></textarea>
          </div>
          <div class="mb-3">
            <label>Program Code (optional)</label>
            <textarea class="form-control" name="pCode" rows="5"></textarea>
          </div>
          <div class="mb-3">
            <label>Upload Picture</label>
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

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function(){

  // ---------------- PROFILE EDIT TOGGLE ----------------
  $("#profile-edit").hide();

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

  // ---------------- ADD POST SUBMIT ----------------
  $("#add-post-form").on("submit", function(e){
    e.preventDefault();
    let form = new FormData(this);

    $.ajax({
      url: "<%= request.getContextPath() %>/AddPostServlet",
      type: "POST",
      data: form,
      processData: false,
      contentType: false,
      beforeSend: function () {
        $(".btn-success").prop("disabled", true).text("Publishing...");
      },
      success: function (data) {
        if (data.includes("success")) {
          $("body").prepend(`
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              ✅ Post published successfully!
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>`);

          $("#add-post-form")[0].reset();
          $("#add-post-modal").modal("hide");

          // reload posts in currently active category
          let activeCatId = $(".category-link.active").data("catid") || 0;
          loadPosts(activeCatId);

        } else {
          $("body").prepend(`
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
              ⚠️ Something went wrong.
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>`);
        }
      },
      error: function () {
        $("body").prepend(`
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ❌ Failed to save post. Try again.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>`);
      },
      complete: function () {
        $(".btn-success").prop("disabled", false).text("Publish Post");
      }
    });
  });

  // ---------------- LOAD POSTS FUNCTION ----------------
  function loadPosts(catId = 0) {
    $("#loader").show();
    $("#post-container").load("load_post.jsp?catId=" + catId, function (response, status) {
      if (status === "success") {
        $("#loader").hide();
      } else {
        $("#post-container").html("<p class='text-danger'>❌ Failed to load posts.</p>");
      }
    });
  }

  // ---------------- INITIAL LOAD ----------------
  loadPosts(0); // all posts on page load

  // ---------------- CATEGORY CLICK ----------------
  $(document).on("click", ".category-link", function(e){
    e.preventDefault();
    let catId = $(this).data("cid"); // matches your HTML attribute
    console.log("Category ID:", catId);
    // highlight active
    $(".category-link").removeClass("active");
    $(this).addClass("active");

    // load posts for selected category
    loadPosts(catId);
  });

});
</script>

</body>
</html>
