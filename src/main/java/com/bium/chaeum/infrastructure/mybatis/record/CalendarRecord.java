package com.bium.chaeum.infrastructure.mybatis.record;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// CalendarRecord는 캘린더 데이터를 나타내는 MyBatis 레코드 클래스입니다. (author: 나규태)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRecord {
    private String calendarId; // 캘린더 고유 ID
    private String userId; // 사용자 ID
    private String yearMonth; // 연도-월
    private List<MealCardRecord> mealCards; // 식사 카드 목록
}
