package com.momentor.mentors.Controller;

import com.momentor.mentors.Service.MeetingService;
import com.momentor.mentors.Service.MomService;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    @Autowired
    private  MeetingService meetingservice;
    @Autowired
    private MomService momservice;
    @PostMapping
    public Meeting createmeeting(@RequestBody Meeting meeting){
        return meetingservice.createmeeting(meeting);
    }
    @PostMapping("/{id}/mom")
    public Moms addmom(@PathVariable Long id,@RequestBody String momText){
        Optional<Meeting> meetingServiceOptional=meetingservice.getmeeting(id);
        return meetingServiceOptional.map(m -> momservice.savemom(momText,m)).orElse(null);
    }
}
