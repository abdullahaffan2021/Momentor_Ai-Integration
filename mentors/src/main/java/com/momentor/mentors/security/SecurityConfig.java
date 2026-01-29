package com.momentor.mentors.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    JwtAuthFilter jwtAuthFilter;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf ->csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**","/api/users/**").permitAll()
                        .requestMatchers("/api/meetings/**").hasRole("MENTOR") //Access Only On Mentor
                        .requestMatchers("/api/tasks/user/**").hasRole("STUDENT")//Access only  On Student
                        .requestMatchers("/api/tasks").hasAnyRole("MENTOR","ADMIN") //Access Anyone By Mentor,Admin
                        .requestMatchers("/api/dashboard/summary/**").hasAnyRole("ADMIN","MENTOR")
                        .anyRequest().authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
