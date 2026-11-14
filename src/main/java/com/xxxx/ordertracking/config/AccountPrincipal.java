package com.xxxx.ordertracking.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public final class AccountPrincipal extends User {

    private final String accountId;

    public AccountPrincipal(
            String accountId,
            String username,
            String passwordHash,
            String roleCode,
            boolean enabled
    ) {
        super(
                username,
                passwordHash,
                enabled,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + roleCode))
        );
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }
}