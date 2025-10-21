package com.bium.chaeum.domain.model.repository;

import java.util.Optional;

import com.bium.chaeum.domain.model.entity.Recommendation;
import com.bium.chaeum.domain.model.vo.UserId;

/**
 * author: 이상우
 */
public interface RecommendationRepository {

	void save(Recommendation recommendation);
	
	/**
     * 특정 사용자의 가장 최근 식단 추천 기록을 조회합니다.
     * @param userId 사용자 ID
     * @return 최신 Recommendation 엔티티와 그에 연결된 MealItem 목록을 포함하는 객체
     */
    Optional<Recommendation> findLatestByUserId(UserId userId);
}