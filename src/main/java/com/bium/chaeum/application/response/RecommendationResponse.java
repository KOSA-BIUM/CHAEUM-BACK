package com.bium.chaeum.application.response;

import java.util.List;

import com.bium.chaeum.domain.model.entity.Recommendation;
import com.bium.chaeum.domain.model.entity.RecommendationMealItem;
import com.bium.chaeum.domain.model.vo.AiMealItem;
import com.bium.chaeum.domain.model.vo.AiWeeklyMealItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RecommendationResponse {

	private String recommendationReason;
	private List<RecommendationMealItemResponse> meals;
	
	public static RecommendationResponse from(AiWeeklyMealItem mealPlan) {
		List<RecommendationMealItemResponse> mealResponses = mealPlan.meals().stream()
	            .map(RecommendationResponse::toRecommendationMealItemResponse)
	            .toList();
		
        return new RecommendationResponse(mealPlan.recommendationReason(), mealResponses);
    }
	
	public static RecommendationResponse from(Recommendation recommendation) {
	        
		List<RecommendationMealItemResponse> mealResponses = recommendation.getMealItems().stream()
		        .map(RecommendationMealItem::getAiMealItem) 
		        .map(RecommendationResponse::toRecommendationMealItemResponse) 
		        .toList();
		        
	    return new RecommendationResponse(recommendation.getRecommendationReason(), mealResponses);
    }
	
	private static RecommendationMealItemResponse toRecommendationMealItemResponse(AiMealItem item) {
        return new RecommendationMealItemResponse(
            item.dayNumber(),
            item.division().name(),
            item.foodName(),
            item.carbohydrate(),
            item.protein(),
            item.fat(),
            item.sodium(),
            item.calories()
        );
    }
}
