package com.academy.filters;

import com.academy.utils.JwtUtil;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

	private final JwtUtil jwtUtil;
	private final ReactiveUserDetailsService userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	public JwtAuthenticationFilter(ReactiveUserDetailsService userDetailsService, JwtUtil jwtUtil) {
		super((ReactiveAuthenticationManager) authentication -> Mono.just(authentication));
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		setServerAuthenticationConverter(this::convert);
	}

	private Mono<Authentication> convert(ServerWebExchange exchange) {
	    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        logger.info("Token: {}", token);

	        return jwtUtil.validateToken(token)
	                .filter(Boolean::booleanValue) // only continue if valid
	                .flatMap(valid -> jwtUtil.getUsernameFromToken(token))
	                .flatMap(username -> userDetailsService.findByUsername(username)
	                        .map(userDetails -> new UsernamePasswordAuthenticationToken(
	                                userDetails, null, userDetails.getAuthorities()
	                        ))
	                );
	    }

	    return Mono.empty();
	}

}
