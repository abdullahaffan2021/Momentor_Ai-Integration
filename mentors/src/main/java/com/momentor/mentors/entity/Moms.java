package com.momentor.mentors.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="mom")
public class Moms {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String momtext;
    @OneToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
    private LocalDateTime updatedat;
    @OneToMany(
            mappedBy = "mom",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Task> tasks;

    public Moms(){
        this.updatedat=LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMomtext() {
        return momtext;
    }

    public void setMomtext(String momtext) {
        this.momtext = momtext;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public LocalDateTime getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(LocalDateTime updatedat) {
        this.updatedat = updatedat;
    }
}
