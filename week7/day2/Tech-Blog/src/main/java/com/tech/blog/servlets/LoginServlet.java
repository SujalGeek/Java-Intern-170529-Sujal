package com.tech.blog.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.tech.blog.dao.UserDao;
import com.tech.blog.entities.Message;
import com.tech.blog.entities.User;
import com.tech.blog.helper.ConnectionProvider;
import com.tech.blog.helper.PasswordHasher;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. Get user input
        String userEmail = req.getParameter("email");
        String userPassword = req.getParameter("password");
        
        // 2. Hash the input password (same algorithm as signup/edit)
        String hashedPassword = PasswordHasher.hashPassword(userPassword);
        
        // 3. Get UserDao and verify credentials using hashed password
        UserDao dao = new UserDao(ConnectionProvider.getConnection());
        User user = dao.getUserbyEmailAndPassword(userEmail, hashedPassword);
        
        if(user == null) {
            // 4. Login failed → redirect back to login page with error
            HttpSession session = req.getSession();
            Message message = new Message("Invalid email or password!", "error", "alert-danger");
            session.setAttribute("message", message);
            resp.sendRedirect("login_page.jsp");
        } else {
            // 5. Login successful → create session and redirect to profile
            HttpSession session = req.getSession();
            session.setAttribute("currentUser", user);
            resp.sendRedirect("profile.jsp");
        }
    }
}
