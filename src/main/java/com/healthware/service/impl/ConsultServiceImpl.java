package com.healthware.service.impl;

import com.healthware.entity.ConsultRecord;
import com.healthware.mapper.ConsultRecordMapper;
import com.healthware.service.ConsultService;
import com.healthware.vo.ConsultVO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultServiceImpl implements ConsultService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ConsultRecordMapper consultRecordMapper;

    private static final String SYSTEM_PROMPT = """
            你是一个专业的智能医疗问诊助手。请严格按照以下结构回复，每个部分用对应标题开头：

            【症状分析】
            对患者描述的症状进行专业分析，归纳主要症状表现。

            【可能原因】
            列出2-3种可能的病因，每种简要说明理由。

            【建议】
            给出日常生活注意事项和就医前的自我护理建议。

            【建议科室】
            只输出一个最匹配的科室名称，不要其他内容。

            注意：你仅提供参考建议，不能开具处方或做出诊断。
            """;

    @Override
    public ConsultVO askQuestion(Long userId, String symptoms) {
        String response;
        try {
            response = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(symptoms)
                    .call()
                    .content();
        } catch (Exception e) {
            response = "【症状分析】AI服务暂时不可用\n【可能原因】网络或服务异常\n【建议】请稍后重试或前往医院就诊\n【建议科室】内科";
        }

        String analysis = extractSection(response, "症状分析");
        String advice = extractSection(response, "可能原因") + "\n\n" + extractSection(response, "建议");
        String department = extractSection(response, "建议科室");
        if (department.isEmpty()) department = "内科";

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

    private String extractSection(String text, String sectionName) {
        if (text == null) return "";
        String marker = "【" + sectionName + "】";
        int start = text.indexOf(marker);
        if (start < 0) return "";
        start += marker.length();
        while (start < text.length() && (text.charAt(start) == '\n' || text.charAt(start) == '\r')) {
            start++;
        }
        int end = text.indexOf("【", start);
        if (end < 0) end = text.length();
        return text.substring(start, end).trim();
    }

    @Override
    public List<ConsultVO> history(Long userId) {
        List<ConsultRecord> records = consultRecordMapper.selectByUserId(userId);
        return records.stream().map(r -> {
            ConsultVO vo = new ConsultVO();
            vo.setId(r.getId());
            vo.setTitle(r.getTitle());
            vo.setSymptoms(r.getSymptoms());
            vo.setAnalysis(extractSection(r.getAiResponse(), "症状分析"));
            vo.setAdvice(extractSection(r.getAiResponse(), "可能原因") + "\n\n" + extractSection(r.getAiResponse(), "建议"));
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
        vo.setAnalysis(extractSection(record.getAiResponse(), "症状分析"));
        vo.setAdvice(extractSection(record.getAiResponse(), "可能原因") + "\n\n" + extractSection(record.getAiResponse(), "建议"));
        vo.setDepartmentSuggest(record.getDepartmentSuggest());
        vo.setStatus(record.getStatus());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
