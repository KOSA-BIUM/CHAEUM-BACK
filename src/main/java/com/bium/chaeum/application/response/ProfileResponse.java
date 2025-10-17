package com.bium.chaeum.application.response;

import com.bium.chaeum.domain.model.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
public class ProfileResponse {

    private String userId;
    private LocalDate birthDate;
    private String gender;
    private Integer height;
    private Integer weight;
    private String preferredDiet;
    private Long bmr;

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(profile.getUserId().value(), profile.getBirthDate(),
                profile.getGender().name(), profile.getHeight(), profile.getWeight(),
                profile.getPreferredDiet().name(), profile.getBmr());
    }
}
