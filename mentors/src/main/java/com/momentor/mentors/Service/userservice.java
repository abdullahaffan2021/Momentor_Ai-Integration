package com.momentor.mentors.Service;
import com.momentor.mentors.entity.user;
import com.momentor.mentors.repository.userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class userservice {
    @Autowired
    private userrepository userrepository;
    public user saveuser(user user) {
        return userrepository.save(user);
    }
    public List<user> getallusers() {
        return userrepository.findAll();
    }
}
