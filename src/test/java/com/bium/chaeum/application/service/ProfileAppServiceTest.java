package com.bium.chaeum.application.service;

import com.bium.chaeum.application.request.RegisterProfileRequest;
import com.bium.chaeum.application.request.UpdateProfileRequest;
import com.bium.chaeum.application.response.ProfileResponse;
import com.bium.chaeum.domain.model.entity.GenderType;
import com.bium.chaeum.domain.model.entity.PreferredDietType;
import com.bium.chaeum.domain.model.entity.User;
import com.bium.chaeum.domain.model.repository.UserRepository;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.domain.shared.error.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ProfileAppServiceTest {

    @Autowired
    ProfileAppService profileAppService;

    @Autowired
    UserRepository userRepository;

    private User seedUser(String username) {
        User u = User.create(
                username,
                "{noop}pw",
                "Tester"
        );
        userRepository.save(u);
        return u;
    }

    private RegisterProfileRequest reg(String userId,
                                       LocalDate birthDate,
                                       GenderType gender,
                                       Integer height,
                                       Integer weight,
                                       PreferredDietType preferred) {
        RegisterProfileRequest r = new RegisterProfileRequest();
        r.setUserId(userId);
        r.setBirthDate(birthDate);
        r.setGender(gender.name());
        r.setHeight(height);
        r.setWeight(weight);
        r.setPreferredDiet(preferred.name());
        return r;
    }

    private UpdateProfileRequest upd(String userId,
                                     Integer height,
                                     Integer weight,
                                     PreferredDietType preferred) {
        UpdateProfileRequest r = new UpdateProfileRequest();
        r.setUserId(userId);
        r.setHeight(height);
        r.setWeight(weight);
        r.setPreferredDiet(preferred.name());
        return r;
    }

    @Test
    @DisplayName("register(): 새 프로필 등록 후 getProfile로 조회 가능")
    @Transactional
    void register_then_getProfile() {
        // given
        String username = "user-001@naver.com";
        User user = seedUser(username);
        String uid = user.getId().value();

        // when: 프로필 등록
        ProfileResponse created = profileAppService.register(
                reg(uid, LocalDate.of(1991, 5, 8),
                        GenderType.MALE, 180, 77,
                        PreferredDietType.PROTEIN)
        );

        // then
        assertThat(created.getUserId()).isEqualTo(uid);
        assertThat(created.getGender()).isEqualTo(GenderType.MALE.name());
        assertThat(created.getHeight()).isEqualTo(180);
        assertThat(created.getWeight()).isEqualByComparingTo(77);
        assertThat(created.getPreferredDiet()).isEqualTo(PreferredDietType.PROTEIN.name());

        // and: 조회 검증
        ProfileResponse found = profileAppService.getProfile(UserId.of(uid));
        assertThat(found.getUserId()).isEqualTo(uid);
        assertThat(found.getPreferredDiet()).isEqualTo(PreferredDietType.PROTEIN.name());
    }

    @Test
    @DisplayName("프로필 업데이트 반영")
    @Transactional
    void updateProfile() {
        // given
        String username = "user-001@naver.com";
        User user = seedUser(username);
        String uid = user.getId().value();

        profileAppService.register(
                reg(uid, LocalDate.of(1990, 1, 1),
                        GenderType.FEMALE, 162, 55,
                        PreferredDietType.LOW_CALORIE)
        );

        // when
        ProfileResponse updated = profileAppService.update(
                upd(uid, 163, 54, PreferredDietType.LOW_SUGAR)
        );

        // then
        assertThat(updated.getHeight()).isEqualTo(163);
        assertThat(updated.getWeight()).isEqualByComparingTo(54);
        assertThat(updated.getPreferredDiet()).isEqualTo(PreferredDietType.LOW_SUGAR.name());
    }

    @Test
    @DisplayName("getProfile(): 존재하지 않는 사용자면 DomainException")
    void registerProfile_without_user_shouldFail() {
        String ghostUser = "U-NOT-EXISTS";

        // FK 제약을 스키마에 뒀다면 DataIntegrityViolationException 기대
        assertThatThrownBy(() ->
                profileAppService.register(
                        reg(ghostUser, LocalDate.of(1995, 12, 1),
                                GenderType.MALE, 170, 60,
                                PreferredDietType.HEALTHY_AGING)
                )
        ).isInstanceOfAny(DataIntegrityViolationException.class, DomainException.class);
    }
}