<%@ page import="com.tech.blog.dao.PostDao" %>
<%@ page import="com.tech.blog.helper.ConnectionProvider "%>
<%@ page import="java.util.*" %>

<%
    int pid = Integer.parseInt(request.getParameter("pid"));
    PostDao dao = new PostDao(ConnectionProvider.getConnection());
    List<Map<String,String>> comments = dao.getComments(pid);

    if(comments.isEmpty()){
%>
    <p class="text-muted">No comments yet. Be the first to comment!</p>
<%
    } else {
        for(Map<String,String> c : comments) {
%>
<div class="card mb-2">
    <div class="card-body p-2">
        <strong><%= c.get("name") %></strong> <small class="text-muted"><%= c.get("date") %></small>
        <p><%= c.get("content") %></p>
    </div>
</div>
<%
        }
    }
%>
