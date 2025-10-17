package com.bium.chaeum.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bium.chaeum.application.response.RecommendationResponse;
import com.bium.chaeum.application.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

	private final RecommendationService recommendationService;
	
	/**
     * POST /api/recommendations
     * AI 식단 추천을 실행하고, 추천된 식단 정보를 반환합니다.
     * (현재는 인증된 사용자 ID가 없으므로 서비스 메서드에 하드코딩된 유저를 사용합니다.)
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> recommendMeal() {
        // 1. Application Service 호출
        // (원래는 @AuthenticationPrincipal 등을 통해 userId를 가져와 서비스에 전달해야 합니다.)
        RecommendationResponse response = recommendationService.executeRecommendation();

        // 2. HTTP 200 OK와 함께 Response DTO 반환
        return ResponseEntity.ok(response);
    }
}
