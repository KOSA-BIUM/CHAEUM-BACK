package com.bium.chaeum.infrastructure.mybatis.record;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRecord {
    private String calendarId;
    private String userId;
    private String yearMonth;    
    private List<MealCardRecord> mealCards;
}
