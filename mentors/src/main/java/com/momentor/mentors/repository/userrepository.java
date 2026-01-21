package com.momentor.mentors.repository;

import com.momentor.mentors.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userrepository extends JpaRepository<user,Long> {
}
