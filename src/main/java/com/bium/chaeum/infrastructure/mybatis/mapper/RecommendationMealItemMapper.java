package com.bium.chaeum.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bium.chaeum.infrastructure.mybatis.record.RecommendationMealItemRecord;

@Mapper
public interface RecommendationMealItemMapper {

	int insert(RecommendationMealItemRecord recommendationMealItemRecord);
}
