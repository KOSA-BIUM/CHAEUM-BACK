package com.bium.chaeum.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.response.RecommendationResponse;

@SpringBootTest
@Transactional
public class RecommendationAppServiceTest {

	@Autowired
    private RecommendationService recommendationService;

    // 주의: 이 테스트는 실제 외부 API 호출이 발생합니다.
    @Test
    @DisplayName("실제 AI 통신이 정상적으로 이루어지고 WeeklyMeal 객체로 변환되어야 한다")
    void executeRecommendation_ShouldCallActualAiAndReturnObject() {
        // Given (준비):
        
        // When (실행):
        RecommendationResponse result = recommendationService.executeRecommendation();

        // Then (검증):
        // 객체 변환 및 기본 구조가 정상적인지 확인
        assertNotNull(result, "실제 AI 호출 결과가 null이 아니어야 합니다.");
        System.out.println(result);
    }
    
    @TestConfiguration
    static class SecurityTestConfig {
        @Bean
        PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    }
}
