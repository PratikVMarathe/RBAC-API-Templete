package com.academy.repositories;

import com.academy.models.UserTokenModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserTokenModelRepository extends ReactiveMongoRepository<UserTokenModel,String> {
    Mono<UserTokenModel> findByUsername(String username);
    Mono<UserTokenModel> findByToken(String token);
    Mono<Void> deleteByUsername(String username);
}
