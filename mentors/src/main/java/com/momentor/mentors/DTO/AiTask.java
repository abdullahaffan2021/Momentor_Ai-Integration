package com.momentor.mentors.DTO;

public class AiTask {

    private String title;
    private String assignedTo;
    private String Resources;
    private String deadline;
    private boolean urgent;
    // getters & setters
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
    public String getResources() {
        return Resources;
    }
    public void setResources(String resources) {
        Resources = resources;
    }
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public boolean isUrgent() {
        return urgent;
    }
    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
