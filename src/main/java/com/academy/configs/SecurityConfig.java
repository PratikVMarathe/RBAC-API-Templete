package com.academy.configs;

import com.academy.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
//@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final ReactiveUserDetailsService userDetailsService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Value("${server.unrestricted:/api/auth/register,/api/auth/login}")
	private String unrestrictedPaths;

	public SecurityConfig(ReactiveUserDetailsService userDetailsService,
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain() {
		ServerHttpSecurity http = ServerHttpSecurity.http();

		List<String> allowedPaths = Arrays.asList(unrestrictedPaths.split(","));

		ServerHttpSecurity.AuthorizeExchangeSpec exchanges = http.csrf().disable().authorizeExchange();

		for (String path : allowedPaths) {
			exchanges = exchanges.pathMatchers(path.trim()).permitAll();
		}

		return exchanges.anyExchange().authenticated().and()
				.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION).build();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager() {
		return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
