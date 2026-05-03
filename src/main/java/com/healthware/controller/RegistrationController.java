package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.AppointmentDTO;
import com.healthware.service.RegistrationService;
import com.healthware.vo.RegistrationVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/appointment")
    public Result<RegistrationVO> appointment(HttpServletRequest request, @Valid @RequestBody AppointmentDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(registrationService.createAppointment(userId, dto));
    }

    @GetMapping("/my")
    public Result<PageResult<RegistrationVO>> myRegistrations(HttpServletRequest request,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(registrationService.myRegistrations(userId, page, size));
    }

    @GetMapping("/{id}")
    public Result<RegistrationVO> getDetail(@PathVariable Long id) {
        return Result.success(registrationService.getDetail(id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(HttpServletRequest request, @PathVariable Long id,
                               @RequestParam(required = false) String reason) {
        Long userId = (Long) request.getAttribute("userId");
        registrationService.cancelRegistration(id, userId, reason);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<RegistrationVO>> listAll(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String date,
                                                       @RequestParam(required = false) Long deptId) {
        return Result.success(registrationService.listAll(page, size, date, deptId));
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirmVisit(@PathVariable Long id) {
        registrationService.confirmVisit(id);
        return Result.success();
    }
}
