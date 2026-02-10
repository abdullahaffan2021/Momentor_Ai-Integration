package com.momentor.mentors.Service;
import com.momentor.mentors.DTO.UserResponseDto;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.entity.user;
import com.momentor.mentors.repository.userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class userservice {
    @Autowired
    private userrepository userrepository;
    @Autowired
    public PasswordEncoder passwordEncoder;
    public user saveuser(user user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userrepository.save(user);
    }
    public List<UserResponseDto> getallusers() {
        List<user> users=userrepository.findAll();
        return users.stream()
                .map(user -> new UserResponseDto(
                        user.getId(),user.getName(),user.getEmail(),user.getRole()
                ))
                .toList();
    }
    public Long getUserIdByEmail(String email) {
        return userrepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }
    public String getUserNameByEmail(String email) {
        return userrepository.findByEmail(email)
                .map(u -> u.getName())
                .orElse(null);
    }

}
