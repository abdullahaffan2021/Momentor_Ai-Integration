package com.momentor.mentors.DTO;

import java.util.List;

public class AiResult {

    private String mom;
    private List<AiTask> tasks;

    // getters & setters
    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }

    public List<AiTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<AiTask> tasks) {
        this.tasks = tasks;
    }
}
