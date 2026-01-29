package com.momentor.mentors.Controller;
import com.momentor.mentors.DTO.LogInResponseDto;
import com.momentor.mentors.DTO.LoginRequestDto;
import com.momentor.mentors.DTO.UserRequestDto;
import com.momentor.mentors.DTO.UserResponseDto;
import com.momentor.mentors.Service.AuthService;
import com.momentor.mentors.Service.userservice;
import com.momentor.mentors.entity.user;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private userservice userservice;
    @PostMapping("/login")
    public LogInResponseDto login(@Valid @RequestBody LoginRequestDto dto){
        String token= authService.login(dto);
        return new LogInResponseDto(token);
    }
    @PostMapping("/createuser")
    public UserResponseDto createuser(@Valid @RequestBody UserRequestDto userdto){
        user user=new user();
        user.setName(userdto.getName());
        user.setEmail(userdto.getEmail());
        user.setPassword(userdto.getPassword());
        user.setRole(userdto.getRole());
        user saved=userservice.saveuser(user);
        return new UserResponseDto(saved.getId(),saved.getName(),saved.getEmail(),saved.getRole());
    }
}
