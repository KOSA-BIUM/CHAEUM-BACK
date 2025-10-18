package com.bium.chaeum.infrastructure.mybatis.record;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProfileRecord {

    private String userId;
    private LocalDate birthDate;
    private String gender;
    private Integer height;
    private Integer weight;
    private String preferredDiet;
    private Long bmr;
}
