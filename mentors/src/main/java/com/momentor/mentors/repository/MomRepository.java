package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MomRepository extends JpaRepository<Moms,Long>{
    //Fetches the Meeting By Id
    Optional<Moms> findByMeeting_Id(Long meetingid);
    Optional<Moms> findByMeeting(Meeting meeting);
    List<Moms> findAllByMeeting_Id(Long meetingId);
}