package com.academy.controllers;

import com.academy.models.Role;
import com.academy.models.User;
import com.academy.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Create a new admin user - Only Admin role can access this
    @PostMapping("/createAdmin")
    public Mono<User> createAdminUser(@RequestBody User user, @RequestHeader("X-Creator-Username") String creatorUsername) {
        return userService.createAdminUser(user, creatorUsername);
    }

    // Verify and update user roles (except admin cannot be assigned here)
    @PutMapping("/verifyUser/{username}")
    public Mono<User> verifyUserAndSetRoles(
            @PathVariable String username,
            @RequestBody List<Role> roles,
            @RequestHeader("X-Admin-Username") String adminUsername) {
        return userService.updateUserRole(username, roles, true, adminUsername);
    }

    // Delete user, admin protected and fixed admin cannot be deleted
    @DeleteMapping("/deleteUser/{username}")
    public Mono<Void> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    // Get user details by username
    @GetMapping("/user/{username}")
    public Mono<User> getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

}
