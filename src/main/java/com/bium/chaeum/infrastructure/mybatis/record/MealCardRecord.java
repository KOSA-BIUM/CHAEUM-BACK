package com.bium.chaeum.infrastructure.mybatis.record;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// MealCardRecord는 식사 카드 데이터를 나타내는 MyBatis 레코드 클래스입니다. (author: 나규태)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealCardRecord {
    private String mealCardId;  // 식사 카드 고유 ID
    private String calendarId;   // 캘린더 ID
    private LocalDateTime recordDate; // 기록 날짜/시간
    private String division;    	// enum : BREAKFAST/LUNCH/DINNER/SNACK
    private List<MealItemRecord> mealItems; // 식사 항목 목록
}
