package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

/**
 * author: 이상우
 */
public final class RecommendationId extends StringId {

	private RecommendationId(String value) {
		super(value);
	}
	
	public static RecommendationId of(String value) {
        return new RecommendationId(value);
    }

    public static RecommendationId newId() {
        return new RecommendationId(IdGenerators.generateUUIDv4());
    }
}
