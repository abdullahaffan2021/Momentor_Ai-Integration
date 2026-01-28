package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    //fetch task assigned to user
    List<Task> findByAssignedTo(String assignedto);
    //Fetch Task By Status.
    List<Task> findByStatus(String status);
}
