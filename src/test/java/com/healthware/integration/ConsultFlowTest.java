package com.healthware.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthware.dto.LoginDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.utils.JwtInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AI问诊完整链路测试
 * 测试流程：登录 → 发起问诊 → 查询历史 → 查询详情
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AI问诊完整链路测试")
class ConsultFlowTest {

    @MockBean
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.healthware.utils.JwtUtil jwtUtil;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // 注册并登录
        String username = "testuser_" + System.currentTimeMillis();
        String phone = "138" + String.format("%08d", System.currentTimeMillis() % 100000000);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("password123");
        registerDTO.setRealName("测试用户");
        registerDTO.setPhone(phone);
        registerDTO.setGender(1);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        userToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("token").asText();

        // 配置mock拦截器：从token解析userId并设置到request属性
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletRequest request = invocation.getArgument(0);
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    Long userId = jwtUtil.getUserId(token);
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", "user");
                } catch (Exception ignored) {}
            }
            return true;
        }).when(jwtInterceptor).preHandle(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    @Order(1)
    @DisplayName("问诊流程测试")
    void testConsultFlow() throws Exception {
        // 1. 发起问诊
        MvcResult askResult = mockMvc.perform(post("/api/consult/ask")
                        .header("Authorization", "Bearer " + userToken)
                        .param("symptoms", "头疼、发烧38度、咳嗽"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.symptoms").value("头疼、发烧38度、咳嗽"))
                .andExpect(jsonPath("$.data.departmentSuggest").value("内科"))
                .andReturn();

        Long consultId = objectMapper.readTree(askResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 2. 查询问诊历史
        mockMvc.perform(get("/api/consult/history")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        // 3. 查询问诊详情
        mockMvc.perform(get("/api/consult/" + consultId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(consultId))
                .andExpect(jsonPath("$.data.symptoms").value("头疼、发烧38度、咳嗽"));
    }

    @Test
    @Order(2)
    @DisplayName("多次问诊测试")
    void testMultipleConsultFlow() throws Exception {
        // 发起多次问诊
        mockMvc.perform(post("/api/consult/ask")
                        .header("Authorization", "Bearer " + userToken)
                        .param("symptoms", "症状1：头疼"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/consult/ask")
                        .header("Authorization", "Bearer " + userToken)
                        .param("symptoms", "症状2：发烧"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/consult/ask")
                        .header("Authorization", "Bearer " + userToken)
                        .param("symptoms", "症状3：咳嗽"))
                .andExpect(status().isOk());

        // 验证历史记录数量
        mockMvc.perform(get("/api/consult/history")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }
}
