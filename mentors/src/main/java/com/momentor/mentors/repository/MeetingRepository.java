package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting,Long> {
    List<Meeting> findByMentorid(Long mentorId);


}