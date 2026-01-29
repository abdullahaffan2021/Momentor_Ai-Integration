package com.momentor.mentors.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {  //The Filter Runs For Ever HTTP Request,For Every API Call it runs.

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); //Fetches The Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) { //Token Exist where Format Is Correct
            String token = authHeader.substring(7); //Remove the beare string
            String email = jwtUtil.extractEmail(token); //Extract The Email
            String role=jwtUtil.extractRole(token); //Extract The Token
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) { //Check If User Already AUthenticated
                List<GrantedAuthority> authoroties =List.of(new SimpleGrantedAuthority("ROLE_"+role)); //Understand the Role
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authoroties); //Create Authentication Object for Username=email and Role=Authoroties.
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request) //Adds The Ip Address and Session Information.
                );
                SecurityContextHolder.getContext().setAuthentication(authentication); //User Is Authenticated For This Request
            }
        }
        filterChain.doFilter(request, response); //Pass Request To Next Controller.
    }

}
