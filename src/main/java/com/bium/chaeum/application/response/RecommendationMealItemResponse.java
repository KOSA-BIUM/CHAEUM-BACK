package com.bium.chaeum.application.response;

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
