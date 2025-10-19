package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealItemRequest {
    private String mealCardId;   // 대상 MealCard ID (생성 시 옵션, 업데이트 시 필수일 수 있음)
    private String name;
    private String ingredient;   // 옵션
    private Integer carbohydrate;   // g, 옵션
    private Integer protein;        // g, 옵션
    private Integer fat;            // g, 옵션
    private Integer sodium;         // mg, 옵션
    private Integer calorie;        // kcal, 옵션
}
