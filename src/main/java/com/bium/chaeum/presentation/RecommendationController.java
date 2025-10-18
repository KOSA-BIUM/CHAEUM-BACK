package com.bium.chaeum.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * 프론트엔드에서 전송된 startDate, endDate를 @RequestBody로 받습니다.
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> recommendMeal(@AuthenticationPrincipal Jwt jwt) {
    	// 로그인한 유저 ID를 추출
    	String userId = (jwt != null) ? jwt.getClaim("user_id") : null;
    	
    	// 서비스 호출
    	RecommendationResponse response = recommendationService.executeRecommendation(
        		userId
//        		request.getStartDate(), // DTO에서 시작일 추출
//        		request.getEndDate()   // DTO에서 종료일 추출
        );
    	
        // HTTP 200 OK와 함께 Response DTO 반환
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/recommendations/latest
     * 인증된 사용자의 가장 최근 식단 추천 기록을 조회합니다.
     */
    @GetMapping("/latest")
    public ResponseEntity<RecommendationResponse> getLatestRecommendation(@AuthenticationPrincipal Jwt jwt) {
    	// 로그인한 유저 ID를 추출
    	String userId = (jwt != null) ? jwt.getClaim("user_id") : null;
        
        // 2. 서비스 호출
        RecommendationResponse response = recommendationService.getLatestRecommendation(userId);
        
        if (response == null) {
            // 204 No Content: 요청은 성공했으나 응답할 데이터(본문)가 없습니다.
            return ResponseEntity.noContent().build();
        }

        // 3. HTTP 200 OK와 함께 Response DTO 반환
        return ResponseEntity.ok(response);
    }
}
