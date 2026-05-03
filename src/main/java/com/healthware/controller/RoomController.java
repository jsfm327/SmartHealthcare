package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.entity.Room;
import com.healthware.service.RoomService;
import com.healthware.vo.RoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/list")
    public Result<PageResult<RoomVO>> list(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return Result.success(roomService.listRooms(page, size));
    }

    @GetMapping("/{id}")
    public Result<RoomVO> getDetail(@PathVariable Long id) {
        return Result.success(roomService.getDetail(id));
    }

    @GetMapping("/department/{deptId}")
    public Result<List<RoomVO>> listByDepartment(@PathVariable Long deptId) {
        return Result.success(roomService.listByDepartment(deptId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Room room) {
        roomService.addRoom(room);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Room room) {
        roomService.updateRoom(id, room);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Result.success();
    }
}
