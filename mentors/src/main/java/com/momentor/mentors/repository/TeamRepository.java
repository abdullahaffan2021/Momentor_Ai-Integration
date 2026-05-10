package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t left join fetch t.participants where t.meeting.id = :meetingId")
    List<Team> findByMeetingIdWithParticipants(@Param("meetingId") Long meetingId);
}