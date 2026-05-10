package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting,Long> {

    @Query("SELECT DISTINCT m FROM Meeting m " +
            "LEFT JOIN FETCH m.meetingAudios " +
            "WHERE m.mentorid = :mentorId")
    List<Meeting> findByMentorIdWithDetails(@Param("mentorId") Long mentorId);
    List<Meeting> findByMentorid(Long mentorId);
}