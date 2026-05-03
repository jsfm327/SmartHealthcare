package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.FeedbackDTO;
import com.healthware.service.FeedbackService;
import com.healthware.vo.FeedbackVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Result<Void> submit(HttpServletRequest request, @Valid @RequestBody FeedbackDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        feedbackService.submitFeedback(userId, dto);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<FeedbackVO>> myFeedbacks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(feedbackService.myFeedbacks(userId));
    }

    @GetMapping("/list")
    public Result<PageResult<FeedbackVO>> listAll(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(feedbackService.listAll(page, size));
    }

    @PutMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestParam String replyContent) {
        feedbackService.replyFeedback(id, replyContent);
        return Result.success();
    }
}
