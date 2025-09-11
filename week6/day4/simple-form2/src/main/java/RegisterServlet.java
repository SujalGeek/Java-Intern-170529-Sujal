import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import org.mindrot.jbcrypt.BCrypt;

@MultipartConfig(maxFileSize = 1024*1024*10)
public class RegisterServlet extends HttpServlet {

    private static final String url = "jdbc:mysql://localhost:3306/demo";
    private static final String username = "root";
    private static final String password2 = "Waheguru\"071!";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String firstName = req.getParameter("firstname");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String gender = req.getParameter("gender");
        String[] courses = req.getParameterValues("courses");
        String terms = req.getParameter("terms");
        

        boolean hasError = false;

        // ===== Password validation =====
        if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$")) {
            req.setAttribute("passwordError", "Password must be at least 8 characters long, include letters, numbers, and special characters");
            hasError = true;
        }

        // ===== Email validation =====
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            req.setAttribute("emailError", "Invalid email format");
            hasError = true;
        }

        // ===== Terms & conditions check =====
        if (terms == null) {
            req.setAttribute("termsError", "Please accept terms & conditions");
            hasError = true;
        }

        // Courses list
        String courseList = "";
        if (courses != null) {
            courseList = String.join(",", courses);
        }

        if (hasError) {
            // Forward back to register.jsp if any validation failed
            RequestDispatcher rd = req.getRequestDispatcher("register.jsp");
            rd.forward(req, resp);
            return;
        }
        
        Part filePart = req.getPart("profileImage");
        InputStream inputStream = null;
        
        
        if(filePart != null && filePart.getSize() > 0)
        {
        	inputStream = filePart.getInputStream();
        }else {
        	req.setAttribute("imageError", "Please upload a profile image");
        	RequestDispatcher rd = req.getRequestDispatcher("register.jsp");
        	rd.forward(req, resp);
        	return ;
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, username, password2)) {

                // ===== Check if email already exists =====
                String checkEmailQuery = "SELECT * FROM user WHERE email = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailQuery)) {
                    checkStmt.setString(1, email);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        req.setAttribute("emailError", "Email already registered!");
                        RequestDispatcher rd = req.getRequestDispatcher("register.jsp");
                        rd.forward(req, resp);
                        return;
                    }
                }

                // ===== Hash password =====
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                // ===== Insert user =====
                String insertQuery = "INSERT INTO user (first_name, password, email, gender, courses, terms, profile_image ) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                    ps.setString(1, firstName);
                    ps.setString(2, hashedPassword);
                    ps.setString(3, email);
                    ps.setString(4, gender);
                    ps.setString(5, courseList);
                    ps.setBoolean(6, true);
                    ps.setBlob(7,inputStream);

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        // ===== Set session =====
                        HttpSession session = req.getSession();
                        session.setAttribute("userEmail", email);
                        session.setAttribute("userName", firstName);

                        RequestDispatcher rd = req.getRequestDispatcher("success.jsp");
                        rd.forward(req, resp);
                    } else {
                        req.setAttribute("generalError", "Registration failed. Try again!");
                        RequestDispatcher rd = req.getRequestDispatcher("register.jsp");
                        rd.forward(req, resp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("generalError", "Error: " + e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("register.jsp");
            rd.forward(req, resp);
        }
    }
}
