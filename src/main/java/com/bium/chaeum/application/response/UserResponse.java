package com.bium.chaeum.application.response;

import com.bium.chaeum.domain.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserResponse {

    private String userId;
    private String email;
    private String name;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId().value(), user.getEmail(), user.getName());
    }
}
