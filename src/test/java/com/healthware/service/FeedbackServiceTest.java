package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.FeedbackDTO;
import com.healthware.entity.Feedback;
import com.healthware.mapper.FeedbackMapper;
import com.healthware.service.impl.FeedbackServiceImpl;
import com.healthware.vo.FeedbackVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackMapper feedbackMapper;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        testFeedback = new Feedback();
        testFeedback.setId(1L);
        testFeedback.setUserId(1L);
        testFeedback.setTitle("系统建议");
        testFeedback.setContent("建议增加在线支付功能");
        testFeedback.setContact("13800138000");
        testFeedback.setStatus(0);
    }

    @Test
    void submitFeedback_Success() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setTitle("系统建议");
        dto.setContent("建议增加在线支付功能");
        dto.setContact("13800138000");

        when(feedbackMapper.insert(any(Feedback.class))).thenReturn(1);

        assertDoesNotThrow(() -> feedbackService.submitFeedback(1L, dto));
        verify(feedbackMapper).insert(any(Feedback.class));
    }

    @Test
    void myFeedbacks_Success() {
        List<FeedbackVO> feedbacks = Arrays.asList(new FeedbackVO());
        when(feedbackMapper.selectByUserId(1L)).thenReturn(feedbacks);

        List<FeedbackVO> result = feedbackService.myFeedbacks(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void listAll_Success() {
        List<Feedback> feedbacks = Arrays.asList(testFeedback);
        when(feedbackMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Feedback>(1, 10).setRecords(feedbacks));

        PageResult<FeedbackVO> result = feedbackService.listAll(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void replyFeedback_Success() {
        when(feedbackMapper.selectById(1L)).thenReturn(testFeedback);
        when(feedbackMapper.updateById(testFeedback)).thenReturn(1);

        assertDoesNotThrow(() -> feedbackService.replyFeedback(1L, "感谢您的反馈"));
        verify(feedbackMapper).updateById(testFeedback);
    }

    @Test
    void replyFeedback_NotFound() {
        when(feedbackMapper.selectById(999L)).thenReturn(null);

        assertDoesNotThrow(() -> feedbackService.replyFeedback(999L, "感谢您的反馈"));
        verify(feedbackMapper, never()).updateById(any(Feedback.class));
    }
}
