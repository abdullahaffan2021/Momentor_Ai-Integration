package com.momentor.mentors.DTO;

import java.time.LocalDateTime;

public class MeetingWithMomResponseDto {
    private Long id;
    private String title;
    private LocalDateTime meetingdate;
    private Long momId;
    private String momText;

    public MeetingWithMomResponseDto(Long id, String title, LocalDateTime meetingdate, Long momId, String momText) {
        this.id = id;
        this.title = title;
        this.meetingdate = meetingdate;
        this.momId = momId;
        this.momText = momText;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getMeetingdate() { return meetingdate; }
    public Long getMomId() { return momId; }
    public String getMomText() { return momText; }
}