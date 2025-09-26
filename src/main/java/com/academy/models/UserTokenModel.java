package com.academy.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("tokenModel")
public class UserTokenModel {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String signingKey;
    private String token;
}
