package com.momentor.mentors.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MeetingRequestDto {
    @NotBlank(message = "Meeting Title Can't be Empty")
    private String title;

    @NotNull(message = "Meeting Date Is Required")
    @Future(message = "Meeting date Must Be In Future")
    private LocalDateTime meetingdate;

    // Removed: mentorid - it will be obtained from Authentication in the controller

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDateTime getMeetingdate() {
        return meetingdate;
    }
    public void setMeetingdate(LocalDateTime meetingdate) {
        this.meetingdate = meetingdate;
    }
}