package com.pencher.manager.security;

import com.pencher.manager.entity.enums.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Resolve current authenticated user ID and role from SecurityContext.
 */
public final class CurrentUser {

    private CurrentUser() {}

    public static Optional<Long> getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return Optional.empty();
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) return Optional.of((Long) principal);
        if (principal instanceof String) {
            try {
                return Optional.of(Long.parseLong((String) principal));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Long getIdOrThrow() {
        return getId().orElseThrow(() -> new IllegalStateException("Not authenticated"));
    }

    public static Optional<RoleType> getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return Optional.empty();
        return auth.getAuthorities().stream()
                .map(a -> {
                    String authority = a.getAuthority();
                    if (authority != null && authority.startsWith("ROLE_")) {
                        try {
                            return RoleType.valueOf(authority.substring(5));
                        } catch (Exception ignored) {}
                    }
                    return null;
                })
                .filter(r -> r != null)
                .findFirst();
    }

    public static boolean hasRole(RoleType role) {
        return getRole().map(r -> r == role).orElse(false);
    }

    public static boolean isSuperAdmin() {
        return hasRole(RoleType.SUPER_ADMIN);
    }
}
