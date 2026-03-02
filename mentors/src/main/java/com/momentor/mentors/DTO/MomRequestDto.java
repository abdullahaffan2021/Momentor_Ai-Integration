package com.momentor.mentors.DTO;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.NotFound;

public class MomRequestDto {
    @NotBlank(message = "Mom Text Can't Be Empty")
    private String momtext;
    public String getMomtext() {
        return momtext;
    }
    public void setMomtext(String momtext) {
        this.momtext = momtext;
    }

}
