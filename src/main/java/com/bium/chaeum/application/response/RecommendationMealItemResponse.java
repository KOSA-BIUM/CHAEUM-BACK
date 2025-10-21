package com.bium.chaeum.application.response;

/**
 * author: 이상우
 */
public record RecommendationMealItemResponse(
	int dayNumber,
	String division,
	String foodName,
	int carbohydrate,
	int protein,
	int fat,
	int sodium,
	int calorie
) {}
