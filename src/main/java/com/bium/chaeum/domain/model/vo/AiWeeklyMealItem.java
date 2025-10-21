package com.bium.chaeum.domain.model.vo;

import java.util.List;

/**
 * author: 이상우
 */
import com.fasterxml.jackson.annotation.JsonProperty;

//AI 응답의 최상위 구조 (7일치 식단 목록 포함)
public record AiWeeklyMealItem(
		// Meal 객체의 리스트 (JSON 배열)
		@JsonProperty(required = true)
	    List<AiMealItem> meals,
	    
	    // AI가 추천 이유를 요약해 주는 필드
	    @JsonProperty(required = true)
	    String recommendationReason
) {}
