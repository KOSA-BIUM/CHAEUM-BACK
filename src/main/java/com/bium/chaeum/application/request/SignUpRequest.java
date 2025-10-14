package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String name;
}
