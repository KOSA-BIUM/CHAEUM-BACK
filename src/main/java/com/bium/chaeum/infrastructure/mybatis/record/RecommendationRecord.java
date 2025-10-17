package com.bium.chaeum.infrastructure.mybatis.record;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * author: 이상우
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRecord {

	private String recommendationId;		// 식단추천 ID
	private String userId;					// 유저 ID
	private String requestPrompt;			// AI에게 보내는 사용자 프롬프트
	private String recommendationReason;	// 식단 추천 이유
	private LocalDateTime createdAt;		// 추천 일자
	
	private List<RecommendationMealItemRecord> mealItems;
	
}
