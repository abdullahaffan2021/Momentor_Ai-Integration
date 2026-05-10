package com.momentor.mentors.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name="meeting_id")
    private Meeting meeting;
    @ElementCollection //same meeting id can store different user
    @CollectionTable(name = "team_participants", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "participant_name")
    private List<String> participants;
    public Team(){}
    public Team(Long id, String name, Meeting meeting, List<String> participants) {
        this.id = id;
        this.name = name;
        this.meeting = meeting;
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
