<%@ page import="com.tech.blog.entities.Post" %>
<%@ page import="com.tech.blog.dao.PostDao" %>
<%@ page import="com.tech.blog.helper.ConnectionProvider" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.tech.blog.entities.Post" %>

<%
    int pid = 0;
    try { pid = Integer.parseInt(request.getParameter("pid")); } catch(Exception e) { response.sendRedirect("profile.jsp"); }

    PostDao dao = new PostDao(ConnectionProvider.getConnection());
    Post p = dao.getPostById(pid); // You need to create getPostById in PostDao
    if(p == null) { response.sendRedirect("profile.jsp"); }
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%= p.getpTitle() %></title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
<div class="container mt-4">
    <div class="card">
<% if(p.getpPic() != null && !p.getpPic().isEmpty()) { %>
    <img src="<%=request.getContextPath()%>/pics/<%= p.getpPic() %>" 
         alt="Post Image" 
         class="img-fluid">
<% } %>
        <div class="card-body">
            <h2><%= p.getpTitle() %></h2>
            <p><%= p.getpContent() %></p>
            <pre><%= p.getpCode() %></pre>

            <!-- Like & Comment -->
            <div class="d-flex justify-content-start mt-3">
                <button class="btn btn-primary me-2" id="like-btn" data-pid="<%= p.getPid() %>">
                    <i class="fa fa-thumbs-up"></i> Like
                </button>
                <button class="btn btn-secondary" id="comment-btn" data-pid="<%= p.getPid() %>">
                    <i class="fa fa-comment"></i> Comment
                </button>
            </div>

            <!-- Comments Section -->
            <div id="comments-section" class="mt-3"></div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function(){
    let pid = $("#like-btn").data("pid");

    // Like button click
    $("#like-btn").click(function(){
        $.post("LikeServlet", { pid: pid }, function(data){
            alert(data); // You can improve by showing updated like count
        });
    });

    // Comment button click
    $("#comment-btn").click(function(){
        let comment = prompt("Enter your comment:");
        if(comment) {
            $.post("CommentServlet", { pid: pid, comment: comment }, function(data){
                alert(data);
                loadComments();
            });
        }
    });

    function loadComments(){
        $("#comments-section").load("LoadComments.jsp?pid=" + pid);
    }

    loadComments();
});
</script>
</body>
</html>
