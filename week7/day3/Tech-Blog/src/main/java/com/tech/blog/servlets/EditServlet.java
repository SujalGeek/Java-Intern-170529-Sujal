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
import com.tech.blog.helper.Helper;
import com.tech.blog.helper.PasswordHasher;

@WebServlet("/EditServlet")
@MultipartConfig
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

        // handle password (only update if new password entered)
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordHasher.hashPassword(password));
        }

        // handle profile picture
        if (image != null && !image.isEmpty()) {
            // define paths
            String oldFilePath = req.getServletContext().getRealPath("/") + "pics" + File.separator + user.getProfile();
            String newFilePath = req.getServletContext().getRealPath("/") + "pics" + File.separator + image;

            // delete old photo if not default.png
            if (!user.getProfile().equals("default.png")) {
                Helper.deleteFile(oldFilePath);
            }

            // save new photo
            if (Helper.saveFile(part.getInputStream(), newFilePath)) {
                user.setProfile(image);
            } else {
                System.out.println("Error while saving new profile picture");
            }
        }

        // update database
        UserDao userDao = new UserDao(ConnectionProvider.getConnection());
        boolean ans = userDao.updateUser(user);

        if (ans) {
            Message msg = new Message("Profile updated successfully!", "success", "alert-success");
            session.setAttribute("message", msg);

            // refresh current user in session
            session.setAttribute("currentUser", user);

            resp.sendRedirect("profile.jsp");
        } else {
            Message msg = new Message("Something went wrong while updating profile.", "error", "alert-danger");
            session.setAttribute("message", msg);
            resp.sendRedirect("profile.jsp");
        }
    }
}
