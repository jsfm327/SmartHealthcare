package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUsername(@Param("username") String username);

    User selectByPhone(@Param("phone") String phone);

    int updateLoginFailCount(@Param("id") Long id, @Param("count") int count);

    int unlockUser(@Param("id") Long id);
}
