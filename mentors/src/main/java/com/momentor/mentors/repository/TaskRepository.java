package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Task;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    //fetch task assigned to user
    List<Task> findByAssignedTo(String assignedto);

    //Fetch Task By Status.
    List<Task> findByStatus(String status);

    //Fetches the MOM By MomId
    List<Task> findByMomId(Long momId);

    List<Task> findByMomIdIn(List<Long> momIds);

}