package com.example.spring_sec_demo.service;

import com.example.spring_sec_demo.model.User;
import com.example.spring_sec_demo.repositry.UserRepo;
import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    private User user;

    public MyUserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       User user = userRepo.findByUsername(username);

       if(user == null)
       {
           System.out.println("User 404");
           throw new UsernameNotFoundException("User 404");
       }

       return new MyUserPrincipal(user);
    }
}
