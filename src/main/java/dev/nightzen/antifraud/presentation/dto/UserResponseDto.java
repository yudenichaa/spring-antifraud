package dev.nightzen.antifraud.presentation.dto;

import dev.nightzen.antifraud.business.entity.User;
import dev.nightzen.antifraud.constants.UserRole;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String username;
    private final UserRole role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
