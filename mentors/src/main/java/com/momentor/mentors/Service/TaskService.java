package com.momentor.mentors.Service;

import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    //create Task From MOM
    public Task createtask(String title, String assignedto, Moms mom){
        Task task=new Task();
        task.setTitle(title);
        task.setAssignedto(assignedto);
        task.setMom(mom);
        return taskRepository.save(task);
    }
    //update Task Status
    public Task updatestatus(Long taskid,String status){
        Task task=taskRepository.findById(taskid).orElseThrow(()->new RuntimeException("Task Not Found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }
}
