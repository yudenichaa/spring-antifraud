package dev.nightzen.antifraud.presentation.dto;

import dev.nightzen.antifraud.constants.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserRoleRequestDto {

    @NotBlank
    private String username;

    @NotNull
    private UserRole role;
}
