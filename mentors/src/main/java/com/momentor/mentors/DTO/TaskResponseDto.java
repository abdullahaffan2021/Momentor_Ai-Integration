package com.momentor.mentors.DTO;

public class TaskResponseDto {
    private Long id;
    private String title;
    private String assignedTo;
    private String status;

    public TaskResponseDto(Long id, String title, String assignedTo, String status) {
        this.id = id;
        this.title = title;
        this.assignedTo = assignedTo;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getStatus() {
        return status;
    }
}
