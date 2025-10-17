package com.bium.chaeum.infrastructure.mybatis.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.bium.chaeum.infrastructure.mybatis.record.RecommendationRecord;

@Mapper
public interface RecommendationMapper {

	int insert(RecommendationRecord recommendationRecord);
	
	/**
	 * 사용자의 가장 최근 식단 추천 기록과 모든 상세 식단 항목을 조회합니다.
	 * @param userId 조회할 사용자 ID (String 타입)
	 * @return 최신 RecommendationRecord (1:N 관계 포함)
	 */
	Optional<RecommendationRecord> findLatestByUserId(String userId);
}