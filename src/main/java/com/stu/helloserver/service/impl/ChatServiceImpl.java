package com.stu.helloserver.service.impl;

import org.springframework.ai.deepseek.DeepSeekChatOptions;
import com.stu.helloserver.dto.ChatRequestDTO;
import com.stu.helloserver.service.ChatService;
import com.stu.helloserver.vo.ChatResponseVO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private static final int MAX_HISTORY_ROUNDS = 3;
    private static final String REDIS_KEY_PREFIX = "chat:session:";

    private final ChatClient chatClient;
    private final StringRedisTemplate stringRedisTemplate;

    public ChatServiceImpl(ChatClient.Builder chatClientBuilder,
                           StringRedisTemplate stringRedisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请结合历史对话上下文回答用户问题")
                .defaultOptions(
                        DeepSeekChatOptions.builder()
                                .temperature(0.7)
                                .build()
                )
                .build();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId();
        if (!StringUtils.hasText(sessionId)) {
            throw new IllegalArgumentException("sessionId 不能为空");
        }

        String message = requestDTO.getMessage();
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("message 不能为空");
        }

        String redisKey = REDIS_KEY_PREFIX + sessionId;

        // 1. 读取历史消息
        List<String> records = stringRedisTemplate.opsForList().range(redisKey, 0, -1);
        String historyText = "";
        if (records != null && !records.isEmpty()) {
            historyText = String.join("\n", records);
        }

        // 2. 拼接上下文
        String finalPrompt = """
                以下是历史对话：
                %s
                当前用户问题：
                %s
                """.formatted(historyText, message);

        // 3. 调用模型
        String answer = chatClient.prompt(finalPrompt)
                .call()
                .content();

        // 4. 保存本轮记录
        String recordText = "用户：" + message + "\n助手：" + answer;
        stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

        // 5. 只保留最近 N 轮
        Long size = stringRedisTemplate.opsForList().size(redisKey);
        if (size != null && size > MAX_HISTORY_ROUNDS) {
            stringRedisTemplate.opsForList().trim(redisKey, size - MAX_HISTORY_ROUNDS, size - 1);
        }

        return new ChatResponseVO(message, answer);
    }
}
