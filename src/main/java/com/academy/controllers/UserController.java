package com.academy.controllers;

import com.academy.models.AuthenticationResponse;
import com.academy.models.Role;
import com.academy.models.User;
import com.academy.services.UserService;
import com.academy.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // Registration endpoint - no admin role allowed
    @PostMapping("/register")
    public Mono<ResponseEntity<User>> registerUser(@RequestBody User user) {
        
        return userService.registerUser(user)
                .map(savedUser -> ResponseEntity.ok(savedUser))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
    public UserDetails toUserDetails(com.academy.models.User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                    .map(role -> "ROLE_" + role.name())
                    .collect(Collectors.toList())
                    .toArray(new String[0]))
                .build();
    }

    // Login endpoint
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthenticationResponse>> loginUser(@RequestBody User loginRequest) {
        return userService.getUserByUsername(loginRequest.getUsername())
            .flatMap(user -> {
                boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
                if (matches) {
                    UserDetails userDetails = toUserDetails(user);
                    String token = jwtUtil.generateToken(userDetails);
                    return Mono.just(new AuthenticationResponse(token, user.getUsername(),user.getRoles(), "Login Successful"));
                } else {
                    return Mono.just(new AuthenticationResponse(null, null,null, "Incorrect Password"));
                }
            })
            .switchIfEmpty(Mono.just(new AuthenticationResponse(null, null, null, "Couldn't find your Account")))
            .map(response -> ResponseEntity.ok(response))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
						.body(new AuthenticationResponse(null, null, null, "Login failed: " + e.getMessage()))));
    }


}
