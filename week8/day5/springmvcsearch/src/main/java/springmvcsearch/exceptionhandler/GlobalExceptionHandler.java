package springmvcsearch.exceptionhandler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleExecption(Exception ex)
	{
		ModelAndView m1 = new ModelAndView("error_page");
		m1.addObject("msg", "An application error has occurred.");
		return m1;
	}

}
