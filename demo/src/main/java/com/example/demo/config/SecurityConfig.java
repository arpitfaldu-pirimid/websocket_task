package com.example.demo.config;

import com.example.demo.jwt.JwtFilter;
import com.example.demo.service.CustomOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuthUserService oAuthUserService;
    private final OAuth2SuccessHandler successHandler;
    private final JwtFilter jwtFilter;

    public SecurityConfig(
            CustomOAuthUserService oAuthUserService,
            OAuth2SuccessHandler successHandler,
            JwtFilter jwtFilter) {
        this.oAuthUserService = oAuthUserService;
        this.successHandler = successHandler;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/oauth2/**",
                                "/login/**",
                                "/index.html",
                                "/error"
                        ).permitAll()

                        .requestMatchers("/prices/**").authenticated()
                        .requestMatchers("/app/**").authenticated()
                        .requestMatchers("/topic/**").authenticated()
                        .requestMatchers("/api/hello").authenticated()
                        .anyRequest().authenticated()
                )


                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userinfo ->
                                userinfo.userService(oAuthUserService)
                        )
                        .successHandler(successHandler)
                )

                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
