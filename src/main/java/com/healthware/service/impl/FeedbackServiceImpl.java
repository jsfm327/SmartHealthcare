package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.dto.FeedbackDTO;
import com.healthware.entity.Feedback;
import com.healthware.mapper.FeedbackMapper;
import com.healthware.service.FeedbackService;
import com.healthware.vo.FeedbackVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public void submitFeedback(Long userId, FeedbackDTO dto) {
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(dto, feedback);
        feedback.setUserId(userId);
        feedback.setStatus(0);
        feedbackMapper.insert(feedback);
    }

    @Override
    public List<FeedbackVO> myFeedbacks(Long userId) {
        return feedbackMapper.selectByUserId(userId);
    }

    @Override
    public PageResult<FeedbackVO> listAll(int page, int size) {
        Page<Feedback> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Feedback::getCreateTime);
        Page<Feedback> result = feedbackMapper.selectPage(pageParam, wrapper);
        List<FeedbackVO> records = result.getRecords().stream().map(f -> {
            FeedbackVO vo = new FeedbackVO();
            BeanUtils.copyProperties(f, vo);
            return vo;
        }).toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public void replyFeedback(Long id, String replyContent) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) return;
        feedback.setReplyContent(replyContent);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus(1);
        feedbackMapper.updateById(feedback);
    }
}
