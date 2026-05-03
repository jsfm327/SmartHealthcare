package com.healthware.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackVO {

    private Long id;
    private Long userId;
    private String userName;
    private String title;
    private String content;
    private String contact;
    private String replyContent;
    private LocalDateTime replyTime;
    private Integer status;
    private LocalDateTime createTime;
}
