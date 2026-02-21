package com.momentor.mentors.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime meetingdate;
    private Long mentorid;
    private LocalDateTime createdat;
    @OneToMany(
            mappedBy = "meeting",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MeetingAudio> meetingAudios;
    public Meeting(){
        this.createdat=LocalDateTime.now();
    }

    public Meeting(Long id, String title, LocalDateTime meetingdate, Long mentorid, LocalDateTime createdat) {
        this.id = id;
        this.title = title;
        this.meetingdate = meetingdate;
        this.mentorid = mentorid;
        this.createdat = LocalDateTime.now();
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

    public LocalDateTime getMeetingdate() {
        return meetingdate;
    }

    public void setMeetingdate(LocalDateTime meetingdate) {
        this.meetingdate = meetingdate;
    }

    public Long getMentorid() {
        return mentorid;
    }

    public void setMentorid(Long mentorid) {
        this.mentorid = mentorid;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }
}
