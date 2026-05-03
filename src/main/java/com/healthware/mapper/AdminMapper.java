package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    Admin selectByUsername(@Param("username") String username);
}
