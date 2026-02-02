package com.momentor.mentors.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String assignedTo;
    private String status;
    private LocalDateTime createdat;
    @ManyToOne
    @JoinColumn(name="mom_id")
    private Moms  mom;
    public Task(){
        this.createdat=LocalDateTime.now();
        this.status="NOT_STARTED";
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignedto() {
        return assignedTo;
    }

    public void setAssignedto(String assignedto) {
        this.assignedTo = assignedto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public Moms getMom() {
        return mom;
    }

    public void setMom(Moms mom) {
        this.mom = mom;
    }
}
