package com.momentor.mentors.repository;

import com.momentor.mentors.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
