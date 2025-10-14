package com.bium.chaeum.domain.shared.identifier;

public sealed interface EntityIdentifier<T> permits StringId, LongId {
    String asString(); // 로깅/외부표현용
}
