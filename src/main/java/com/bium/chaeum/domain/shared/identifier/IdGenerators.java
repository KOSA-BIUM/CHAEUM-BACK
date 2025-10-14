package com.bium.chaeum.domain.shared.identifier;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

public class IdGenerators {

    // 완전 무작위
    public static String generateUUIDv4() {
        return UUID.randomUUID().toString();
    }

    // 타임스탬프를 기반으로 하는 정렬 가능한 UUID -> 데이터베이스 인덱스 효율성 향상.
    public static String generateUUIDv7() {
        return Generators.timeBasedEpochGenerator().generate().toString();
    }
}
