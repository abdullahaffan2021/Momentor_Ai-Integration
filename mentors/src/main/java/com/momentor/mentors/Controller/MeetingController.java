package com.momentor.mentors.Controller;

import com.momentor.mentors.DTO.MeetingRequestDto;
import com.momentor.mentors.DTO.MeetingResponseDto;
import com.momentor.mentors.DTO.MomRequestDto;
import com.momentor.mentors.DTO.MomResponseDto;
import com.momentor.mentors.Service.MeetingService;
import com.momentor.mentors.Service.MomService;
import com.momentor.mentors.Service.TaskService;
import com.momentor.mentors.Service.userservice;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/meetings")
public class MeetingController {
    @Autowired
    private MeetingService meetingservice;
    @Autowired
    private MomService momservice;
    @Autowired
    private userservice userservice;
    @Autowired
    private TaskService taskService;

    // Create a meeting (MENTOR only)
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public MeetingResponseDto createmeeting(
            @Valid @RequestBody MeetingRequestDto dto,
            Authentication authentication) {

        String email = authentication.getName();
        Long mentorId = userservice.getUserIdByEmail(email);

        Meeting meet = new Meeting();
        meet.setTitle(dto.getTitle());
        meet.setMeetingdate(dto.getMeetingdate());
        meet.setMentorid(mentorId);  // Get from logged-in user, not from request

        Meeting saved = meetingservice.createmeeting(meet);
        return new MeetingResponseDto(
                saved.getId(), saved.getTitle(), saved.getMeetingdate()
        );
    }

    // Add MOM to a meeting
    @PostMapping("/{id}/mom")
    public MomResponseDto addmom(@Valid @PathVariable Long id, @Valid @RequestBody MomRequestDto dto) {
        Meeting meeting = meetingservice.getmeeting(id);
        Moms mom = momservice.savemom(dto.getMomtext(), meeting);
        return new MomResponseDto(mom.getId(), mom.getMomtext());
    }

    // Get progress for a specific meeting
    @GetMapping("/progress/meeting/{meetingid}")
    public double progresspermeeting(@PathVariable Long meetingid) {
        return taskService.calculatemeetingprogress(meetingid);
    }

    // Get meetings for the logged-in mentor (ONLY THEIR OWN)
    @GetMapping("/my")
    @PreAuthorize("hasRole('MENTOR')")
    public List<MeetingResponseDto> myMeetings(Authentication authentication) {
        String email = authentication.getName();
        Long mentorId = userservice.getUserIdByEmail(email);
        return meetingservice.getmeetingsbymentor(mentorId)
                .stream()
                .map(m -> new MeetingResponseDto(
                        m.getId(),
                        m.getTitle(),
                        m.getMeetingdate()
                ))
                .collect(Collectors.toList());
    }

    // Get MOM for a meeting
    // MENTOR can view their own meetings' MOM
    // STUDENT can view MOM only if they have a task assigned from this meeting
    @PreAuthorize("hasAnyRole('STUDENT','MENTOR','ADMIN')")
    @GetMapping("/{meetingId}/mom")
    public ResponseEntity<?> getMomByMeeting(@PathVariable Long meetingId, Authentication authentication) {
        try {
            Meeting meeting = meetingservice.getmeeting(meetingId);
            if (meeting == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Meeting not found"));
            }

            String email = authentication.getName();
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .filter(auth -> auth.contains("MENTOR") || auth.contains("STUDENT") || auth.contains("ADMIN"))
                    .findFirst()
                    .orElse("");

            // MENTOR/ADMIN can view MOM of their own meetings
            if (role.contains("MENTOR") || role.contains("ADMIN")) {
                Long userId = userservice.getUserIdByEmail(email);
                if (!meeting.getMentorid().equals(userId) && !role.contains("ADMIN")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only view MOM from your own meetings"));
                }
            }

            // STUDENT can view MOM only if they have a task assigned from this meeting
            if (role.contains("STUDENT")) {
                Long userId = userservice.getUserIdByEmail(email);
                boolean hasTaskInMeeting = taskService.isStudentAssignedToMeeting(meetingId, email);
                if (!hasTaskInMeeting) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You don't have access to this meeting"));
                }
            }

            Moms mom = momservice.getMomByMeetingOrNull(meeting);
            if (mom == null) {
                return ResponseEntity.ok(new MomResponseDto(null, null));
            }

            return ResponseEntity.ok(new MomResponseDto(mom.getId(), mom.getMomtext()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching MOM: " + e.getMessage()));
        }
    }
}

// Error Response DTO
class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}