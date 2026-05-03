package com.healthware.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.healthware.entity.ConsultRecord;
import com.healthware.mapper.ConsultRecordMapper;
import com.healthware.service.ConsultService;
import com.healthware.vo.ConsultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultServiceImpl implements ConsultService {

    // TODO: 启用 Spring AI 后取消注释
    // @Autowired
    // private ChatClient chatClient;

    @Autowired
    private ConsultRecordMapper consultRecordMapper;

    @Override
    public ConsultVO askQuestion(Long userId, String symptoms) {
        // TODO: 启用 Spring AI 后恢复大模型调用
        String response = "{\"analysis\":\"功能开发中，即将接入AI大模型\",\"advice\":\"请前往医院就诊\",\"department\":\"内科\"}";

        String analysis = "功能开发中，即将接入AI大模型";
        String advice = "请前往医院就诊";
        String department = "内科";
        try {
            JSONObject result = JSONObject.parseObject(response);
            analysis = result.getString("analysis");
            advice = result.getString("advice");
            department = result.getString("department");
        } catch (Exception e) {
            analysis = response;
        }

        ConsultRecord record = new ConsultRecord();
        record.setUserId(userId);
        record.setTitle(symptoms.substring(0, Math.min(50, symptoms.length())));
        record.setSymptoms(symptoms);
        record.setAiResponse(response);
        record.setDepartmentSuggest(department);
        record.setStatus(1);
        consultRecordMapper.insert(record);

        ConsultVO vo = new ConsultVO();
        vo.setId(record.getId());
        vo.setTitle(record.getTitle());
        vo.setSymptoms(symptoms);
        vo.setAnalysis(analysis);
        vo.setAdvice(advice);
        vo.setDepartmentSuggest(department);
        vo.setStatus(1);
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    @Override
    public List<ConsultVO> history(Long userId) {
        List<ConsultRecord> records = consultRecordMapper.selectByUserId(userId);
        return records.stream().map(r -> {
            ConsultVO vo = new ConsultVO();
            vo.setId(r.getId());
            vo.setTitle(r.getTitle());
            vo.setSymptoms(r.getSymptoms());
            vo.setDepartmentSuggest(r.getDepartmentSuggest());
            vo.setStatus(r.getStatus());
            vo.setCreateTime(r.getCreateTime());
            return vo;
        }).toList();
    }

    @Override
    public ConsultVO getDetail(Long id) {
        ConsultRecord record = consultRecordMapper.selectById(id);
        if (record == null) return null;
        ConsultVO vo = new ConsultVO();
        vo.setId(record.getId());
        vo.setTitle(record.getTitle());
        vo.setSymptoms(record.getSymptoms());
        vo.setAnalysis(record.getAiResponse());
        vo.setDepartmentSuggest(record.getDepartmentSuggest());
        vo.setStatus(record.getStatus());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
