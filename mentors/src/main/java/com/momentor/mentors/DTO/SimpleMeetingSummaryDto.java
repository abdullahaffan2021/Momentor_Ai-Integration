package com.momentor.mentors.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class SimpleMeetingSummaryDto {
    private Long meetingId;
    private String title;
    private LocalDateTime meetingDate;
    private String meetingType;
    private String momText;
    private List<AudioFileDto> audioFiles;
    private List<TeamDto> teams;

    public SimpleMeetingSummaryDto(Long meetingId, String title, LocalDateTime meetingDate,
                                   String meetingType, String momText,
                                   List<AudioFileDto> audioFiles, List<TeamDto> teams) {
        this.meetingId = meetingId;
        this.title = title;
        this.meetingDate = meetingDate;
        this.meetingType = meetingType;
        this.momText = momText;
        this.audioFiles = audioFiles;
        this.teams = teams;
    }

    // Getters
    public Long getMeetingId() {
        return meetingId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getMeetingDate() {
        return meetingDate;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public String getMomText() {
        return momText;
    }

    public List<AudioFileDto> getAudioFiles() {
        return audioFiles;
    }

    public List<TeamDto> getTeams() {
        return teams;
    }
}