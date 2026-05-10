package com.momentor.mentors.DTO;

import java.util.List;

public class TeamDto {
    private String name;
    private List<String> participants;
    public TeamDto(String name, List<String> participants) {
        this.name = name;
        this.participants = participants;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
