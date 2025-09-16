package com.tech.blog.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

import com.tech.blog.dao.UserDao;
import com.tech.blog.entities.Message;
import com.tech.blog.entities.User;
import com.tech.blog.helper.ConnectionProvider;
import com.tech.blog.helper.PasswordHasher;

@WebServlet("/EditServlet")
@MultipartConfig   // needed for file upload
public class EditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // get user from session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            resp.sendRedirect("login_page.jsp");
            return;
        }

        // fetch form data
        String userEmail = req.getParameter("useremail");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String userAbout = req.getParameter("about");
        Part part = req.getPart("profilePic");
        String image = part.getSubmittedFileName();

        // update user details
        user.setEmail(userEmail);
        user.setName(username);
        user.setAbout(userAbout);

        // handle password (hash only if new password entered)
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordHasher.hashPassword(password));
        }

        // handle profile picture upload (only if user uploads new file)
        if (image != null && !image.isEmpty()) {
            String uploadPath = req.getServletContext().getRealPath("/") + "pics" + File.separator + image;
            try {
                part.write(uploadPath); // save file physically
                user.setProfile(image); // update DB with new filename
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // update database
        UserDao userDao = new UserDao(ConnectionProvider.getConnection());
        boolean ans = userDao.updateUser(user);

        if (ans) {
            Message msg = new Message("Profile updated successfully!", "success", "alert-success");
            session.setAttribute("message", msg);
            resp.sendRedirect("profile.jsp");
        } else {
            Message msg = new Message("Something went wrong while updating profile.", "error", "alert-danger");
            session.setAttribute("message", msg);
            resp.sendRedirect("profile.jsp");
        }
    }
}
