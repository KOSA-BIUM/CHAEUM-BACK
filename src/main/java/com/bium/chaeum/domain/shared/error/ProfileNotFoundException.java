package com.bium.chaeum.domain.shared.error;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String userId) {
        super("Profile not found for userId=" + userId);
    }
}
