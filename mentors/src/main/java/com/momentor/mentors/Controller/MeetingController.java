package com.momentor.mentors.Controller;
import com.momentor.mentors.DTO.MeetingRequestDto;
import com.momentor.mentors.DTO.MeetingResponseDto;
import com.momentor.mentors.DTO.MomRequestDto;
import com.momentor.mentors.DTO.MomResponseDto;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.Service.MeetingService;
import com.momentor.mentors.Service.MomService;
import com.momentor.mentors.Service.TaskService;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/meetings")
public class MeetingController {
    @Autowired
    private  MeetingService meetingservice;
    @Autowired
    private MomService momservice;
    @Autowired
    private TaskService taskService;
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public MeetingResponseDto createmeeting(@Valid @RequestBody MeetingRequestDto dto){
        Meeting meet=new Meeting();
        meet.setTitle(dto.getTitle());
        meet.setMeetingdate(dto.getMeetingdate());
        meet.setMentorid(dto.getMentorid());
        Meeting saved=meetingservice.createmeeting(meet);
        return new MeetingResponseDto(
                saved.getId(), saved.getTitle(),saved.getMeetingdate()
        );
    }
    @PostMapping("/{id}/mom")
    public MomResponseDto addmom(@Valid @PathVariable Long id,@Valid @RequestBody MomRequestDto dto){
        Meeting meeting=meetingservice.getmeeting(id);
        Moms mom=momservice.savemom(dto.getMomtext(),meeting);
        return new MomResponseDto(mom.getId(),mom.getMomtext());
    }
    @GetMapping("/progress/meeting/{meetingid}")
    public double progresspermeeting(@PathVariable Long meetingid){
        return taskService.calculatemeetingprogress(meetingid);
    }
    @GetMapping
    public List<MeetingResponseDto> getallmeetings(){
        return meetingservice.getallmeetings()
                .stream()
                .map(m->new MeetingResponseDto(m.getId(),m.getTitle(),m.getMeetingdate()))
                .collect(Collectors.toList());

    }
}
