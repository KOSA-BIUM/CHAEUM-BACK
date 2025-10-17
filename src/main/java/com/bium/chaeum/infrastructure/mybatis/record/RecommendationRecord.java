package com.bium.chaeum.infrastructure.mybatis.record;

import lombok.Builder;
import lombok.Getter;

/**
 * author: 이상우
 */
@Getter
@Builder
public class RecommendationRecord {

	private String recommendationId;		// 식단추천 ID
	private String userId;					// 유저 ID
	private String requestPrompt;			// AI에게 보내는 사용자 프롬프트
	private String recommendationReason;	// 식단 추천 이유
	
}
