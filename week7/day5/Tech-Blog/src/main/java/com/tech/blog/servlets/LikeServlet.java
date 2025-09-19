package com.tech.blog.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.tech.blog.dao.PostDao;
import com.tech.blog.entities.User;
import com.tech.blog.helper.ConnectionProvider;

/**
 * Servlet implementation class LikeServlet
 */
@WebServlet("/LikeServlet")
public class LikeServlet extends HttpServlet {
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int pid = Integer.parseInt(request.getParameter("pid"));
		User user = (User) request.getSession().getAttribute("currentUser");
		
		if(user == null)
		{
		response.getWriter().write("Login to like");
		return ;
		}
		
		PostDao dao = new PostDao(ConnectionProvider.getConnection());
		boolean added = dao.addLike(pid, user.getId());
		int count = dao.countLikes(pid);
		
		if(added)
		{
			response.getWriter().write("Liked! Total Likes" +count);	
		}
		else {response.getWriter().write("Already liked! Total Likes: " + count);
		}
		}
}
