package com.momentor.mentors.Service;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.MeetingAudio;
import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.MeetingAudioRepository;
import com.momentor.mentors.repository.MeetingRepository;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Spring HTTP
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
// Spring Security
import org.springframework.security.core.Authentication;
// Java collections
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
// DTOs
import com.momentor.mentors.DTO.MeetingRequestDto;
import com.momentor.mentors.DTO.MeetingResponseDto;
import com.momentor.mentors.DTO.MomRequestDto;
import com.momentor.mentors.DTO.MomResponseDto;
import com.momentor.mentors.DTO.ErrorResponse;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.repository.MomRepository;
import com.momentor.mentors.repository.TaskRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MeetingService {
    @Autowired
    private  MeetingRepository meetingRepository;
    @Autowired
    private MomRepository momRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MeetingAudioRepository meetingAudioRepository;
    @Autowired
    private userservice userservice;
    @Autowired
    private MomService momService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private AiMeetingService aiMeetingService;
    public Meeting createmeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }
    public Meeting getmeeting(Long id) {
        return meetingRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Meeting Not Found"));
    }
    public List<Meeting> getmeetingsbymentor(Long mentorid){
        return meetingRepository.findByMentorid(mentorid);
    }
    public MeetingResponseDto createMeetingService(
            MeetingRequestDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long mentorId = userservice.getUserIdByEmail(email);

        Meeting meet = new Meeting();
        meet.setTitle(dto.getTitle());
        meet.setMeetingdate(dto.getMeetingdate());
        meet.setMentorid(mentorId);

        Meeting saved = createmeeting(meet);

        return new MeetingResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getMeetingdate()
        );
    }
    public MomResponseDto addMomService(Long meetingId, MomRequestDto dto) {
        Meeting meeting = getmeeting(meetingId);
        Moms mom = momService.savemom(dto.getMomtext(), meeting);
        return new MomResponseDto(mom.getId(), mom.getMomtext());
    }
    public List<MeetingResponseDto> getMyMeetingsService(Authentication authentication) {
        String email = authentication.getName();
        Long mentorId = userservice.getUserIdByEmail(email);

        return getmeetingsbymentor(mentorId)
                .stream()
                .map(m -> new MeetingResponseDto(
                        m.getId(),
                        m.getTitle(),
                        m.getMeetingdate()
                ))
                .toList();
    }
    public ResponseEntity<?> getMomByMeetingService(
            Long meetingId,
            Authentication authentication
    ) {
        try {
            Meeting meeting = getmeeting(meetingId);
            if (meeting == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Meeting not found"));
            }
            String email = authentication.getName();
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .filter(auth ->
                            auth.contains("MENTOR") ||
                                    auth.contains("STUDENT") ||
                                    auth.contains("ADMIN"))
                    .findFirst()
                    .orElse("");
            // MENTOR / ADMIN
            if (role.contains("MENTOR") || role.contains("ADMIN")) {
                Long userId = userservice.getUserIdByEmail(email);
                if (!meeting.getMentorid().equals(userId) && !role.contains("ADMIN")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only view MOM from your own meetings"));
                }
            }
            // STUDENT
            if (role.contains("STUDENT")) {
                boolean hasTask =
                        taskService.isStudentAssignedToMeeting(meetingId, email);
                if (!hasTask) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You don't have access to this meeting"));
                }
            }

            Moms mom = momService.getMomByMeetingOrNull(meeting);
            if (mom == null) {
                return ResponseEntity.ok(new MomResponseDto(null, null));
            }

            return ResponseEntity.ok(
                    new MomResponseDto(mom.getId(), mom.getMomtext())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching MOM: " + e.getMessage()));
        }
    }
    public ResponseEntity<?> deleteMeetingService(
            Long meetingId,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            Long mentorId = userservice.getUserIdByEmail(email);

            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Meeting not found"));

            if (!meeting.getMentorid().equals(mentorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("You can only delete your own meetings"));
            }

            List<Moms> moms =
                    momRepository.findAllByMeeting_Id(meetingId);

            for (Moms mom : moms) {
                taskRepository.deleteAll(
                        taskRepository.findByMomId(mom.getId()));
                momRepository.delete(mom);
            }

            meetingRepository.delete(meeting);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Meeting deleted successfully");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error deleting meeting: " + e.getMessage()));
        }
    }
    public double calculateMeetingProgress(Long meetingId) {
        Meeting meeting = getmeeting(meetingId);

        // Get all Moms for this meeting
        List<Moms> moms = momRepository.findAllByMeeting_Id(meetingId);

        if (moms.isEmpty()) {
            return 0;
        }

        int totalTasks = 0;
        double progressPoints = 0;

        // For each Mom, get tasks and calculate progress
        for (Moms mom : moms) {
            List<Task> tasks = taskRepository.findByMomId(mom.getId());
            totalTasks += tasks.size();

            // Calculate progress: NOT_STARTED=0, IN_PROGRESS=0.5, DONE=1
            for (Task task : tasks) {
                if ("DONE".equals(task.getStatus())) {
                    progressPoints += 1.0;
                } else if ("IN_PROGRESS".equals(task.getStatus())) {
                    progressPoints += 0.5;
                }
                // NOT_STARTED contributes 0
            }
        }

        if (totalTasks == 0) {
            return 0;
        }

        return (progressPoints / totalTasks) * 100;
    }


    public void handlemeeting(Long meetingId, MultipartFile audio) {
        String path=saveAudio(audio);
        Meeting meeting=meetingRepository.findById(meetingId).orElseThrow();
        MeetingAudio meetingAudio=new MeetingAudio();
        meetingAudio.setFilePath(path);
        meetingAudio.setFileName(audio.getOriginalFilename());
        meetingAudio.setUploadedAt(LocalDateTime.now());
        meetingAudio.setMeeting(meeting);
        meetingAudioRepository.save(meetingAudio);
        aiMeetingService.processmeeting(meetingId,path);
    }

    private String saveAudio(MultipartFile audio) {
        try {
            String uploadDir = "uploads/audio/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName =
                    System.currentTimeMillis() + "_" + audio.getOriginalFilename();

            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, audio.getBytes());

            return filePath.toString();

        } catch (IOException | java.io.IOException e) {
            throw new RuntimeException("Audio saving failed", e);
        }
    }
}