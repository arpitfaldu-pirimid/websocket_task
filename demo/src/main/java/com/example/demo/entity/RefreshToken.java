package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 2000)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    public RefreshToken() {}

    public RefreshToken(String username, String token, Instant expiresAt) {
        this.username = username;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getUsername() { return username; }
    public String getToken() { return token; }
    public Instant getExpiresAt() { return expiresAt; }
}
