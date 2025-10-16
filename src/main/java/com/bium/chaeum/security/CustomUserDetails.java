package com.bium.chaeum.security;

import com.bium.chaeum.domain.model.vo.UserId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public final class CustomUserDetails extends User {

    private final UserId userId;

    public CustomUserDetails(UserId userId, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
