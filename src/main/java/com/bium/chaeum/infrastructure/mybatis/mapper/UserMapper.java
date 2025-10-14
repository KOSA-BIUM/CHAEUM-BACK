package com.bium.chaeum.infrastructure.mybatis.mapper;

import com.bium.chaeum.infrastructure.mybatis.record.UserRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserRecord selectById(@Param("userId") String userId);
    UserRecord selectByEmail(@Param("email") String email);
    Long existsByEmail(@Param("email") String email);

    int insert(UserRecord userRecord);
    int update(UserRecord userRecord);
}
