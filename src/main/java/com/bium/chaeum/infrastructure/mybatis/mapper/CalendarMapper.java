package com.bium.chaeum.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bium.chaeum.infrastructure.mybatis.record.CalendarRecord;

@Mapper
public interface CalendarMapper {
	CalendarRecord selectByCalendarId(@Param("calendarId") String calendarId);
	CalendarRecord selectByUserId(@Param("userId") String userId);
	CalendarRecord selectByUserIdAndYearMonth(@Param("userId") String userId, @Param("yearMonth") String yearMonth);
	CalendarRecord selectWithMealCardsByUserIdAndYearMonth(@Param("userId") String userId, @Param("yearMonth") String yearMonth);
	
	int insert(CalendarRecord calendarRecord);
	int update(CalendarRecord calendarRecord);	
}
