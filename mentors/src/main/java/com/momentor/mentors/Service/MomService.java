package com.momentor.mentors.Service;

import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.repository.MeetingRepository;
import com.momentor.mentors.repository.MomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class MomService {
    @Autowired
    private MomRepository momRepository;
    @Autowired
    private MeetingRepository meetingRepository;
    public Moms savemom(String momText, Meeting m) {
        Moms mom = new Moms();
        mom.setMomtext(momText);
        mom.setMeeting(m);
        return momRepository.save(mom);
    }
    //Save Mom For Ai Flow
    public Moms save(String momText, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new RuntimeException("Meeting not found"));
        Moms mom = new Moms();
        mom.setMomtext(momText);
        mom.setMeeting(meeting);
        return momRepository.save(mom);
    }

    public Moms getMomByMeeting(Meeting meeting) {
        return momRepository.findByMeeting(meeting)
                .orElseThrow(() -> new RuntimeException("Mom Not Found For This Meeting"));
    }

    // Return null instead of throwing exception
    public Moms getMomByMeetingOrNull(Meeting meeting) {
        return momRepository.findByMeeting(meeting).orElse(null);
    }}
