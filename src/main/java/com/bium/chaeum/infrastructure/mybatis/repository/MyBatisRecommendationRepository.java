package com.bium.chaeum.infrastructure.mybatis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bium.chaeum.domain.model.entity.Recommendation;
import com.bium.chaeum.domain.model.entity.RecommendationMealItem;
import com.bium.chaeum.domain.model.repository.RecommendationRepository;
import com.bium.chaeum.domain.model.vo.AiMealItem;
import com.bium.chaeum.domain.model.vo.RecommendationId;
import com.bium.chaeum.domain.model.vo.RecommendationMealItemId;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.infrastructure.mybatis.mapper.RecommendationMapper;
import com.bium.chaeum.infrastructure.mybatis.record.RecommendationMealItemRecord;
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
		Optional<RecommendationRecord> recordOptional = mapper.findLatestByUserId(userId.value());
		return recordOptional.map(this::toDomainWithMealItems);
	}
	
	private Recommendation toDomainWithMealItems(RecommendationRecord record) {
		Recommendation recommendation = Recommendation.reconstruct(
				RecommendationId.of(record.getRecommendationId()),
				UserId.of(record.getUserId()),
				record.getRequestPrompt(),
				record.getRecommendationReason(),
				record.getCreatedAt());
		
		if (record.getMealItems() != null) {
			List<RecommendationMealItem> mealItems = record.getMealItems().stream()
					.map(this::toMealItemDomain)
					.toList();
			
			recommendation.setMealItems(mealItems);
		}
		
		return recommendation;
	}
	
	private RecommendationMealItem toMealItemDomain(RecommendationMealItemRecord record) {
			
			AiMealItem aiMealItem = AiMealItem.reconstruct(
	                record.getDayNumber(),
	                record.getDivision(),
	                record.getName(),
	                record.getCarbohydrate(),
	                record.getProtein(),
	                record.getFat(),
	                record.getSodium(),
	                record.getCalorie()
	        );
			
			return RecommendationMealItem.reconstruct(
					RecommendationMealItemId.of(record.getRecommendationMealItemId()),
					RecommendationId.of(record.getRecommendationId()),
					aiMealItem);
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
