package com.bium.chaeum.application.response;

import java.util.List;
import java.util.stream.Collectors;

import com.bium.chaeum.domain.model.entity.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// CalendarResponse는 특정 사용자의 특정 연월에 해당하는 캘린더와 그 안의 식사 기록들을 응답으로 반환할 때 사용됩니다. (author: 나규태)
@Builder
@Data
@AllArgsConstructor
public class CalendarResponse {
    private String calendarId;
    private String userId;
    private String yearMonth;
    private List<MealCardResponse> mealCards;

    public static CalendarResponse from(Calendar calendar) {
    	if (calendar == null) return null;
    	return new CalendarResponse(
    		calendar.getId().value(),
    		calendar.getUserId().value(),
    		calendar.getYearMonth(),
    		calendar.getMealCards() == null ? List.of() : calendar.getMealCards().stream().map(MealCardResponse::from).collect(Collectors.toList())
    	);
    }
}
