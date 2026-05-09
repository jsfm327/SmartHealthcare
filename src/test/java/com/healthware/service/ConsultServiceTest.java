package com.healthware.service;

import com.healthware.entity.ConsultRecord;
import com.healthware.mapper.ConsultRecordMapper;
import com.healthware.service.impl.ConsultServiceImpl;
import com.healthware.vo.ConsultVO;
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
class ConsultServiceTest {

    @Mock
    private ConsultRecordMapper consultRecordMapper;

    @InjectMocks
    private ConsultServiceImpl consultService;

    private ConsultRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = new ConsultRecord();
        testRecord.setId(1L);
        testRecord.setUserId(1L);
        testRecord.setTitle("头疼发烧");
        testRecord.setSymptoms("头疼、发烧38度");
        testRecord.setAiResponse("{\"analysis\":\"功能开发中\",\"advice\":\"请前往医院就诊\",\"department\":\"内科\"}");
        testRecord.setDepartmentSuggest("内科");
        testRecord.setStatus(1);
    }

    @Test
    void askQuestion_Success() {
        when(consultRecordMapper.insert(any(ConsultRecord.class))).thenReturn(1);

        ConsultVO result = consultService.askQuestion(1L, "头疼、发烧38度");

        assertNotNull(result);
        assertEquals("内科", result.getDepartmentSuggest());
        assertEquals("头疼、发烧38度", result.getSymptoms());
        verify(consultRecordMapper).insert(any(ConsultRecord.class));
    }

    @Test
    void askQuestion_ShortSymptoms() {
        when(consultRecordMapper.insert(any(ConsultRecord.class))).thenReturn(1);

        ConsultVO result = consultService.askQuestion(1L, "头疼");

        assertNotNull(result);
        assertEquals("头疼", result.getTitle());
    }

    @Test
    void history_Success() {
        List<ConsultRecord> records = Arrays.asList(testRecord);
        when(consultRecordMapper.selectByUserId(1L)).thenReturn(records);

        List<ConsultVO> result = consultService.history(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("头疼发烧", result.get(0).getTitle());
    }

    @Test
    void getDetail_Success() {
        when(consultRecordMapper.selectById(1L)).thenReturn(testRecord);

        ConsultVO result = consultService.getDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("头疼发烧", result.getTitle());
        assertEquals("内科", result.getDepartmentSuggest());
    }

    @Test
    void getDetail_NotFound() {
        when(consultRecordMapper.selectById(999L)).thenReturn(null);

        ConsultVO result = consultService.getDetail(999L);

        assertNull(result);
    }
}
