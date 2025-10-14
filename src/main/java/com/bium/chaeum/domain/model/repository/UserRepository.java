package com.bium.chaeum.domain.model.repository;

import com.bium.chaeum.domain.model.entity.User;
import com.bium.chaeum.domain.model.vo.UserId;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(UserId id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // UPSERT = 새 UUID면 insert, 아니면 update
    void save(User user);
}
