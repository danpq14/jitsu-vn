package com.jitsu.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    
    /**
     * Get the current authenticated user's ID based on username
     * @return User ID or null if not found/authenticated
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        
        String username = auth.getName();
        
        // Map username to user ID based on hardcoded users
        switch (username) {
            case "admin": return 1L;
            case "driver1": return 2L; 
            case "driver2": return 3L;
            case "driver3": return 4L;
            case "driver4": return 5L;
            default: return null;
        }
    }
    
    /**
     * Get the current authenticated username
     * @return Username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
    
    /**
     * Check if current user has admin role
     * @return true if user is admin, false otherwise
     */
    public static boolean isCurrentUserAdmin() {
        return "admin".equals(getCurrentUsername());
    }
    
    /**
     * Check if current user is a driver
     * @return true if user is a driver, false otherwise
     */
    public static boolean isCurrentUserDriver() {
        String username = getCurrentUsername();
        return username != null && username.startsWith("driver");
    }
}