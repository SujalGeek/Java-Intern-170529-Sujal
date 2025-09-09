package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // don't create a new session
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp"); // if not logged in, go to login
            return;
        }

        // forward request to JSP
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}
