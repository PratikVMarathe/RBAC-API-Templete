package com.academy.utils;

import com.academy.services.CustomReactiveUserDetailsService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final CustomReactiveUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtReactiveAuthenticationManager(CustomReactiveUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getCredentials().toString();

        return jwtUtil.validateToken(jwtToken)
                .filter(Boolean::booleanValue) // only continue if valid
                .flatMap(valid -> jwtUtil.getUsernameFromToken(jwtToken))
                .flatMap(username -> userDetailsService.findByUsername(username)
                        .map(userDetails -> new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        ))
                );
    }
}
