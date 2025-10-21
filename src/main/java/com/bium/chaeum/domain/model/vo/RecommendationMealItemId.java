package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

/**
 * author: 이상우
 */
public class RecommendationMealItemId extends StringId {

	private RecommendationMealItemId(String value) {
		super(value);
	}
	
	public static RecommendationMealItemId of(String value) {
        return new RecommendationMealItemId(value);
    }

    public static RecommendationMealItemId newId() {
        return new RecommendationMealItemId(IdGenerators.generateUUIDv4());
    }
}
