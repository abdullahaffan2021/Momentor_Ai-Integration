package com.momentor.mentors.Service;

import com.momentor.mentors.DTO.LoginRequestDto;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.entity.user;
import com.momentor.mentors.repository.userrepository;
import com.momentor.mentors.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    @Autowired
    private userrepository userrepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    public String login(LoginRequestDto loginRequestDto){
        user user=userrepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(()->new ResourceNotFoundException("Invalid Credentials")); //Fetch User By Email
        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){ //Bcrypt The Input Password and Database Password
            throw new ResourceNotFoundException(("Invalid Credentials"));
        }
        return jwtUtil.generateToken(user.getEmail(),user.getRole()); //Generate The Token Based On JwtUtil where Subject=Email,Role and Expiration Be Set As One Hour
    }
}
