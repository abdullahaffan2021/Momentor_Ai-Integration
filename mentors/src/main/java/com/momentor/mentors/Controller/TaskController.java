package com.momentor.mentors.Controller;

import com.momentor.mentors.DTO.TaskRequestDto;
import com.momentor.mentors.DTO.TaskResponseDto;
import com.momentor.mentors.DTO.TaskStatusResponseDto;
import com.momentor.mentors.Service.TaskService;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.MomRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private MomRepository momRepository;
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
    //Mentor Dashboard for Admin Overview
    @GetMapping
    public List<TaskResponseDto> getalltasks(){
        return taskService.getalltaskdto();
    }
    //Student Specific Task View
    @GetMapping("/user/{username}")
    public List<TaskResponseDto> gettaskbyuser(@PathVariable String username){
        return taskService.gettaskbyuserdto(username);
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
}
