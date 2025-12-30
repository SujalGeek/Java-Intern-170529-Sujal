package com.example.enotes.service;

import org.springframework.stereotype.Service;

@Service
public interface HomeService {

	public Boolean verifyAccount(Integer userId, String verificationCode) throws Exception;
}
