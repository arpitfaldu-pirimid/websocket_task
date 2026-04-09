package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.UserEntity;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest request,
            HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );
        String accessToken =
                jwtUtil.generateAccessToken(request.getUsername());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(request.getUsername());

        ResponseCookie cookie = ResponseCookie.from(
                        "refreshToken",
                        refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(
                Map.of("accessToken", accessToken)
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(
            @CookieValue(name = "refreshToken") String token) {
        RefreshToken rt = refreshTokenService.verifyRefreshToken(token);
        String newAccessToken =
                jwtUtil.generateAccessToken(rt.getUsername());
        return Map.of("accessToken", newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken") String token) {
        refreshTokenService.revokeRefreshToken(token);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out");
    }
}
