package com.momentor.mentors.DTO;

import java.time.LocalDateTime;

public class AudioFileDto {
    private Long audioId;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;

    public AudioFileDto(Long audioId, String fileName, String filePath, LocalDateTime uploadedAt) {
        this.audioId = audioId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    // Getters
    public Long getAudioId() { return audioId; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}