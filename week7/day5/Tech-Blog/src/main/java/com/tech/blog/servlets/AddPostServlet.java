package com.tech.blog.servlets;

import java.io.File;
import java.io.IOException;

import com.tech.blog.dao.PostDao;
import com.tech.blog.entities.Post;
import com.tech.blog.entities.User;
import com.tech.blog.helper.ConnectionProvider;
import com.tech.blog.helper.Helper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/AddPostServlet")
@MultipartConfig
public class AddPostServlet  extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int catId = Integer.parseInt(req.getParameter("catId"));
		String pTitle = req.getParameter("pTitle");
		String pContent = req.getParameter("pContent");
		String pCode = req.getParameter("pCode");
		Part part = req.getPart("pPic");
//		Getting user id
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("currentUser");
		Post p = new Post(pTitle,pContent,pCode,part.getSubmittedFileName(),null,catId,user.getId());
		
		String filename = part.getSubmittedFileName();
		
		PostDao dao = new PostDao(ConnectionProvider.getConnection());
		 boolean saved = dao.savePost(p);

	        if (saved) {
	            // Save file to "pics" folder
	            String path = req.getServletContext().getRealPath("/") + "pics" + File.separator + filename;
	            Helper.saveFile(part.getInputStream(), path);

	            resp.sendRedirect("profile.jsp?msg=Post+added+successfully");
	        } else {
	            resp.sendRedirect("profile.jsp?error=Post+not+saved");
	        }
	}

}
