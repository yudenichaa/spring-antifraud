package dev.nightzen.antifraud.security;

import dev.nightzen.antifraud.business.entity.User;
import dev.nightzen.antifraud.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found");
        }

        return new UserDetailsImpl(user.get());
    }
}
