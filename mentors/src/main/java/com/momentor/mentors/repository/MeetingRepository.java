package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting,Long> {
}
