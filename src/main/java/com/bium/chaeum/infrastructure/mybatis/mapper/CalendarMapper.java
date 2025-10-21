package com.bium.chaeum.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bium.chaeum.infrastructure.mybatis.record.CalendarRecord;

// CalendarMapper는 캘린더 데이터에 대한 MyBatis 매퍼 인터페이스로, 데이터베이스와의 상호작용을 정의합니다. (author: 나규태)
@Mapper
public interface CalendarMapper {
	CalendarRecord selectByCalendarId(@Param("calendarId") String calendarId);
	CalendarRecord selectByUserId(@Param("userId") String userId);
	CalendarRecord selectByUserIdAndYearMonth(@Param("userId") String userId, @Param("yearMonth") String yearMonth);
	CalendarRecord selectWithMealCardsByUserIdAndYearMonth(@Param("userId") String userId, @Param("yearMonth") String yearMonth);
	
	int insert(CalendarRecord calendarRecord);
	int update(CalendarRecord calendarRecord);	
}
