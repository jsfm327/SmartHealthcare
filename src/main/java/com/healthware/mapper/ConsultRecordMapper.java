package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.ConsultRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultRecordMapper extends BaseMapper<ConsultRecord> {

    List<ConsultRecord> selectByUserId(@Param("userId") Long userId);
}
