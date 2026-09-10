package com.projects.task_master.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.task_master.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = true, length = 200, columnDefinition = "TEXT")
    private String bio;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Column(nullable = false)
    private Roles role;
}
