package com.springmvc.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home"); // maps to /WEB-INF/views/home.jsp
        mv.addObject("message", "Welcome to Spring 6 + Java 21!");
        return mv;
    }
    @GetMapping("/about")
    public String about() {
    	System.out.println("This is about controller");
    	return "about";
    }
}
