package com.healthware.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackDTO {

    private String title;

    @NotBlank(message = "反馈内容不能为空")
    private String content;

    private String contact;
}
