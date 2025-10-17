package com.bium.chaeum.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bium.chaeum.infrastructure.mybatis.record.RecommendationRecord;

@Mapper
public interface RecommendationMapper {

	int insert(RecommendationRecord recommendationRecord);
}
