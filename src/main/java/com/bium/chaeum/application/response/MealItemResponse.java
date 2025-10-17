package com.bium.chaeum.application.response;

import com.bium.chaeum.domain.model.entity.MealItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
