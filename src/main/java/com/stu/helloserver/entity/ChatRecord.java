package com.stu.helloserver.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRecord {
    private String sessionId;
    private String role;
    private String content;
    private LocalDateTime createTime;
}
