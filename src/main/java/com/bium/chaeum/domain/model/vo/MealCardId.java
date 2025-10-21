package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

// MealCardId는 MealCard 엔티티의 고유 식별자를 나타냅니다. (author: 나규태)
public final class MealCardId extends StringId {
	
    private MealCardId(String value) {
        super(value);
    }

    public static MealCardId of(String value) {
        return new MealCardId(value);
    }

    public static MealCardId newId() {
        return new MealCardId(IdGenerators.generateUUIDv4());
    }
}
