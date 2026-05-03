package com.healthware.service;

import com.healthware.vo.ConsultVO;

import java.util.List;

public interface ConsultService {

    ConsultVO askQuestion(Long userId, String symptoms);

    List<ConsultVO> history(Long userId);

    ConsultVO getDetail(Long id);
}
