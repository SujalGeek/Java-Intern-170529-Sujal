package com.tech.blog.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

import com.tech.blog.dao.UserDao;
import com.tech.blog.entities.User;
import com.tech.blog.helper.ConnectionProvider;
import com.tech.blog.helper.PasswordHasher;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
@MultipartConfig
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Fetch form parameters
        String name = req.getParameter("user_name");
        String email = req.getParameter("user_email");
        String password = req.getParameter("user_password");
        String confirmPassword = req.getParameter("confirmpassword");
        String gender = req.getParameter("gender");
        String about = req.getParameter("about");
        String terms = req.getParameter("checkbox");
      
        
        // Validation
        if (terms == null) {
            req.setAttribute("errorMessage", "You must agree to the terms & conditions!");
            req.getRequestDispatcher("register_page.jsp").forward(req, resp);
            return;
        }

        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            gender == null || gender.trim().isEmpty()) {

            req.setAttribute("errorMessage", "All fields are required!");
            req.getRequestDispatcher("register_page.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match!");
            req.getRequestDispatcher("register_page.jsp").forward(req, resp);
            return;
        }
        String hashedPassword = PasswordHasher.hashPassword(password);
        // Create user object
        User user = new User(name, email, hashedPassword, gender, about);

        try {
            Connection conn = ConnectionProvider.getConnection();
            UserDao userDao = new UserDao(conn);
            boolean saved = userDao.saveUser(user);

            if (saved) {
                HttpSession session = req.getSession();
                session.setAttribute("message", "Registration successful! Login now.");
                resp.sendRedirect("login_page.jsp"); // Only redirect on success
            } else {
                req.setAttribute("errorMessage", "Something went wrong! Please try again!");
                req.getRequestDispatcher("register_page.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Server error! Please try again!");
            req.getRequestDispatcher("register_page.jsp").forward(req, resp);
        }
    }
}