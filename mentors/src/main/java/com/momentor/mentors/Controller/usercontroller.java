package com.momentor.mentors.Controller;
import com.momentor.mentors.Service.userservice;
import com.momentor.mentors.entity.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class usercontroller {
    @Autowired
    private userservice userservice;
    @PostMapping("/createuser")
    public user createuser(@RequestBody user user){
        return userservice.saveuser(user);
    }
    @GetMapping("/getuser")
    public List<user> getuser(){
        return userservice.getallusers();
    }

}
