package com.momentor.mentors.Controller;

import com.momentor.mentors.Service.TaskService;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.MomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private MomRepository momRepository;
    @PostMapping("/mom/{momid}")
    public Task createTask(@PathVariable Long momid, @RequestParam String title,@RequestParam String assignedto){
        Moms mom=momRepository.findById(momid).orElseThrow(()->new RuntimeException("MOM Not Found"));
        return taskService.createtask(title,assignedto,mom);
    }
    @PutMapping("/{taskid}/status")
    public Task updatestatus(@PathVariable Long taskid,@RequestParam String status){
        return taskService.updatestatus(taskid,status);
    }
}
