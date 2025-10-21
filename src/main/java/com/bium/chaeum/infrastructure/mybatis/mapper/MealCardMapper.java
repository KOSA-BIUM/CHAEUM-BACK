package com.bium.chaeum.infrastructure.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bium.chaeum.infrastructure.mybatis.record.MealCardRecord;

// MealCardMapper는 식사 카드 데이터에 대한 MyBatis 매퍼 인터페이스로, 데이터베이스와의 상호작용을 정의합니다. (author: 나규태)
@Mapper
public interface MealCardMapper {
	MealCardRecord selectByMealCardId(@Param("mealCardId") String mealCardId);
	List<MealCardRecord> selectByRecordDate(@Param("calendarId") String calendarId, @Param("recordDate") LocalDateTime recordDate);
	List<MealCardRecord> selectByCalendarId(@Param("calendarId") String calendarId);
	MealCardRecord selectByCalendarIdAndRecordDateAndDivision(@Param("calendarId") String calendarId, @Param("recordDate") LocalDateTime recordDate,  @Param("division") String division);
	List<MealCardRecord> selectByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	
	int insert(MealCardRecord mealCard);
	int update(MealCardRecord mealCard);
	
	int delete(@Param("mealCardId") String mealCardId);
}
