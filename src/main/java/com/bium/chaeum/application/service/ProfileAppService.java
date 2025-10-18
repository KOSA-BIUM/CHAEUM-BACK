package com.bium.chaeum.application.service;

import com.bium.chaeum.application.request.RegisterProfileRequest;
import com.bium.chaeum.application.request.UpdateProfileRequest;
import com.bium.chaeum.application.response.ProfileResponse;
import com.bium.chaeum.domain.model.entity.GenderType;
import com.bium.chaeum.domain.model.entity.PreferredDietType;
import com.bium.chaeum.domain.model.entity.Profile;
import com.bium.chaeum.domain.model.repository.ProfileRepository;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.domain.shared.error.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileAppService {

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileResponse register(RegisterProfileRequest request) {
        if(request == null) throw new IllegalArgumentException("request is required");

        Profile profile = Profile.create(UserId.of(request.getUserId()), request.getBirthDate(),
                GenderType.valueOf(request.getGender()), request.getHeight(), request.getWeight(),
                PreferredDietType.valueOf(request.getPreferredDiet()));

        profileRepository.save(profile);
        return ProfileResponse.from(profile);
    }

    @Transactional
    public ProfileResponse update(UpdateProfileRequest request) {
        if(request == null) throw new IllegalArgumentException("request is required");

        Profile profile = profileRepository.findById(UserId.of(request.getUserId()))
                .orElseThrow(() -> new DomainException("user profile not found"));

        profile.updateProfile(request.getHeight(), request.getWeight(),
                PreferredDietType.valueOf(request.getPreferredDiet()));

        profileRepository.save(profile);
        return ProfileResponse.from(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(UserId userId) {
        return profileRepository.findById(userId).map(ProfileResponse::from)
                .orElseThrow(() -> new DomainException("user profile not found"));
    }
}
