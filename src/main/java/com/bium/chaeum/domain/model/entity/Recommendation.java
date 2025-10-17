package com.bium.chaeum.domain.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.bium.chaeum.domain.model.vo.RecommendationId;
import com.bium.chaeum.domain.model.vo.UserId;

import lombok.Getter;

@Getter
public class Recommendation {

	private RecommendationId id;			// 식단추천 ID
	private UserId userId;					// 유저 ID
	private String requestPrompt;			// AI에게 보내는 사용자 프롬프트
	private String recommendationReason;	// 식단 추천 이유
	private LocalDateTime createdAt;		// 추천일자
	
	private List<RecommendationMealItem> mealItems;
	
	public Recommendation(RecommendationId id, UserId userId, String requestPrompt, String recommendationReason, LocalDateTime createdAt) {
		if (id == null) throw new IllegalArgumentException("id is required");
		if (userId == null) throw new IllegalArgumentException("userId is required");
		if (requestPrompt == null) throw new IllegalArgumentException("requestPrompt is required");
		if (recommendationReason == null) throw new IllegalArgumentException("recommendationReason is required");
		if (createdAt == null) throw new IllegalArgumentException("createdAt is required");
		
		this.id = id;
		this.userId = userId;
		this.requestPrompt = requestPrompt;
		this.recommendationReason = recommendationReason;
		this.createdAt = createdAt;
	}
	
	public static Recommendation create(UserId userId, String requestPrompt, String recommendationReason){
        return new Recommendation(RecommendationId.newId(), userId, requestPrompt, recommendationReason, LocalDateTime.now());
    }
	
	// 인프라 복원용(레코드 → 도메인).
	public static Recommendation reconstruct(RecommendationId id, UserId userId, String requestPrompt, String recommendationReason, LocalDateTime createdAt) {
		return new Recommendation(id, userId, requestPrompt, recommendationReason, createdAt);
	}
	
	public void setMealItems(List<RecommendationMealItem> mealItems) {
		this.mealItems = mealItems;
	}
	
}
