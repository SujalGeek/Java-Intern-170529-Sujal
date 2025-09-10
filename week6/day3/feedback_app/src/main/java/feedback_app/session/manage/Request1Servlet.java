package feedback_app.session.manage;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.*;

//@WebServlet(initParams = [])
public class Request1Servlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// we have to create the cookie and send in the response
		String username = "sujal";
		String userId = "1234";
		
		Cookie cookie = new Cookie("username",username);
		var cookie1 = new Cookie("userid",userId);
		
		HttpSession session = req.getSession();
		session.setAttribute("userSecret", UUID.randomUUID().toString());
		session.setMaxInactiveInterval(10*60);
		
		
		ServletConfig servletConfig = getServletConfig();
		
		ServletContext servletContext = getServletContext();
		String app_name = servletContext.getInitParameter("app_name");
		String userName = servletConfig.getInitParameter("userName");
		
		cookie.setMaxAge(10*60);
		cookie1.setMaxAge(5*60);
		resp.addCookie(cookie);
		resp.addCookie(cookie1);
		
		resp.setContentType("text/html");
		resp.getWriter().println("<h1>Cookie set successfully</h1>");	
	
		resp.getWriter().print("""
				<h1>Context Params: %s</h1>
				<h1>Init Params: %s </h1>
				""".formatted(app_name,userName));
	}

}
