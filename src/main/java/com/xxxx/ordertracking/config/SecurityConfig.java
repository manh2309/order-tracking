package com.xxxx.ordertracking.config;

import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.entity.Account;
import com.xxxx.ordertracking.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/api/csrf").authenticated()
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/orders").hasRole("CUSTOMER")
                                .requestMatchers("/api/v1/cart-item/**", "/api/v1/checkout/**").hasRole("CUSTOMER")
                                .anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AccountRepository accounts) {
        return username -> {
            Account account = accounts.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND.getMessage()));

            return new AccountPrincipal(
                    account.getId(),
                    account.getUsername(),
                    account.getPassword(),
                    account.getRole().getCode(),
                    account.getDeleted() == 0 && account.getStatus() == 1
            );
        };
    }
}
