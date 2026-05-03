package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.FeedbackDTO;
import com.healthware.vo.FeedbackVO;

import java.util.List;

public interface FeedbackService {

    void submitFeedback(Long userId, FeedbackDTO dto);

    List<FeedbackVO> myFeedbacks(Long userId);

    PageResult<FeedbackVO> listAll(int page, int size);

    void replyFeedback(Long id, String replyContent);
}
