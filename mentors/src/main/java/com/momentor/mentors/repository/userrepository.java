package com.momentor.mentors.repository;

import com.momentor.mentors.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userrepository extends JpaRepository<user,Long> {
    Optional<user> findByEmail(String email);
    Optional<user> findByNameIgnoreCase(String name);
}