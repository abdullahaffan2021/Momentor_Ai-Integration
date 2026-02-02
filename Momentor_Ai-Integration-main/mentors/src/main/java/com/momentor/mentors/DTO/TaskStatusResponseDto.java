package com.momentor.mentors.DTO;

public class TaskStatusResponseDto {
    private Long id;
    private String title;
    private String status;

    public TaskStatusResponseDto(Long id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }
}
