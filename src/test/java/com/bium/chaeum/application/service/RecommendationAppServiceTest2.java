package com.bium.chaeum.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.response.RecommendationResponse;

@SpringBootTest
@Transactional
public class RecommendationAppServiceTest2 {

	@Autowired
    private RecommendationService recommendationService;

	@Test
    @DisplayName("최신 추천 기록 조회 시, Repository 결과를 응답 DTO로 변환하여 반환해야 한다.")
    void getLatestRecommendation_ShouldReturnLatestRecord() {
        // Given (준비):
		String userId = "840335f2-ff80-4afb-a219-968bfb050dea";

        // When (실행):
        RecommendationResponse result = recommendationService.getLatestRecommendation(userId);

        // Then (검증):
        assertNotNull(result, "조회 결과는 null이 아니어야 합니다.");
        System.out.println(result);
    }
}
