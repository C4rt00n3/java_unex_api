package com.dende.eventos.dto;

import com.dende.eventos.entities.User;
import com.dende.eventos.entities.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(Long id, String name, LocalDate birthDate, String gender, String email, UserRole role, boolean active,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getBirthDate(),
                user.getGender(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
