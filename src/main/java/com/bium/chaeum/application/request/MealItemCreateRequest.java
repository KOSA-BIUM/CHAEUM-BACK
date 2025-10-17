package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MealItemCreateRequest {
    private String mealItemId;   // null이면 새로 생성, 값이 있으면 업데이트
    private String name;        // 필수
    private String ingredient;  // 옵션
    private Integer carbohydrate;  // g, 옵션
    private Integer protein;       // g, 옵션
    private Integer fat;           // g, 옵션
    private Integer sodium;        // mg, 옵션
    private Integer calorie;       // kcal, 옵션
}
