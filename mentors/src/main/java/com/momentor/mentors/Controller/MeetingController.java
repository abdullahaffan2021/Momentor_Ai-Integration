package com.momentor.mentors.Controller;

import com.momentor.mentors.DTO.MeetingRequestDto;
import com.momentor.mentors.DTO.ErrorResponse;
import com.momentor.mentors.DTO.MeetingResponseDto;
import com.momentor.mentors.DTO.MomRequestDto;
import com.momentor.mentors.DTO.MomResponseDto;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.Service.MeetingService;
import com.momentor.mentors.Service.MomService;
import com.momentor.mentors.Service.TaskService;
import com.momentor.mentors.Service.userservice;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.repository.MeetingRepository;
import com.momentor.mentors.repository.MomRepository;
import com.momentor.mentors.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/meetings")
public class MeetingController {
    @Autowired
    private MeetingService meetingservice;
    // Create a meeting (MENTOR only)
    @PostMapping
    @PreAuthorize("hasRole('MENTOR')")
    public MeetingResponseDto createmeeting(
            @Valid @RequestBody MeetingRequestDto dto,
            Authentication authentication) {
        return meetingservice.createMeetingService(dto, authentication);
    }
    //Add Mom To The Meeting
    @PostMapping("/{id}/mom")
    public MomResponseDto addmom(
            @PathVariable Long id,
            @Valid @RequestBody MomRequestDto dto) {
        return meetingservice.addMomService(id, dto);
    }
    //List Of The Meetings
    @GetMapping("/my")
    @PreAuthorize("hasRole('MENTOR')")
    public List<MeetingResponseDto> myMeetings(Authentication authentication) {
        return meetingservice.getMyMeetingsService(authentication);
    }
    //View The Specific MOM for Their Respective Meeting
    @GetMapping("/{meetingId}/mom")
    @PreAuthorize("hasAnyRole('STUDENT','MENTOR','ADMIN')")
    public ResponseEntity<?> getMomByMeeting(
            @PathVariable Long meetingId,
            Authentication authentication) {
        return meetingservice.getMomByMeetingService(meetingId, authentication);
    }
    //Delete The Meeting and their Respective Tasks.
    @DeleteMapping("/{meetingId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<?> deleteMeeting(
            @PathVariable Long meetingId,
            Authentication authentication) {
        return meetingservice.deleteMeetingService(meetingId, authentication);
    }
    @GetMapping("/progress/meeting/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','MENTOR','ADMIN')")
    public ResponseEntity<?> getMeetingProgress(@PathVariable Long id) {
        try {
            double progress = meetingservice.calculateMeetingProgress(id);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error calculating progress: " + e.getMessage()));
        }
    }
    //Upload Of Audio
    @PostMapping(value = "/{meetingId}/audio",
            consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<?> audioupload(
            @PathVariable Long meetingId,
            @RequestPart("audio") MultipartFile audio) {
        meetingservice.handlemeeting(meetingId, audio);
        return ResponseEntity.ok(
                "Audio Uploaded Successfully, AI Processing started...."
        );
    }

}
