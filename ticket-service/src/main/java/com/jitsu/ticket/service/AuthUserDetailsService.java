package com.jitsu.ticket.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Simple hardcoded auth for demo - in production, call auth-service
        switch (username) {
            case "admin":
                return User.builder()
                    .username("admin")
                    .password("$2a$10$jIXD/G8nmfdH3dcZlZQ.GOxXZzseA6Hh8DlkeIdlSFcg/beQc0hc2") // admin123
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
            case "driver1":
            case "driver2": 
            case "driver3":
            case "driver4":
                return User.builder()
                    .username(username)
                    .password("$2a$10$C6puunboEerUnQR0SYDJj.PLCOlfxebkFTcwI7.D.20zwIEeNw5v2") // driver123
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_DRIVER")))
                    .build();
            default:
                throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}