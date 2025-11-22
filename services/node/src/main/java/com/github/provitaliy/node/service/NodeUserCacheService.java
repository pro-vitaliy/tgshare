package com.github.provitaliy.node.service;

import com.github.provitaliy.node.user.NodeUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NodeUserCacheService {
    private final RedisTemplate<String, NodeUser> redisTemplate;
    private static final String PREFIX = "user:";

    public void save(NodeUser nodeUser) {
        redisTemplate.opsForValue().set(PREFIX + nodeUser.getTelegramUserId(), nodeUser);
    }

    public Optional<NodeUser> findByTelegramId(Long telegramUserId) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + telegramUserId));
        } catch (Exception e) {
            // TODO: send alert/metric: Redis is unavailable
            log.error("Redis unavailable", e);
            return Optional.empty();
        }
    }

    public void delete(long telegramId) {
        redisTemplate.delete(PREFIX + telegramId);
    }

}
