package springmvcsearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SearchController {
    
    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/search")
    public ModelAndView search(@RequestParam("querybox") String query) {
        if (query == null || query.isBlank()) {
            ModelAndView mv = new ModelAndView("home");
            mv.addObject("errorMsg", "Please enter a keyword before searching!");
            return mv;
        }

        String url = "https://www.google.com/search?q=" + query;
        RedirectView redirectView = new RedirectView(url);
        return new ModelAndView(redirectView);
    } 
    
    @RequestMapping("/welcome")
    public String welcome(@RequestParam("user") String name,Model m)
    {
    	System.out.println(name);
    	m.addAttribute("name",name);
    	return "welcome"; 
    }
}
