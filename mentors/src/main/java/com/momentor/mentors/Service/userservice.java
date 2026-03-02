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

        //  Unique name check
        userrepository.findByNameIgnoreCase(user.getName()) //Check The Unique Name
                .ifPresent(u -> {
                    throw new ResourceNotFoundException(
                            "Name already exists. Please use a different initial or Add Father Name"
                    );
                });
        user.setPassword(passwordEncoder.encode(user.getPassword()));   //Encode The Password
        return userrepository.save(user); //Save The User In DB
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
    public String getUserEmailByName(String name) {
        return userrepository.findByNameIgnoreCase(name)
                .map(u -> u.getEmail())
                .orElse(null);
    }

}
