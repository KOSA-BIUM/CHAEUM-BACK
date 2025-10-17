package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

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
