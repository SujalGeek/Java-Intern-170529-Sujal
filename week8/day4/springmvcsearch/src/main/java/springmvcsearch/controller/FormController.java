package springmvcsearch.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springmvcsearch.entities.Student;

@Controller
public class FormController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, "date", new CustomDateEditor(dateFormat, true));
    }

	@RequestMapping("/complex")
	public String showForm() {
		
		return "complex_form";
	}
	
	@RequestMapping(path = "/handleform",method= RequestMethod.POST)
	public String handleForm(@ModelAttribute("student") Student student, Model model, BindingResult result) {
		if(result.hasErrors())
		{
			return "complex_form";
		}
		System.out.println(student);
		model.addAttribute("student", student);
		return "success";
	}
	
}
