package com.academy.repositories;

import java.util.Collection;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.academy.models.User;
import com.academy.repositories.projections.UserProfilePictureUrlProjection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;



public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmail(String email);
    Mono<User> findByResetToken(String resetToken);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByEmailAndUsername(String email, String username);
    Mono<User> findByUsernameAndPassword(String username, String password);
    Mono<User> findByUsername(String username);
    Flux<String> findRolesByUsername(String username);
    Mono<Void> deleteByUsername(String username);
    Flux<User> findAllByUsernameIn(Collection<String> usernames);
    @Query("{ 'roles': ?0 }")
    Flux<User> findByRole(String role);
    Flux<User> findByRolesContains(String role);
    @Query("{ 'username': ?0 }")
    Mono<UserProfilePictureUrlProjection> findProfilePictureUrlByUsername(String username);
}
