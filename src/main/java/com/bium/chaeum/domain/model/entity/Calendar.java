package com.bium.chaeum.domain.model.entity;

import java.util.List;

import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.UserId;

import lombok.Getter;

@Getter
public class Calendar {
	private CalendarId id;
	private UserId userId;
	private String yearMonth;
	private List<MealCard> mealCards;
	
	private Calendar(CalendarId id, UserId userId, String yearMonth, List<MealCard> mealCards) {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null) throw new IllegalArgumentException("yearMonth is required");
        
        this.id = id;
        this.userId = userId;
        this.yearMonth = yearMonth;
        this.mealCards = mealCards;
	}
	
	public static Calendar create(UserId userId, String yearMonth, List<MealCard> mealCards) {
		return new Calendar(CalendarId.newId(), userId, yearMonth, mealCards);
	}
	
	public static Calendar reconstruct(CalendarId id, UserId userId, String yearMonth, List<MealCard> mealCards) {
		return new Calendar(id, userId, yearMonth, mealCards);
	}
}
