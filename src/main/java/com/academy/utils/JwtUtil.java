package com.academy.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.academy.exceptions.TokenNotAvailableException;
import com.academy.services.UserTokenService;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	@Autowired
	private UserTokenService userTokenService;

	private SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	@Value("${jwt.expiration:3600000}")
	private long jwtExpirationMs;

	// Generate JWT token
	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)).signWith(jwtSecret).compact();
	}

	// Extract any claim reactively
	public <T> Mono<T> getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		return getAllClaimsFromToken(token).map(claims -> claimsResolver.apply(claims));
	}

	// Extract username from token
	public Mono<String> getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// Extract expiration date from token
	public Mono<Date> getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// Parse claims reactively
	private Mono<Claims> getAllClaimsFromToken(String token) {
		return getSigningKey(token).map(secretKey -> {
			try {
				return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
			} catch (Exception e) {
				throw new TokenNotAvailableException("Invalid Token");
			}
		}).switchIfEmpty(Mono.error(new TokenNotAvailableException("Secret key unavailable")));
	}

	private Mono<SecretKey> getSigningKey(String token) {
		return userTokenService.findByToken(token)
				.flatMap(userTokenModel -> SecretKeyUtil.deserializeKey(userTokenModel.getSigningKey()))
				.onErrorResume(e -> {
					e.printStackTrace();
					return Mono.just(jwtSecret);
				}).switchIfEmpty(Mono.just(jwtSecret));
	}

	// Check if token is expired
	public Mono<Boolean> isTokenExpired(String token) {
		return getExpirationDateFromToken(token).map(expiration -> expiration.before(new Date())).defaultIfEmpty(true); // if
																														// no
																														// expiration,
																														// treat
																														// as
																														// expired
	}

	// Validate token against UserDetails reactively
	public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
		return getUsernameFromToken(token).zipWith(isTokenExpired(token)).map(tuple -> {
			String username = tuple.getT1();
			Boolean expired = tuple.getT2();
			return username.equals(userDetails.getUsername()) && !expired;
		});
	}

	// Validate token reactively
	public Mono<Boolean> validateToken(String token) {
		return getAllClaimsFromToken(token).flatMap(claims -> isTokenExpired(token).map(expired -> !expired))
				.onErrorResume(e -> Mono.just(false));
	}
}
