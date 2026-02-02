package com.momentor.mentors.Controller;

import com.momentor.mentors.DTO.DashBoardResponseDto;
import com.momentor.mentors.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class apiDashboard {
    @Autowired
    private TaskService taskService;
    //Return The Tasks Like Wise of Dashboard
    @GetMapping("/summary")
    public DashBoardResponseDto getdashboardsummary(){
        return taskService.getSummary();
    }

}
