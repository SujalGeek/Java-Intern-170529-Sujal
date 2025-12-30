package com.example.enotes.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.endpoint.HomeEndpoint;
import com.example.enotes.service.HomeService;
import com.example.enotes.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class HomeController implements HomeEndpoint {

	
	Logger log= org.slf4j.LoggerFactory.getLogger(HomeController.class);

	
	@Autowired
	private HomeService homeService;
	
	@Override
	public ResponseEntity<?> verifyAccount(Integer userId,String code) throws Exception{
		log.info("HomeController : verifyUserAccount() : Exceution Start");
		Boolean verified = homeService.verifyAccount(userId, code);
	
		if(verified)
		{
			return CommonUtil.createBuildResponseMessage("Account Verified Success", HttpStatus.OK);
		}
		else {
			log.info("HomeController : verifyUserAccount() : Exceution End");
			return CommonUtil.createErrorResponseMessage("Invalid Verfication Link", HttpStatus.BAD_REQUEST);
		}
	}
}
