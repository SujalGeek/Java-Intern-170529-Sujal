package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/demo", "root", "Waheguru\"071!"
            );

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(username,password) VALUES(?,?)"
            );
            ps.setString(1, username);
            ps.setString(2, password); // For production, hash passwords!

            ps.executeUpdate();
            ps.close();
            conn.close();

            response.sendRedirect("login.jsp"); // go to login after signup

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Signup failed: " + e.getMessage());
        }
    }
}