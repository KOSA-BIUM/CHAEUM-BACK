package com.bium.chaeum.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bium.chaeum.infrastructure.mybatis.record.RecommendationMealItemRecord;

/**
 * author: 이상우
 */
@Mapper
public interface RecommendationMealItemMapper {

	/**
	 * 사용자의 식단 추천 기록과 모든 상세 식단 항목을 등록합니다.
	 * @param recommendationMealItemRecord 추천 식단 음식 (RecommendationMealItemRecord 타입)
	 * @return int 등록된 행개수
	 */
	int insert(RecommendationMealItemRecord recommendationMealItemRecord);
}
