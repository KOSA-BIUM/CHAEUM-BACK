package com.bium.chaeum.application.response;

import com.bium.chaeum.domain.model.entity.MealItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// MealItemResponse는 특정 식사 항목(MealItem)의 세부 정보를 응답으로 반환할 때 사용됩니다. (author: 나규태)
@Builder
@Data
@AllArgsConstructor
public class MealItemResponse {
    private String mealItemId;
    private String mealCardId;
    private String name;
    private String ingredient;
    private Integer carbohydrate;
    private Integer protein;
    private Integer fat;
    private Integer sodium;
    private Integer calorie;

    public static MealItemResponse from(MealItem item) {
        if (item == null) return null;
        return new MealItemResponse(item.getId().value(), item.getMealCardId().value(), item.getName(), item.getIngredient(), item.getCarbohydrate(), item.getProtein(), item.getFat(), item.getSodium(), item.getCalorie());
    }
}
