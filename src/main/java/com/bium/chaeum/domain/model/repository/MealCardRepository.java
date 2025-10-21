package com.bium.chaeum.domain.model.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.bium.chaeum.domain.model.entity.MealCard;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.MealCardId;

// MealCardRepository는 MealCard 엔티티에 대한 저장소 인터페이스로, 식사 기록 데이터를 조회하고 저장하는 기능을 제공합니다. (author: 나규태)
public interface MealCardRepository {
	Optional<MealCard> findByMealCardId(MealCardId id);
	List<MealCard> findListByRecordDate(CalendarId calendarId, LocalDateTime recordDate);
	List<MealCard> findListByCalendarId(CalendarId calendarId);
	Optional<MealCard> findByCalendarIdAndRecordDateAndDivision(CalendarId calendarId, LocalDateTime recordDate, String division);
	List<MealCard> findListByPeriod(LocalDateTime start, LocalDateTime end);
	
	// UPSERT = 새 UUID면 insert, 아니면 update
    void save(MealCard mealCard);
    void delete(MealCardId id);
}
