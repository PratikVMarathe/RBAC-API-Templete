package com.academy.services;

import com.academy.models.UserTokenModel;
import com.academy.repositories.UserTokenModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserTokenService {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private UserTokenModelRepository userTokenRepository;

    // Upsert token by username
    public Mono<UserTokenModel> upsertTokenByUsername(String username, String token, String signingKey) {
        Query query = new Query(Criteria.where("username").is(username));
        Update update = new Update().set("token", token).set("signingKey",signingKey);

        return reactiveMongoTemplate
                .findAndModify(query, update, UserTokenModel.class)
                .switchIfEmpty(Mono.defer(() -> {
                    UserTokenModel newToken = new UserTokenModel();
                    newToken.setUsername(username);
                    newToken.setToken(token);
                    newToken.setSigningKey(signingKey);
                    return userTokenRepository.save(newToken);
                }));
    }

    // Retrieve token by username
    public Mono<UserTokenModel> findByUsername(String username) {
        return userTokenRepository.findByUsername(username);
    }

    public Mono<UserTokenModel> findByToken(String token){
        return userTokenRepository.findByToken(token);
    }

    // Delete token by username
    public Mono<Void> deleteByUsername(String username) {
        return userTokenRepository.deleteByUsername(username);
    }
}

