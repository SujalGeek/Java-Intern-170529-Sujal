package com.example.enotes.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.enotes.entities.AccountStatus;
import com.example.enotes.entities.User;
import com.example.enotes.exception.ResourceNotFoundException;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.HomeService;

@Service
public class HomeServiceImpl implements HomeService{

	@Autowired
	private UserRepository userRepository;
	
	
	
	@Override
	public Boolean verifyAccount(Integer userId, String verificationCode) throws Exception {

		User user = userRepository.findById(userId)
				.orElseThrow(
						() -> new ResourceNotFoundException("Invalid User"));
				
		if(user.getStatus().getVerficationCode() ==  null)
		{
			return true;
		}
		
		if(user.getStatus().getVerficationCode().equals(verificationCode))
		{
			AccountStatus status = user.getStatus();
			status.setIsActive(true);
			status.setVerficationCode(null);
		
			userRepository.save(user);
			return true;
		}
		return false;
	}

}
