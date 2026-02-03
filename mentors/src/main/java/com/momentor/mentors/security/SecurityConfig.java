package com.momentor.mentors.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Autowired
    JwtAuthFilter jwtAuthFilter;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // CORS Configuration for Spring Security
   @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of(
            "https://momentoraiintegration.netlify.app"
    ));

    config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));

    config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept"
    ));

    // IMPORTANT: must be FALSE when using Authorization header
    config.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ✅ AUTH & CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // ===== TASKS =====

                        // Dashboard
                        .requestMatchers(HttpMethod.GET, "/api/tasks/dashboard/summary/student")
                        .hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/dashboard/summary/mentor")
                        .hasAnyRole("MENTOR","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/dashboard/summary")
                        .hasRole("ADMIN")

                        // Student task list
                        .requestMatchers(HttpMethod.GET, "/api/tasks/user/**")
                        .hasRole("STUDENT")

                        // View meeting from task
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*/meeting")
                        .hasAnyRole("STUDENT","MENTOR","ADMIN")

                        // Task CRUD
                        .requestMatchers(HttpMethod.GET, "/api/tasks/**")
                        .hasAnyRole("STUDENT","MENTOR","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tasks/**")
                        .hasAnyRole("MENTOR","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/**")
                        .hasAnyRole("STUDENT","MENTOR","ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**")
                        .hasAnyRole("MENTOR","ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

