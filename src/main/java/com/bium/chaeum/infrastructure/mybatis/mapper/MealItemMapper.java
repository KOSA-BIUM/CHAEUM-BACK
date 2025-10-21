package com.bium.chaeum.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bium.chaeum.infrastructure.mybatis.record.MealItemRecord;

// MealItemMapper는 식사 항목 데이터에 대한 MyBatis 매퍼 인터페이스로, 데이터베이스와의 상호작용을 정의합니다. (author: 나규태)
@Mapper
public interface MealItemMapper {
	MealItemRecord selectByMealItemId(@Param("mealItemId") String mealItemId);
	List<MealItemRecord> selectByMealCardId(@Param("mealCardId") String mealCardId);
	
    int insert(MealItemRecord mealItemRecord);   // 새로 생성된 자산
    int update(MealItemRecord mealItemRecord);   // 기존 자산
    
    int delete(@Param("mealItemId") String mealItemId);    
}
