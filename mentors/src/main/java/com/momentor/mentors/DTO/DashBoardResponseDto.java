package com.momentor.mentors.DTO;
public class DashBoardResponseDto {
    private Long totalTasks;
    private Long DoneTasks;
    private Long inProgressTasks;
    private Long notStartedTasks;
    private double overallprogress;

    public DashBoardResponseDto(Long totalTasks, Long doneTasks, Long inProgressTasks, Long notStartedTasks, double overallprogress) {
        this.totalTasks = totalTasks;
        DoneTasks = doneTasks;
        this.inProgressTasks = inProgressTasks;
        this.notStartedTasks = notStartedTasks;
        this.overallprogress = overallprogress;
    }

    public Long getTotalTasks() {
        return totalTasks;
    }

    public Long getDoneTasks() {
        return DoneTasks;
    }

    public Long getInProgressTasks() {
        return inProgressTasks;
    }

    public Long getNotStartedTasks() {
        return notStartedTasks;
    }

    public double getOverallprogress() {
        return overallprogress;
    }
}