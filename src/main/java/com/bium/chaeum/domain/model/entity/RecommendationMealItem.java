package com.bium.chaeum.domain.model.entity;

import com.bium.chaeum.domain.model.vo.AiMealItem;
import com.bium.chaeum.domain.model.vo.RecommendationId;
import com.bium.chaeum.domain.model.vo.RecommendationMealItemId;

import lombok.Getter;

/**
 * author: 이상우
 */
@Getter
public class RecommendationMealItem {
	
	private RecommendationMealItemId id;
	private RecommendationId recommendationId;
	private AiMealItem aiMealItem;
	
	public RecommendationMealItem(RecommendationMealItemId id, RecommendationId recommendationId, AiMealItem aiMealItem) {
		if (id == null) throw new IllegalArgumentException("id is required");
		if (recommendationId == null) throw new IllegalArgumentException("recommendationId is required");
		if (aiMealItem == null) throw new IllegalArgumentException("aiMealItem is required");
		
		this.id = id;
		this.recommendationId = recommendationId;
		this.aiMealItem = aiMealItem;
	}
	
	public static RecommendationMealItem create(RecommendationId recommendationId, AiMealItem aiMealItem){
        return new RecommendationMealItem(RecommendationMealItemId.newId(), recommendationId, aiMealItem);
    }
	
	// 인프라 복원용(레코드 → 도메인).
	public static RecommendationMealItem reconstruct(RecommendationMealItemId id, RecommendationId recommendationId, AiMealItem aiMealItem) {
		return new RecommendationMealItem(id, recommendationId, aiMealItem);
	}
}
