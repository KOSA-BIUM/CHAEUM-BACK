package com.bium.chaeum.infrastructure.mybatis.record;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealItemRecord {
    private String mealItemId;
    private String mealCardId;
    private String name;
    private String ingredient;
	private Integer carbohydrate;
	private Integer protein;
	private Integer fat;
	private Integer sodium;
	private Integer calorie;
}
