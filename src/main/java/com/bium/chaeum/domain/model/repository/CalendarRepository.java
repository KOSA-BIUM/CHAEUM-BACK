package com.bium.chaeum.domain.model.repository;

import java.util.Optional;

import com.bium.chaeum.domain.model.entity.Calendar;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.UserId;

public interface CalendarRepository {
	Optional<Calendar> findByCalendarId(CalendarId id);	
	Optional<Calendar> findByUserId(UserId userId);
	Optional<Calendar> findByUserIdAndYearMonth(UserId userId, String yearMonth);
	
	// UPSERT = 새 UUID면 insert, 아니면 update
	void save(Calendar calendar);
}
