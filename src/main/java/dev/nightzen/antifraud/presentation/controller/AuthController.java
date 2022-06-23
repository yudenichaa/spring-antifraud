package dev.nightzen.antifraud.presentation.controller;

import dev.nightzen.antifraud.business.entity.User;
import dev.nightzen.antifraud.business.service.UserService;
import dev.nightzen.antifraud.presentation.dto.ChangeUserAccessRequestDto;
import dev.nightzen.antifraud.presentation.dto.ChangeUserRoleRequestDto;
import dev.nightzen.antifraud.presentation.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody @Valid User user) {
        User createdUser = userService.createUser(user);
        return new UserResponseDto(user);
    }

    @GetMapping("list")
    public List<UserResponseDto> usersList() {
        List<UserResponseDto> users = new ArrayList<>();
        userService.usersList().forEach(user -> users.add(new UserResponseDto(user)));
        return users;
    }

    @DeleteMapping("user/{username}")
    public Map<String, String> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return Map.of(
                "username", username,
                "status", "Deleted successfully!"
        );
    }

    @PutMapping("role")
    public UserResponseDto changeUserRole(@RequestBody @Valid ChangeUserRoleRequestDto changeUserRoleRequestDto) {
        User user = userService.changeRole(changeUserRoleRequestDto);
        return new UserResponseDto(user);
    }

    @PutMapping("access")
    public Map<String, String> changeUserAccess(
            @RequestBody @Valid ChangeUserAccessRequestDto changeUserAccessRequestDto) {
        boolean isLocked = userService.changeAccess(changeUserAccessRequestDto);
        return Map.of(
                "status",
                "User " +
                        changeUserAccessRequestDto.getUsername() +
                        " " + (isLocked ? "locked" : "unlocked") + "!");
    }
}
