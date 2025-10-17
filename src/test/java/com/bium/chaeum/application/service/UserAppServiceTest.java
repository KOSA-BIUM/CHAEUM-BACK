package com.bium.chaeum.application.service;

import com.bium.chaeum.application.request.LoginRequest;
import com.bium.chaeum.application.request.SignUpRequest;
import com.bium.chaeum.application.response.UserResponse;
import com.bium.chaeum.domain.model.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserAppServiceTest {

    @Autowired
    private UserAppService userAppService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("register → repository insert → authenticate OK (end-to-end)")
    void register_then_authenticate() {
        // given
        String email = "test@naver.com";
        String password = "1234";
        String name = "uitak";

        // when
        UserResponse userResponse = userAppService.register(new SignUpRequest(email, password, name));

        // then
        assertThat(userResponse.getUserId()).isNotBlank();
        assertThat(userRepository.existsByEmail(email)).isTrue();

        UserResponse auth = userAppService.authenticate(new LoginRequest(email, password));
        assertThat(auth).isNotNull();
        assertThat(auth.getEmail()).isEqualTo(email);
    }

}