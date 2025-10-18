package com.bium.chaeum.domain.model.repository;

import com.bium.chaeum.domain.model.entity.Profile;
import com.bium.chaeum.domain.model.vo.UserId;

import java.util.Optional;

public interface ProfileRepository {

    Optional<Profile> findById(UserId id);

    // UPSERT = 새 UUID면 insert, 아니면 update
    void save(Profile profile);
}
