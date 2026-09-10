package com.projects.task_master.repositories;

import com.projects.task_master.entities.BlackListJwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JwtTokenRepository extends JpaRepository<BlackListJwt,String> {
    boolean existsByJti(String jti);
    BlackListJwt findByJti(String jti);
}
