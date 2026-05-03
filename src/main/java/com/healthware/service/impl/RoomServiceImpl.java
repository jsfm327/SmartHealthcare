package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.entity.Room;
import com.healthware.mapper.RoomMapper;
import com.healthware.service.RoomService;
import com.healthware.vo.RoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public PageResult<RoomVO> listRooms(int page, int size) {
        Page<Room> pageParam = new Page<>(page, size);
        Page<Room> result = roomMapper.selectPage(pageParam, null);
        List<RoomVO> records = result.getRecords().stream().map(room -> {
            RoomVO vo = new RoomVO();
            vo.setId(room.getId());
            vo.setName(room.getName());
            vo.setDepartmentId(room.getDepartmentId());
            vo.setLocation(room.getLocation());
            vo.setCapacity(room.getCapacity());
            vo.setStatus(room.getStatus());
            return vo;
        }).toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public RoomVO getDetail(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) return null;
        RoomVO vo = new RoomVO();
        vo.setId(room.getId());
        vo.setName(room.getName());
        vo.setDepartmentId(room.getDepartmentId());
        vo.setLocation(room.getLocation());
        vo.setCapacity(room.getCapacity());
        vo.setStatus(room.getStatus());
        return vo;
    }

    @Override
    public List<RoomVO> listByDepartment(Long deptId) {
        return roomMapper.selectByDepartmentId(deptId);
    }

    @Override
    public void addRoom(Room room) {
        room.setStatus(1);
        roomMapper.insert(room);
    }

    @Override
    public void updateRoom(Long id, Room room) {
        room.setId(id);
        roomMapper.updateById(room);
    }

    @Override
    public void deleteRoom(Long id) {
        roomMapper.deleteById(id);
    }
}
