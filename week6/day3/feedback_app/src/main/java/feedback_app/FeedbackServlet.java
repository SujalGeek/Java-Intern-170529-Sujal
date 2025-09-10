package feedback_app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;   // ✅ use JDBC Connection
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// form data: get karna hoga
		// form data process
		// response dena hoga:
	
		
		// database connectivity 
		// data save
	
		
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");
		String feedbackMessage = req.getParameter("feedback_message");
		
		if (phone == null || phone.isBlank()) {
		    resp.getWriter().println("<h3 style='color:red'>Phone number is required</h3>");
		    return;
		}
		
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		writer.println("<h1>Feedback servlet is Working</h1>");
		writer.println(""" 
				<h2>Your form details that you have submitted </h2> 				
				<h3> Email address %s </h3> <h3> Phone Number %s  </h3> 
				<h3> Feedback message %s  </h3>
				""".formatted(email,phone,feedbackMessage));
	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","Waheguru\"071!");
		
		PreparedStatement ps = con.prepareStatement("insert into feedback (email,phone,feedbackMessage) values (?,?,?)");
		ps.setString(1,email);
		ps.setString(2, phone);
		ps.setString(3, feedbackMessage);
		
		ps.executeUpdate();
		ps.close();
		con.close();
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Database Operation Failed!!!");
	}
}
	/* resp.sendRedirect("/home"); */
}
