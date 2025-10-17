package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.model.entity.MealCardType;
import com.fasterxml.jackson.annotation.JsonProperty;

//AI가 응답할 식단의 상세 구조
public record AiMealItem(
		// required = true를 명시하여 JSON 스키마에 이 필드가 필수로 포함되도록 강제
		// 일차 (1일차, 2일차, ...)
		@JsonProperty(required = true) int dayNumber,
		
		// 분류 (아침, 점심, 저녁, 간식)
		@JsonProperty(required = true) MealCardType division,

		// 음식이름
		@JsonProperty(required = true) String foodName,

		// 영양소는 정수(integer)로 받도록 유도 (double/float 대신)
		// 탄수화물
		@JsonProperty(required = true) int carbohydrate,

		// 단백질
		@JsonProperty(required = true) int protein,

		// 지방
		@JsonProperty(required = true) int fat,

		// 나트륨
		@JsonProperty(required = true) int sodium,

		// 칼로리
		@JsonProperty(required = true) int calories
) {
	public static AiMealItem reconstruct(int dayNumber, String division, String name, int carbohydrate, int protein,
			int fat, int sodium, int calorie) {
		MealCardType mealCardType = MealCardType.valueOf(division);
		return new AiMealItem(dayNumber, mealCardType, name, carbohydrate, protein, fat, sodium, calorie);
	}}
