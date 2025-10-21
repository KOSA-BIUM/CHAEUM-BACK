package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

// MealItemId는 MealItem 엔티티의 고유 식별자를 나타냅니다. (author: 나규태)
public final class MealItemId extends StringId {
	
    private MealItemId(String value) {
        super(value);
    }

    public static MealItemId of(String value) {
        return new MealItemId(value);
    }

    public static MealItemId newId() {
        return new MealItemId(IdGenerators.generateUUIDv4());
    }
}
