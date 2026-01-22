package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken createRefreshToken(String username) {
        repository.deleteByUsername(username);
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken =
                new RefreshToken(
                        username,
                        token,
                        Instant.now().plusSeconds(7 * 24 * 60 * 60)
                );
        return repository.save(refreshToken);
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        repository.delete(rt);
    }


    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));
        if (rt.getExpiresAt().isBefore(Instant.now())) {
            repository.delete(rt);
            throw new RuntimeException("Refresh token expired");
        }
        return rt;
    }
}
