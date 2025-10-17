package com.bium.chaeum.application.response;

import java.util.List;
import java.util.stream.Collectors;

import com.bium.chaeum.domain.model.entity.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
