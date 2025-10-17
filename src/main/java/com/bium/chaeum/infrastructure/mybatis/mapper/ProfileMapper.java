package com.bium.chaeum.infrastructure.mybatis.mapper;

import com.bium.chaeum.infrastructure.mybatis.record.ProfileRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfileMapper {

    ProfileRecord selectById(@Param("userId") String userId);

    int insert(ProfileRecord profileRecord);
    int update(ProfileRecord profileRecord);
}
