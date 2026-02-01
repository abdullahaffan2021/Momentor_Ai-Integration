package com.momentor.mentors.Service;

import com.momentor.mentors.Exception.ResourceNotFoundException;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class MeetingService {
    @Autowired
    private  MeetingRepository meetingRepository;
    public Meeting createmeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }
    public Meeting getmeeting(Long id) {
            return meetingRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Meeting Not Found"));
    }
    public List<Meeting> getmeetingsbymentor(Long mentorid){
        return meetingRepository.findByMentorid(mentorid);
    }
}
