package com.tech.blog.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.tech.blog.dao.PostDao;
import com.tech.blog.entities.Post;
import com.tech.blog.entities.User;
import com.tech.blog.helper.ConnectionProvider;

/**
 * Servlet implementation class CommentServlet
 */
@WebServlet("/CommentServlet")
public class CommentServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		int pid = Integer.parseInt(request.getParameter("pid"));
		String content = request.getParameter("comment");
		User user = (User) request.getSession().getAttribute("currentUser");
	
	if(user ==  null)
	{
		response.getWriter().write("Login to comment");
		return ;
	}
	
	PostDao dao = new PostDao(ConnectionProvider.getConnection());
	boolean added = dao.addComment(pid, user.getId(), content);
	
	response.getWriter().write( added ? "Comment added" : "Failed to add the comment");
	
	}

}
