package com.bium.chaeum.infrastructure.mybatis.record;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// MealItemRecord는 식사 항목 데이터를 나타내는 MyBatis 레코드 클래스입니다. (author: 나규태)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealItemRecord {
    private String mealItemId;	// 식사 항목 고유 ID
    private String mealCardId;   // 식사 카드 ID
    private String name;         // 식사 항목 이름
    private String ingredient;  // 식사 항목 재료
	private Integer carbohydrate; // 탄수화물
	private Integer protein;      // 단백질
	private Integer fat;          // 지방
	private Integer sodium;      // 나트륨
	private Integer calorie;     // 칼로리
}
