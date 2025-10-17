package com.bium.chaeum.infrastructure.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bium.chaeum.infrastructure.mybatis.record.MealCardRecord;

@Mapper
public interface MealCardMapper {
	MealCardRecord selectByMealCardId(@Param("mealCardId") String mealCardId);
	List<MealCardRecord> selectByRecordDate(@Param("calendarId") String calendarId, @Param("recordDate") LocalDateTime recordDate);
	List<MealCardRecord> selectByCalendarId(@Param("calendarId") String calendarId);
	MealCardRecord selectByCalendarIdAndRecordDateAndDivision(@Param("calendarId") String calendarId, @Param("recordDate") LocalDateTime recordDate,  @Param("division") String division);
	
	int insert(MealCardRecord mealCard);
	int update(MealCardRecord mealCard);
	
	int delete(@Param("mealCardId") String mealCardId);
}
