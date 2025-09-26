package com.academy.services;

import com.academy.models.Role;
import com.academy.models.User;
import com.academy.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    private static final String FIXED_ADMIN_USERNAME = "fixed_admin_username";
    // username: fixed_admin_username
    // password: AdminPass123

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<User> registerUser(User user) {
        // Remove admin role if present in registration
        if (user.getRoles() != null && user.getRoles().contains(Role.ADMIN)) {
            user.getRoles().remove(Role.ADMIN);
        }
        // Default role if none assigned
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of(Role.USERS));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false);
        return userRepository.save(user);
    }

    public Mono<User> createAdminUser(User user, String createdByUsername) {
        // Prevent creating user with fixed admin username here or allow only fixed admin to create itself?
        if (user.getUsername().equalsIgnoreCase(FIXED_ADMIN_USERNAME)) {
            return Mono.error(new RuntimeException("Cannot create or modify the fixed admin user via API"));
        }

        // Validate creator's admin status (you can also do this in controller/security)
        return userRepository.findByUsername(createdByUsername)
            .flatMap(creator -> {
                if (creator.getRoles().contains(Role.ADMIN)) {
                    user.setRoles(List.of(Role.ADMIN));
                    user.setActive(true);
                    user.setVerified(true);
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(user);
                } else {
                    return Mono.error(new RuntimeException("Only admins can create admin users"));
                }
            });
    }

    public Mono<User> updateUserRole(String username, List<Role> roles, boolean verified, String verifiedByAdmin) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (user.getUsername().equalsIgnoreCase(FIXED_ADMIN_USERNAME)) {
                        return Mono.error(new RuntimeException("Cannot modify fixed admin"));
                    }
                    user.setRoles(roles);
                    user.setVerified(verified);
                    user.setVerifiedBy(verifiedByAdmin);
                    return userRepository.save(user);
                });
    }

    public Mono<Void> deleteUser(String username) {
        if (username.equalsIgnoreCase(FIXED_ADMIN_USERNAME)) {
            return Mono.error(new RuntimeException("Cannot delete fixed admin"));
        }
        return userRepository.findByUsername(username)
                .flatMap(userRepository::delete);
    }

    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
