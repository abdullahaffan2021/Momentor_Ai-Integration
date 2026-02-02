package com.momentor.mentors.Controller;

import com.momentor.mentors.DTO.*;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.Service.MomService;
import com.momentor.mentors.Service.TaskService;
import com.momentor.mentors.Service.userservice;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.MomRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private userservice userservice;
    @Autowired
    private MomRepository momRepository;
    @Autowired
    private MomService momService;
    //create a task for Respective user
    @PostMapping("/mom/{momid}")
    public TaskResponseDto createTask(@PathVariable Long momid, @Valid @RequestBody TaskRequestDto dto){
        Task task= taskService.createtask(dto.getTitle(), dto.getAssignedTo(), momid);
        return new TaskResponseDto(task.getId(),task.getTitle(),task.getAssignedto(),task.getStatus());
    }
    @PutMapping("/{taskid}/status")
    public TaskStatusResponseDto updatestatus(@PathVariable Long taskid, @RequestParam String status){
        Task task= taskService.updatestatus(taskid,status);
        return new TaskStatusResponseDto (task.getId(),task.getTitle(),task.getStatus());
    }
    //used for progress bar and reports
    @GetMapping("/progress")
    public double getprogress(){
        return taskService.calculatepercentage();
    }
    //Used For PAgination and Sorting
    @GetMapping("/paged")
    public Page<TaskResponseDto> gettaskpaged(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5")int size,
                                              @RequestParam(defaultValue = "createdat")String sortby,
                                              @RequestParam(defaultValue = "desc")String direction){
        return taskService.gettaskwithpagination(page,size,sortby,direction);
    }
    //Students to view the meeting details for the respective Task
    @PreAuthorize("hasAnyRole('STUDENT','MENTOR','ADMIN')")
    @GetMapping("/{taskId}/meeting")
    public ResponseEntity<?> getMeetingByTask(@PathVariable Long taskId, Authentication authentication) {
        try {
            Task task = taskService.getTaskById(taskId);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Task not found"));
            }

            String email = authentication.getName();
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .filter(auth -> auth.contains("ROLE_MENTOR") || auth.contains("ROLE_STUDENT") || auth.contains("ROLE_ADMIN"))
                    .findFirst()
                    .orElse("");

            // STUDENT can only view their own assigned tasks
            if (role.contains("STUDENT")) {
                String studentName = userservice.getUserNameByEmail(email);
                boolean isAssigned = task.getAssignedto().equalsIgnoreCase(email) ||
                        (studentName != null && task.getAssignedto().equalsIgnoreCase(studentName));
                if (!isAssigned) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You are not assigned to this task"));
                }
            }

            // MENTOR/ADMIN can view tasks from their own meetings (or all for ADMIN)
            if (role.contains("MENTOR") || role.contains("ADMIN")) {
                Long userId = userservice.getUserIdByEmail(email);
                Meeting meeting = task.getMom().getMeeting();

                // Only block mentor if they don't own the meeting; admins see everything
                if (!role.contains("ADMIN") && !meeting.getMentorid().equals(userId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only view tasks from your own meetings"));
                }
            }

            // Return meeting + MOM
            Meeting meeting = task.getMom().getMeeting();
            Moms mom = momService.getMomByMeetingOrNull(meeting);

            MeetingWithMomResponseDto dto = new MeetingWithMomResponseDto(
                    meeting.getId(),
                    meeting.getTitle(),
                    meeting.getMeetingdate(),
                    mom != null ? mom.getId() : null,
                    mom != null ? mom.getMomtext() : null
            );

            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(rnfe.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching meeting: " + ex.getMessage()));
        }
    }
    //Mentor Dashboard - Only their tasks
    @GetMapping
    @PreAuthorize("hasRole('MENTOR')")
    public List<TaskResponseDto> getmentortasks(Authentication authentication) {
        String email = authentication.getName();
        Long mentorId = userservice.getUserIdByEmail(email);
        return taskService.gettaskbymentor(mentorId);
    }
    // Keep the student-specific endpoint as is
    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<TaskResponseDto> gettaskbyuser(@PathVariable String username){
        return taskService.gettaskbyuserdto(username);
    }
    // Keep admin endpoint for all tasks
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponseDto> getalltasks(){
        return taskService.getalltaskdto();
    }
// Add these endpoints to your TaskController class

    /**
     * Dashboard summary for MENTOR - shows only their own meeting tasks
     */
    @GetMapping("/dashboard/summary/mentor")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<?> getMentorDashboardSummary(Authentication authentication) {
        try {
            String email = authentication.getName();
            Long mentorId = userservice.getUserIdByEmail(email);

            // Get all tasks for this mentor's meetings
            List<TaskResponseDto> tasks = taskService.gettaskbymentor(mentorId);

            long totalTasks = tasks.size();
            long doneTasks = tasks.stream()
                    .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                    .count();
            long inProgressTasks = tasks.stream()
                    .filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                    .count();
            long notStartedTasks = tasks.stream()
                    .filter(t -> "NOT_STARTED".equalsIgnoreCase(t.getStatus()))
                    .count();

            double overallProgress = totalTasks > 0 ? (doneTasks * 100.0) / totalTasks : 0;

            Map<String, Object> response = new HashMap<>();
            response.put("totalTasks", totalTasks);
            response.put("doneTasks", doneTasks);
            response.put("inProgressTasks", inProgressTasks);
            response.put("notStartedTasks", notStartedTasks);
            response.put("overallprogress", overallProgress);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load dashboard summary: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Dashboard summary for STUDENT - shows only their assigned tasks
     */
    @GetMapping("/dashboard/summary/student")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getStudentDashboardSummary(Authentication authentication) {
        try {
            String email = authentication.getName();

            // Get all tasks assigned to this student
            List<TaskResponseDto> tasks = taskService.gettaskbyuserdto(email);

            long totalTasks = tasks.size();
            long doneTasks = tasks.stream()
                    .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                    .count();
            long inProgressTasks = tasks.stream()
                    .filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                    .count();
            long notStartedTasks = tasks.stream()
                    .filter(t -> "NOT_STARTED".equalsIgnoreCase(t.getStatus()))
                    .count();

            double overallProgress = totalTasks > 0 ? (doneTasks * 100.0) / totalTasks : 0;

            Map<String, Object> response = new HashMap<>();
            response.put("totalTasks", totalTasks);
            response.put("doneTasks", doneTasks);
            response.put("inProgressTasks", inProgressTasks);
            response.put("notStartedTasks", notStartedTasks);
            response.put("overallprogress", overallProgress);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load dashboard summary: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Dashboard summary for ADMIN - shows all tasks
     */
    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminDashboardSummary() {
        try {
            // Get all tasks
            List<TaskResponseDto> tasks = taskService.getalltaskdto();

            long totalTasks = tasks.size();
            long doneTasks = tasks.stream()
                    .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                    .count();
            long inProgressTasks = tasks.stream()
                    .filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                    .count();
            long notStartedTasks = tasks.stream()
                    .filter(t -> "NOT_STARTED".equalsIgnoreCase(t.getStatus()))
                    .count();

            double overallProgress = totalTasks > 0 ? (doneTasks * 100.0) / totalTasks : 0;

            Map<String, Object> response = new HashMap<>();
            response.put("totalTasks", totalTasks);
            response.put("doneTasks", doneTasks);
            response.put("inProgressTasks", inProgressTasks);
            response.put("notStartedTasks", notStartedTasks);
            response.put("overallprogress", overallProgress);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load dashboard summary: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
