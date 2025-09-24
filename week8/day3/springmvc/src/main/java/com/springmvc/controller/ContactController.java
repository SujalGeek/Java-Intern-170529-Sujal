package com.springmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.springmvc.model.User;
import com.springmvc.service.UserService;


@Controller
public class ContactController 
{
	@Autowired
	private UserService userService;

	@ModelAttribute
	public void commonDataforModel(Model m)
	{
		m.addAttribute("Header","Form");
		m.addAttribute("Desc", "Home");
		System.out.println("adding the common data to model");
	}
	
	@RequestMapping("/contact")
	public String showForm(Model m) {
		System.out.println("Creating form");
		return "contact";
	}
	
	@RequestMapping(path="/processform", method = RequestMethod.POST)
	public String handleForm(@ModelAttribute("user") User user,Model model)
	{	
		/*
		 * user.setEmail(userEmail); user.setUserName(userName);
		 * user.setUserPassword(userPassword);
		 */	
		System.out.println(user);
		this.userService.createUser(user);
// success
//		model.addAttribute("user",user);
	/*
	 * model.addAttribute("Header","Form"); model.addAttribute("Desc", "Home");
	 */
		return "success";
	}

}
