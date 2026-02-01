package com.momentor.mentors.DTO;

public class MomResponseDto {
    private Long id;
    private String momtext;
    public MomResponseDto(Long id, String momtext) {
        this.id = id;
        this.momtext = momtext;
    }
    public Long getId() {
        return id;
    }

    public String getMomtext() {
        return momtext;
    }
}
