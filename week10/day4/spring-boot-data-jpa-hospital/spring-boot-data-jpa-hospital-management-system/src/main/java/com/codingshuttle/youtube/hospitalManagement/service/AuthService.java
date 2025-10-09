package com.codingshuttle.youtube.hospitalManagement.service;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.entity.type.AuthProviderType;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import com.codingshuttle.youtube.hospitalManagement.security.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final AuthUtil authUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    public LoginResponseDto login(LoginRequestDto loginRequestDto)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),loginRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(token,user.getId());
    }


    public User signUpInternal(LoginRequestDto signupRequestDto, AuthProviderType authProviderType, String providerId)
    {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if(user != null)
        {
            throw new IllegalArgumentException("User already exits");
        }

       user = User.builder()
               .username(signupRequestDto.getUsername())
               .providerId(providerId)
               .providerType(authProviderType)
               .build();
        if(authProviderType == AuthProviderType.EMAIL)
        {
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }
    return userRepository.save(user);
    }

    //login controller
    public SignUpResponseDto signup(LoginRequestDto signupRequestDto) {

        User user = signUpInternal(signupRequestDto,AuthProviderType.EMAIL,null);
        return new SignUpResponseDto(user.getId(),user.getUsername());
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {

    // fetch the providerType and providerId
        // save the providerType and provider id info with user
        // if the user has an account: directly login

        //otherwise, first signup and then login
//        AuthProviderType
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User,registrationId);

        User user = userRepository.findByProviderIdAndProviderType(providerId,providerType).orElse(null);

        String email = oAuth2User.getAttribute("email");
        User emailUser = userRepository.findByUsername(email).orElse(null);

        if(user == null && emailUser == null)
        {
            // signup flow:
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User,registrationId,providerId);

            user =signUpInternal(new LoginRequestDto(username,null),providerType,providerId);
        }
        else if(user != null)
        {
            if(email != null && !email.isBlank() && !email.equals(user.getUsername()))
            {
                user.setUsername(email);
                userRepository.save(user);
            }
        }
        else{
            throw new BadCredentialsException("This email is already registered with provider "+emailUser.getProviderType());
        }

        LoginResponseDto loginResponseDto =new LoginResponseDto(authUtil.generateAccessToken(user),user.getId());
        return ResponseEntity.ok(loginResponseDto);
    }
}
