package com.mockly.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role", nullable = false)
    private String role; // CANDIDATE, INTERVIEWER, ADMIN

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "level")
    private String level;

    @Column(name = "skills", columnDefinition = "jsonb")
    private String skills; // Stored as JSON string representation for simplicity in this example
}
