package com.bium.chaeum.infrastructure.mybatis.repository;

import com.bium.chaeum.domain.model.entity.GenderType;
import com.bium.chaeum.domain.model.entity.PreferredDietType;
import com.bium.chaeum.domain.model.entity.Profile;
import com.bium.chaeum.domain.model.repository.ProfileRepository;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.infrastructure.mybatis.mapper.ProfileMapper;
import com.bium.chaeum.infrastructure.mybatis.record.ProfileRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisProfileRepository implements ProfileRepository {

    private final ProfileMapper mapper;


    @Override
    public Optional<Profile> findById(UserId userId) {
        ProfileRecord profileRecord = mapper.selectById(userId.value());
        return Optional.ofNullable(profileRecord).map(this::toDomain);
    }

    @Override
    public void save(Profile profile) {
        // 존재하면 UPDATE, 없으면 INSERT
        ProfileRecord existing = mapper.selectById(profile.getUserId().value());
        if (existing == null) {
            mapper.insert(toRecord(profile));
        } else {
            mapper.update(toRecord(profile));
        }
    }

    private Profile toDomain(ProfileRecord record) {
        return Profile.reconstruct(
                UserId.of(record.getUserId()),
                record.getBirthDate(),
                GenderType.valueOf(record.getGender()),
                record.getHeight(),
                record.getWeight(),
                PreferredDietType.valueOf(record.getPreferredDiet()),
                record.getBmr());
    }

    private ProfileRecord toRecord(Profile profile) {
        return ProfileRecord.builder()
                .userId(profile.getUserId().value())
                .birthDate(profile.getBirthDate())
                .gender(profile.getGender().name())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .preferredDiet(profile.getPreferredDiet().name())
                .bmr(profile.getBmr())
                .build();
    }
}
