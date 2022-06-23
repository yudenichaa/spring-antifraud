package dev.nightzen.antifraud.business.service;

import dev.nightzen.antifraud.business.entity.User;
import dev.nightzen.antifraud.persistance.UserRepository;
import dev.nightzen.antifraud.presentation.dto.ChangeUserAccessRequestDto;
import dev.nightzen.antifraud.presentation.dto.ChangeUserRoleRequestDto;
import dev.nightzen.antifraud.constants.AccessOperation;
import dev.nightzen.antifraud.constants.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        boolean isCreatingFirstUser = userRepository.count() == 0;
        UserRole role = isCreatingFirstUser ? UserRole.ADMINISTRATOR : UserRole.MERCHANT;
        user.setRole(role);
        user.setLocked(!isCreatingFirstUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Iterable<User> usersList() {
        return userRepository.findByOrderByIdAsc();
    }

    public void deleteUser(String username) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        userRepository.delete(user.get());
    }

    public User changeRole(ChangeUserRoleRequestDto changeUserRoleRequestDto) {
        String username = changeUserRoleRequestDto.getUsername();
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();
        UserRole newRole = changeUserRoleRequestDto.getRole();

        if (user.getRole() == newRole) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (!(newRole == UserRole.MERCHANT || newRole == UserRole.SUPPORT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setRole(changeUserRoleRequestDto.getRole());
        return userRepository.save(user);
    }

    public boolean changeAccess(ChangeUserAccessRequestDto changeUserAccessRequestDto) {
        String username = changeUserAccessRequestDto.getUsername();
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        if (user.getRole() == UserRole.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        boolean isLocked = changeUserAccessRequestDto.getOperation() == AccessOperation.LOCK;
        user.setLocked(isLocked);
        return userRepository.save(user).getLocked();
    }
}
