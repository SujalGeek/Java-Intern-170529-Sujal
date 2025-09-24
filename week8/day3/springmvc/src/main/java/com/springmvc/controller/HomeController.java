   package com.springmvc.controller;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model) {
    	System.out.println("This is home url");
    	model.addAttribute("message", "Welcome to Spring MVC!"); 
    	model.addAttribute("name","Sujal Morwani");
    	model.addAttribute("id",1421);
    	
    	List<String> friends = new ArrayList<String>();
    	friends.add("Vandan");
    	friends.add("Roshan");
    	friends.add("Paramjeet");
    	model.addAttribute("f",friends);
    	return "home";
    }
    @GetMapping("/about")
    public String about() {
    	System.out.println("This is about controller");
    	return "about";
    }
    
    @GetMapping("/help")
    public ModelAndView help() {
    	System.out.println("This is help controller");
    
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.addObject("name","Durgesh");
    	modelAndView.addObject("rollnumber",2212);
    	LocalDateTime now = LocalDateTime.now();
    	modelAndView.addObject("time",now);
    	
    	List<Integer> list = new ArrayList<>();
    	list.add(12);
    	list.add(13);
    	list.add(14);
    	list.add(15);
    	
    	modelAndView.addObject("marks",list);
    	
    	
    	modelAndView.setViewName("help");
    	return modelAndView;
    }
}
