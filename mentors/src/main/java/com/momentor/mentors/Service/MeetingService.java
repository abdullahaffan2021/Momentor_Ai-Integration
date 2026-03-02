package com.momentor.mentors.Service;
import com.momentor.mentors.DTO.*;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.entity.*;
import com.momentor.mentors.repository.*;
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
    @Autowired
    private TeamRepository teamRepository;
    public Meeting createmeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    } //Create A Meeting For LoggedIn Mentor
    public Meeting getmeeting(Long id) {
        return meetingRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Meeting Not Found")); //Get The Meeting Id From DB
    }
    public List<Meeting> getmeetingsbymentor(Long mentorid){
        return meetingRepository.findByMentorid(mentorid);
    }
    public MeetingResponseDto createMeetingService(
            MeetingRequestDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();  //Get Mentor Name Used For Finding Email
        Long mentorId = userservice.getUserIdByEmail(email); //Check The ID which store In Db for Email Finder

        Meeting meet = new Meeting(); //Create a Meeting Object.
        meet.setTitle(dto.getTitle());
        meet.setMeetingdate(dto.getMeetingdate());
        meet.setMentorid(mentorId);
        meet.setType(dto.getType()); // meeting type

        Meeting saved = createmeeting(meet); //After Creation Of Object we can we can request the DTo and saved innto DB
        if("TEAM".equals(dto.getType()) && dto.getTeams()!=null){
            for(TeamDto teamDto:dto.getTeams()){
                Team team=new Team();
                team.setName(teamDto.getName());
                team.setParticipants(teamDto.getParticipants());
                team.setMeeting(saved);
                teamRepository.save(team);
            }
        }
        return new MeetingResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getMeetingdate()
        ); //Response The Meeting Details
    }
    public MomResponseDto addMomService(Long meetingId, MomRequestDto dto) {
        Meeting meeting = getmeeting(meetingId); //Get The Meeting Id From DB
        Moms mom = momService.savemom(dto.getMomtext(), meeting); //To Save The Mom Of Meeting from specific MomText
        return new MomResponseDto(mom.getId(), mom.getMomtext()); //Mom Response The Id and MomText From
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
            String email = authentication.getName(); //Get The email with the help of getName() function.
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .filter(auth ->
                            auth.contains("MENTOR") ||
                                    auth.contains("STUDENT") ||
                                    auth.contains("ADMIN")) //Filter The Specific Role
                    .findFirst()
                    .orElse("");
            // MENTOR / ADMIN
            if (role.contains("MENTOR") || role.contains("ADMIN")) {
                Long userId = userservice.getUserIdByEmail(email);
                if (!meeting.getMentorid().equals(userId) && !role.contains("ADMIN")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only view MOM from your own meetings"));
                } //If Meeting id not equal to UserId and role not equal to mentor or Admin then it say the error Response.
            }
            // STUDENT
            if (role.contains("STUDENT")) {
                boolean hasTask =
                        taskService.isStudentAssignedToMeeting(meetingId, email); //It Check The This Student is assigned By Task From Particular MMeeting
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
            ); //Finally Return From The Specific Meeting

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
            String email = authentication.getName(); //Get the particular user Of Email
            Long mentorId = userservice.getUserIdByEmail(email); //Get the Particular User Id Of Email.

            Meeting meeting = meetingRepository.findById(meetingId) //Find By Meeting Id
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Meeting not found"));

            if (!meeting.getMentorid().equals(mentorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("You can only delete your own meetings")); //If Meeting Id != Mentor Id Then We cant delete the Other Meetings.
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
        String savedFilename=Paths.get(path).getFileName().toString();
        meetingAudio.setFilePath("/uploads/audio/"+savedFilename);
        meetingAudio.setFileName(savedFilename);
        meetingAudio.setUploadedAt(LocalDateTime.now());
        meetingAudio.setMeeting(meeting);
        meetingAudioRepository.save(meetingAudio);
        aiMeetingService.processmeeting(meetingId,path);
    }

    private String saveAudio(MultipartFile audio) {
        try {
            String uploadDir = Paths.get("uploads/audio").toAbsolutePath().toString();
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