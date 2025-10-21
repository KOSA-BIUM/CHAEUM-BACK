package com.bium.chaeum.infrastructure.mybatis.repository;

import org.springframework.stereotype.Repository;

import com.bium.chaeum.domain.model.entity.RecommendationMealItem;
import com.bium.chaeum.domain.model.repository.RecommendationMealItemRepository;
import com.bium.chaeum.infrastructure.mybatis.mapper.RecommendationMealItemMapper;
import com.bium.chaeum.infrastructure.mybatis.record.RecommendationMealItemRecord;

import lombok.RequiredArgsConstructor;


/**
 * author: 이상우
 */
@Repository
@RequiredArgsConstructor
public class MyBatisRecommendationMealItemRepository implements RecommendationMealItemRepository {
	
	private final RecommendationMealItemMapper mapper;

	@Override
	public void save(RecommendationMealItem recommendationMealItem) {
		mapper.insert(toRecord(recommendationMealItem));
	}
	
	// domain -> record
	private RecommendationMealItemRecord toRecord(RecommendationMealItem recommendationMealItem) {
		return RecommendationMealItemRecord.builder()
				.recommendationMealItemId(recommendationMealItem.getId().value())
				.recommendationId(recommendationMealItem.getRecommendationId().value())
				.dayNumber(recommendationMealItem.getAiMealItem().dayNumber())
				.division(recommendationMealItem.getAiMealItem().division().toString())
				.name(recommendationMealItem.getAiMealItem().foodName())
				.carbohydrate(recommendationMealItem.getAiMealItem().carbohydrate())
				.protein(recommendationMealItem.getAiMealItem().protein())
				.fat(recommendationMealItem.getAiMealItem().fat())
				.sodium(recommendationMealItem.getAiMealItem().sodium())
				.calorie(recommendationMealItem.getAiMealItem().calories())
				.build();
	}
}