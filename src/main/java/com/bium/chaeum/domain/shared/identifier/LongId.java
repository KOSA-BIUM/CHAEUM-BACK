package com.bium.chaeum.domain.shared.identifier;

// Long 타입 기반 식별자(DB 시퀀스)
public non-sealed abstract class LongId implements EntityIdentifier<Long> {

    private final long value;

    protected LongId(long value) {
        if (value <= 0) throw new IllegalArgumentException("id must be positive");
        this.value = value;
    }

    public long asLong() { return value; }

    @Override public String asString() { return Long.toString(value); }
    @Override public String toString() { return asString(); }
    @Override public int hashCode() { return Long.hashCode(value); }
    @Override public boolean equals(Object o) {
        return (this == o) || (o instanceof LongId other && value == other.value);
    }
}
