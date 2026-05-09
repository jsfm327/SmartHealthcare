package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.FeedbackDTO;
import com.healthware.service.FeedbackService;
import com.healthware.vo.FeedbackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@Tag(name = "反馈管理", description = "用户反馈提交与管理")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "提交反馈", description = "用户提交反馈")
    public Result<Void> submit(HttpServletRequest request, @Valid @RequestBody FeedbackDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        feedbackService.submitFeedback(userId, dto);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "我的反馈", description = "获取当前用户的反馈列表")
    public Result<List<FeedbackVO>> myFeedbacks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(feedbackService.myFeedbacks(userId));
    }

    @GetMapping("/list")
    @Operation(summary = "反馈列表", description = "分页获取所有反馈（管理端）")
    public Result<PageResult<FeedbackVO>> listAll(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(feedbackService.listAll(page, size));
    }

    @PutMapping("/{id}/reply")
    @Operation(summary = "回复反馈", description = "管理员回复用户反馈")
    public Result<Void> reply(
            @Parameter(description = "反馈ID") @PathVariable Long id,
            @Parameter(description = "回复内容") @RequestParam String replyContent) {
        feedbackService.replyFeedback(id, replyContent);
        return Result.success();
    }
}
