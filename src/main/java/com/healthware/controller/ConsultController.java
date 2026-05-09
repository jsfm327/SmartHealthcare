package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.service.ConsultService;
import com.healthware.vo.ConsultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consult")
@Tag(name = "AI问诊", description = "智能问诊功能")
public class ConsultController {

    @Autowired
    private ConsultService consultService;

    @PostMapping("/ask")
    @Operation(summary = "发起问诊", description = "向AI发起问诊")
    public Result<ConsultVO> ask(HttpServletRequest request,
                                 @Parameter(description = "症状描述") @RequestParam String symptoms) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(consultService.askQuestion(userId, symptoms));
    }

    @GetMapping("/history")
    @Operation(summary = "问诊历史", description = "获取当前用户的问诊历史")
    public Result<List<ConsultVO>> history(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(consultService.history(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "问诊详情", description = "获取问诊详细信息")
    public Result<ConsultVO> getDetail(
            @Parameter(description = "问诊ID") @PathVariable Long id) {
        return Result.success(consultService.getDetail(id));
    }
}
