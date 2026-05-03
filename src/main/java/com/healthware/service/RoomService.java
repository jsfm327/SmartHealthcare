package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.entity.Room;
import com.healthware.vo.RoomVO;

import java.util.List;

public interface RoomService {

    PageResult<RoomVO> listRooms(int page, int size);

    RoomVO getDetail(Long id);

    List<RoomVO> listByDepartment(Long deptId);

    void addRoom(Room room);

    void updateRoom(Long id, Room room);

    void deleteRoom(Long id);
}
