package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

public final class UserId extends StringId {

    private UserId(String value) {
        super(value);
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId newId() {
        return new UserId(IdGenerators.generateUUIDv4());
    }


}
