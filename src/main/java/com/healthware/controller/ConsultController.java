package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.service.ConsultService;
import com.healthware.vo.ConsultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consult")
public class ConsultController {

    @Autowired
    private ConsultService consultService;

    @PostMapping("/ask")
    public Result<ConsultVO> ask(HttpServletRequest request, @RequestParam String symptoms) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(consultService.askQuestion(userId, symptoms));
    }

    @GetMapping("/history")
    public Result<List<ConsultVO>> history(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(consultService.history(userId));
    }

    @GetMapping("/{id}")
    public Result<ConsultVO> getDetail(@PathVariable Long id) {
        return Result.success(consultService.getDetail(id));
    }
}
