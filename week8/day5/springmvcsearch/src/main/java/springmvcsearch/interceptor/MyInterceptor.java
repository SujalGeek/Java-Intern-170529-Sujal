package springmvcsearch.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyInterceptor implements HandlerInterceptor {

	// The preHandle method must match the HandlerInterceptor signature exactly.
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		System.out.println("This is prehandler. Request received.");
		
		// You can implement logic here, for example, checking a session attribute.
		// if (session.getAttribute("user") == null) {
		//    response.sendRedirect("login"); // Block the request
		//    return false; 
		// }
		
		return true; // Return true to allow the request to proceed to the controller.
	}

	// postHandle method is called AFTER the controller method is executed.
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("This is posthandler. Controller execution finished.");
	}

	// afterCompletion method is called AFTER the view is rendered (clean-up phase).
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("This is afterCompletion. View rendering complete.");
	}
}