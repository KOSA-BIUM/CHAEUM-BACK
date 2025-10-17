package com.bium.chaeum.infrastructure.mybatis.record;

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
public class RecommendationMealItemRecord {

	private String recommendationMealItemId;	// 식단음식 ID
	private String recommendationId;			// 식단추천 ID
	private int dayNumber;						// 일차 (1일차, 2일차, ...)
	private String division;					// 분류 (아침, 점심, 저녁, 간식)
	private String name;						// 음식이름
	private int carbohydrate;					// 탄수화물
	private int protein;						// 단백질
	private int fat;							// 지방
	private int sodium;							// 나트륨
	private int calorie;						// 칼로리
	
}
