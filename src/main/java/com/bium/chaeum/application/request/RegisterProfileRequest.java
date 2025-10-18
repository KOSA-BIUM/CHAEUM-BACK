package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterProfileRequest {

//    private String userId;
    private LocalDate birthDate;
    private String gender;
    private Integer height;
    private Integer weight;
    private String preferredDiet;
}
