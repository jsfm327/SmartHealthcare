package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "反馈请求参数")
public class FeedbackDTO {

    @Schema(description = "标题", example = "系统建议")
    private String title;

    @NotBlank(message = "反馈内容不能为空")
    @Schema(description = "反馈内容", example = "建议增加在线支付功能")
    private String content;

    @Schema(description = "联系方式", example = "13800138000")
    private String contact;
}
