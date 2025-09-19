<%@page import="java.util.*"%>
<%@page import="com.tech.blog.entities.Post"%>
<%@page import="com.tech.blog.helper.ConnectionProvider"%>
<%@page import="com.tech.blog.dao.PostDao"%>

<%
    int catId = 0;
    try { catId = Integer.parseInt(request.getParameter("catId")); } catch(Exception e) { catId = 0; }

    PostDao dao = new PostDao(ConnectionProvider.getConnection());
    List<Post> posts = (catId == 0) ? dao.getAllPosts() : dao.getPostByCatId(catId);

    if(posts.isEmpty()) {
%>
<div class="col-md-12 text-center mt-3">
    <h5 class="text-muted">🚫 No posts in this category.</h5>
</div>
<%
    } else {
        for(Post p : posts) {
%>


<div class="col-md-6 mt-2">
  <div class="card">
    <% if(p.getpPic() != null && !p.getpPic().isEmpty()) { %>
      <img src="pics/<%= p.getpPic() %>" class="card-img-top" alt="Post Image">
    <% } %>
    <div class="card-body">
      <h5><%= p.getpTitle() %></h5>
      <p><%= p.getpContent().length() > 100 ? p.getpContent().substring(0, 100) + "..." : p.getpContent() %></p>
      <pre><%= p.getpCode().length() > 100 ? p.getpCode().substring(0,100) + "..." : p.getpCode() %></pre>

      <!-- Buttons -->
      <div class="d-flex justify-content-between mt-2">
        <button class="btn btn-sm btn-outline-primary like-btn" data-pid="<%= p.getPid() %>">
          <i class="fa fa-thumbs-up"></i> Like
        </button>
        <button class="btn btn-sm btn-outline-secondary comment-btn" data-pid="<%= p.getPid() %>">
          <i class="fa fa-comment"></i> Comment
        </button>
        <a href="post_detail.jsp?pid=<%= p.getPid() %>" class="btn btn-sm btn-outline-success">
          Read More
        </a>
      </div>
    </div>
  </div>
</div>
<%
        }
    }
%>
