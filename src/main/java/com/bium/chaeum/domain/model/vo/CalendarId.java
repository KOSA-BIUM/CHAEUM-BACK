package com.bium.chaeum.domain.model.vo;

import com.bium.chaeum.domain.shared.identifier.IdGenerators;
import com.bium.chaeum.domain.shared.identifier.StringId;

public class CalendarId extends StringId {

    private CalendarId(String value) {
        super(value);
    }

    public static CalendarId of(String value) {
        return new CalendarId(value);
    }

    public static CalendarId newId() {
        return new CalendarId(IdGenerators.generateUUIDv4());
    }
}
