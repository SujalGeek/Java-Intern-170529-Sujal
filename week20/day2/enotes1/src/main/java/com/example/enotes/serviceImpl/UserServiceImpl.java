package com.example.enotes.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.enotes.dto.UserDto;
import com.example.enotes.entities.Role;
import com.example.enotes.entities.User;
import com.example.enotes.repository.RoleRepository;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.UserService;
import com.example.enotes.util.Validation;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private Validation validation;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@Override
	public Boolean register(UserDto userDto) {
		validation.userValidation(userDto);
		User user = modelMapper.map(userDto, User.class);
		
		setRole(userDto,user);
		
		User saveUser = userRepository.save(user);
		
		if(!ObjectUtils.isEmpty(saveUser))
		{
			return true;
		}
		return false;
	}
	
	private void setRole(UserDto userDto, User user)
	{
		List<Integer> reqRoleId = userDto.getRoles().stream().map(r-> r.getId()).toList();
		List<Role> roles = roleRepository.findAllById(reqRoleId);
		user.setRoles(roles);
	}

}
