package com.momentor.mentors.DTO;

import java.time.LocalDateTime;

public class MeetingResponseDto {
    private Long id;
    private String title;
    private LocalDateTime meetingdate;
    public MeetingResponseDto(Long id, String title, LocalDateTime meetingdate) {
        this.id = id;
        this.title = title;
        this.meetingdate = meetingdate;
    }
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public LocalDateTime getMeetingdate() {
        return meetingdate;
    }
}