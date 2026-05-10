package com.momentor.mentors.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskRequestDto {
    //Frontend Says
    @NotBlank(message = "Title Cannot Be Empty")
    private String title;
    @NotBlank(message = "AssignedTo Cannot Be Empty")
    private String assignedTo;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}