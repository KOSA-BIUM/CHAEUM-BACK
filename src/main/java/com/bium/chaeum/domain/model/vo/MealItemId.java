package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

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
