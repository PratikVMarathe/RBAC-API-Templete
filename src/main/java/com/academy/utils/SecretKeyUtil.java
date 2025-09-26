package com.academy.utils;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretKeyUtil {

    // Convert SecretKey to Base64 String
    public static String serializeKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // Convert Base64 String back to SecretKey
    public static Mono<SecretKey> deserializeKey(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return Mono.just(new javax.crypto.spec.SecretKeySpec(decodedKey, "HmacSHA512"));
    }
}
