package com.bium.chaeum.infrastructure.mybatis.record;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRecord {

    private String userId;
    private String email;
    private String password;
    private String name;
}
