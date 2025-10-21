package com.bium.chaeum.domain.model.repository;

import java.util.Optional;

import com.bium.chaeum.domain.model.entity.Calendar;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.UserId;

// CalendarRepository는 Calendar 엔티티에 대한 저장소 인터페이스로, 캘린더 데이터를 조회하고 저장하는 기능을 제공합니다. (author: 나규태)
public interface CalendarRepository {
	Optional<Calendar> findByCalendarId(CalendarId id);	
	Optional<Calendar> findByUserId(UserId userId);
	Optional<Calendar> findByUserIdAndYearMonth(UserId userId, String yearMonth);
	
	// UPSERT = 새 UUID면 insert, 아니면 update
	void save(Calendar calendar);
}
