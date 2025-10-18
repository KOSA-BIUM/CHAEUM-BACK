package com.bium.chaeum.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.response.RecommendationResponse;

@SpringBootTest
//@Transactional
public class RecommendationAppServiceTest {

	@Autowired
    private RecommendationService recommendationService;

    // 주의: 이 테스트는 실제 외부 API 호출이 발생합니다.
    @Test
    @DisplayName("실제 AI 통신이 정상적으로 이루어지고 RecommendationResponse 객체로 변환되어야 한다")
    void executeRecommendation_ShouldCallActualAiAndReturnObject() {
        // Given (준비):
    	String userId = "3f45b6c1-b813-42cf-ae28-4b02ad23cfb0";
    	String start = "2025-10-11";
    	String end = "2025-10-18";
        
        // When (실행):
        RecommendationResponse result = recommendationService.executeRecommendation(userId, start, end);

        // Then (검증):
        // 객체 변환 및 기본 구조가 정상적인지 확인
        assertNotNull(result, "실제 AI 호출 결과가 null이 아니어야 합니다.");
        System.out.println(result);
    }
}