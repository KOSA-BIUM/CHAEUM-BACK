package com.bium.chaeum.infrastructure.mybatis.repository;

import com.bium.chaeum.domain.model.entity.User;
import com.bium.chaeum.domain.model.repository.UserRepository;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.infrastructure.mybatis.mapper.UserMapper;
import com.bium.chaeum.infrastructure.mybatis.record.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements UserRepository {

    private final UserMapper mapper;

    @Override
    public Optional<User> findById(UserId id) {
        UserRecord userRecord = mapper.selectById(id.value());
        return Optional.ofNullable(userRecord).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserRecord userRecord = mapper.selectByEmail(email);
        return Optional.ofNullable(userRecord).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mapper.existsByEmail(email) > 0;
    }

    @Override
    public void save(User user) {
        // 존재하면 UPDATE, 없으면 INSERT
        UserRecord existing = mapper.selectById(user.getId().value());
        if (existing == null) {
            mapper.insert(toRecord(user));
        } else {
            mapper.update(toRecord(user));
        }
    }

    private User toDomain(UserRecord record) {
        return User.reconstruct(UserId.of(record.getUserId()),
                record.getEmail(),
                record.getPassword(),
                record.getName());
    }

    private UserRecord toRecord(User user) {
        return UserRecord.builder()
                .userId(user.getId().value())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .build();
    }
}
