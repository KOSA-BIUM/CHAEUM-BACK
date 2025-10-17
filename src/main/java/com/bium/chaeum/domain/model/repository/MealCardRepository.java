package com.bium.chaeum.domain.model.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.bium.chaeum.domain.model.entity.MealCard;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.MealCardId;

public interface MealCardRepository {
	Optional<MealCard> findByMealCardId(MealCardId id);
	List<MealCard> findListByRecordDate(CalendarId calendarId, LocalDateTime recordDate);
	List<MealCard> findListByCalendarId(CalendarId calendarId);
	Optional<MealCard> findByCalendarIdAndRecordDateAndDivision(CalendarId calendarId, LocalDateTime recordDate, String division);
	
	// UPSERT = 새 UUID면 insert, 아니면 update
    void save(MealCard mealCard);
    void delete(MealCardId id);
}
