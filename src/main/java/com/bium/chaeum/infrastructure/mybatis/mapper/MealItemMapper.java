package com.bium.chaeum.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bium.chaeum.infrastructure.mybatis.record.MealItemRecord;

@Mapper
public interface MealItemMapper {
	MealItemRecord selectByMealItemId(@Param("mealItemId") String mealItemId);
	List<MealItemRecord> selectByMealCardId(@Param("mealCardId") String mealCardId);
	
    int insert(MealItemRecord mealItemRecord);   // 새로 생성된 자산
    int update(MealItemRecord mealItemRecord);   // 기존 자산
    
    int delete(@Param("mealItemId") String mealItemId);    
}
