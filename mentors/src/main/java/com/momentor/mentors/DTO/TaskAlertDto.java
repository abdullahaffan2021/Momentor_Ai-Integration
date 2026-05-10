package com.momentor.mentors.DTO;

import java.time.LocalDateTime;

public class TaskAlertDto {

    private String title;
    private String priority;
    private LocalDateTime dueDate;
    private long hoursRemaining;
    private String alertType;

    public TaskAlertDto(String title, String priority,
                        LocalDateTime dueDate,
                        long hoursRemaining,
                        String alertType) {

        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.hoursRemaining = hoursRemaining;
        this.alertType = alertType;
    }

    public String getTitle() {
        return title;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public long getHoursRemaining() {
        return hoursRemaining;
    }

    public String getAlertType() {
        return alertType;
    }
}