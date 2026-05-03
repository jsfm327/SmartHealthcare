package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Room;
import com.healthware.vo.RoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {

    List<RoomVO> selectByDepartmentId(@Param("deptId") Long deptId);
}
