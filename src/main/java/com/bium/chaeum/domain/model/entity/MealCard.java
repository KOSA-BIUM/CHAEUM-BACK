package com.bium.chaeum.domain.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.MealCardId;

import lombok.Getter;

// MealCard 엔티티는 특정 날짜와 식사 구분에 해당하는 식사 기록을 나타냅니다. (author: 나규태)
@Getter
public class MealCard {
	private MealCardId id;
	private CalendarId calendarId;
	private LocalDateTime recordDate;
	private MealCardType division;
	private List<MealItem> mealItems;
	
    private MealCard(MealCardId id, CalendarId calendarId, LocalDateTime recordDate, MealCardType division, List<MealItem> mealItems) {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        if (division == null) throw new IllegalArgumentException("division is required");

        this.id = id;
        this.calendarId = calendarId;
        this.recordDate = recordDate;
        this.division = division;
        this.mealItems = mealItems;
    }
    
    public static MealCard create(CalendarId calendarId, LocalDateTime recordDate, MealCardType division, List<MealItem> mealItems){
        return new MealCard(MealCardId.newId(), calendarId, recordDate, division, mealItems);
    }

    // 인프라 복원용(레코드 → 도메인).
    public static MealCard reconstruct(MealCardId id, CalendarId calendarId, LocalDateTime recordDate, MealCardType division, List<MealItem> mealItems) {
        return new MealCard(id, calendarId, recordDate, division, mealItems);
    }
}
