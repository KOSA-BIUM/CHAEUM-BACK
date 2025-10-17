package com.bium.chaeum.infrastructure.mybatis.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bium.chaeum.domain.model.entity.Recommendation;
import com.bium.chaeum.domain.model.repository.RecommendationRepository;
import com.bium.chaeum.domain.model.vo.RecommendationId;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.infrastructure.mybatis.mapper.RecommendationMapper;
import com.bium.chaeum.infrastructure.mybatis.record.RecommendationRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisRecommendationRepository implements RecommendationRepository {

	private final RecommendationMapper mapper;
	
	@Override
	public void save(Recommendation recommendation) {
		mapper.insert(toRecord(recommendation));
		
	}
	
	@Override
	public Optional<Recommendation> findLatestByUserId(UserId userId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	private Recommendation toDomain(RecommendationRecord record) {
		return Recommendation.reconstruct(RecommendationId.of(record.getRecommendationId()),
				UserId.of(record.getUserId()),
				record.getRequestPrompt(),
				record.getRecommendationReason(),
				record.getCreatedAt());
	}
	
	private RecommendationRecord toRecord(Recommendation recommendation) {
		return RecommendationRecord.builder()
				.recommendationId(recommendation.getId().value())
				.userId(recommendation.getUserId().value())
				.requestPrompt(recommendation.getRequestPrompt())
				.recommendationReason(recommendation.getRecommendationReason())
				.createdAt(recommendation.getCreatedAt())
				.build();
	}

}
