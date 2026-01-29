package com.momentor.mentors.Controller;
import com.momentor.mentors.DTO.UserRequestDto;
import com.momentor.mentors.DTO.UserResponseDto;
import com.momentor.mentors.Service.userservice;
import com.momentor.mentors.entity.user;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class usercontroller {
    @Autowired
    private userservice userservice;
    @GetMapping("/getuser")
    public List<UserResponseDto> getuser(){
        return userservice.getallusers();
    }
}
