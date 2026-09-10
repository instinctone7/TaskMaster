package com.projects.task_master.repositories;

import com.projects.task_master.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findUserByEmail(String email);
}