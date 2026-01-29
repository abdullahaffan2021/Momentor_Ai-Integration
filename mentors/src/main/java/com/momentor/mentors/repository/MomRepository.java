package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Moms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MomRepository extends JpaRepository<Moms,Long>{
    //Fetches the Meeting By Id
    Optional<Moms> findByMeetingId(Long meetingid);
}
