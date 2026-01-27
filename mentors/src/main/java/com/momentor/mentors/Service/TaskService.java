package com.momentor.mentors.Service;
import com.momentor.mentors.DTO.TaskResponseDto;
import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.MomRepository;
import com.momentor.mentors.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MomRepository momRepository;
    //create Task From MOM
    public Task createtask(String title, String assignedTo, Long momid){
        //Fetch Moms Entity From DB
        Moms mom=momRepository.findById(momid).orElseThrow(()->new ResourceNotFoundException("Mom Not Found"));
        //Create A Task
        Task task=new Task();
        task.setTitle(title);
        task.setAssignedto(assignedTo);
        //Set Moms Entity
        task.setMom(mom);
        return taskRepository.save(task);
    }
    //update Task Status
    public Task updatestatus(Long taskid,String status){
        Task task=taskRepository.findById(taskid).orElseThrow(()->new ResourceNotFoundException("Task Not Found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }
    //Get All Tasks
    public List<Task> getalltasks(){
        return taskRepository.findAll();
    }
    //Get The Taks By Specific User
    public List<TaskResponseDto> gettaskbyuserdto(String username){
        return taskRepository.findByAssignedTo(username).stream().map(task -> new TaskResponseDto(task.getId(),task.getTitle(),task.getStatus(),task.getAssignedto())).collect(Collectors.toList());
    }
    //Calculate Task Percentage
    public double calculatepercentage(){
        List<Task> taskstatus=taskRepository.findAll();
        if(taskstatus.isEmpty()){
            return 0;
        }
        Long completedtasks=taskstatus.stream()
                .filter(task -> "DONE".equals(task.getStatus()))
                .count();
        return (completedtasks*100.0)/taskstatus.size();
    }
    public List<TaskResponseDto> getalltaskdto(){
        return taskRepository.findAll()
                .stream()
                .map(task -> new TaskResponseDto(task.getId(),task.getTitle(),task.getAssignedto(),task.getStatus()))
                .collect(Collectors.toList());
    }
    //Add Pagination
    public Page<TaskResponseDto> gettaskwithpagination(int page,int size,String sortby,String direction){
        Sort sort=direction.equalsIgnoreCase("desc")
                ?Sort.by(sortby).descending()
                :Sort.by(sortby).ascending();
        Pageable pageable= PageRequest.of(page,size,sort);
        return taskRepository.findAll(pageable).map(task -> new TaskResponseDto(task.getId(),task.getTitle(),task.getAssignedto(),task.getStatus()));
    }
}
