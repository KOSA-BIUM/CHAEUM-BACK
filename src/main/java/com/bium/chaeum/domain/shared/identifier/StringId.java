package com.bium.chaeum.domain.shared.identifier;

// String 타입 기반 식별자(UUID v4/v7, ULID)
public non-sealed abstract class StringId implements EntityIdentifier<String> {

    private final String value;

    protected StringId(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("id is blank");
        this.value = value;
    }

    public String value() { return value; }

    @Override public String asString() { return value; }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public boolean equals(Object o) {
        return (this == o) || (o instanceof StringId other && value.equals(other.value));
    }
}
