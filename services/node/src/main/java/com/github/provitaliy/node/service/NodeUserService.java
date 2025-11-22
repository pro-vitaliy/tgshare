package com.github.provitaliy.node.service;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.common.grpc.TelegramUserIdRequest;
import com.github.provitaliy.node.exception.NodeInternalProcessingException;
import com.github.provitaliy.node.exception.RetryableGrpcException;
import com.github.provitaliy.node.exception.UserServiceProcessingException;
import com.github.provitaliy.node.exception.UserServiceUnavailableException;
import com.github.provitaliy.node.mapper.NodeUserMapper;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class NodeUserService {
    private final AppUserServiceGrpc.AppUserServiceBlockingStub userServiceStub;
    private final NodeUserMapper userMapper;
    private final NodeUserCacheService cacheService;
    private final ProducerService producerService;

    public NodeUser getOrCreateAppUser(AppUserCreateDTO userCreateDto) {
        return cacheService.findByTelegramId(userCreateDto.getTelegramUserId())
                .orElseGet(() -> createAndCacheUser(userCreateDto));
    }

    public NodeUser getByTelegramUserId(Long telegramUserId) {
        return cacheService.findByTelegramId(telegramUserId)
                .orElseGet(() -> fetchAndCacheUser(telegramUserId));
    }

    public void changeState(NodeUser nodeUser, UserState state) {
        nodeUser.setState(state);
        cacheService.save(nodeUser);
    }

    public void setEmail(NodeUser nodeUser, String email) {
        var emailEnteredEvent = new UserEmailEnteredEvent(nodeUser.getTelegramUserId(), email);
        producerService.produceRegistrationMail(emailEnteredEvent);
    }

    private NodeUser createAndCacheUser(AppUserCreateDTO userCreateDto) {
        try {
            var request = userMapper.toGrpc(userCreateDto);
            AppUserResponse response = callWithRetry(() -> userServiceStub.getOrCreateAppUser(request));

            NodeUser nodeUser = userMapper.fromGrpc(response);
            nodeUser.setState(UserState.BASIC_STATE);
            cacheService.save(nodeUser);

            return nodeUser;
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    private NodeUser fetchAndCacheUser(Long telegramUserId) {
        try {
            TelegramUserIdRequest request = TelegramUserIdRequest.newBuilder()
                    .setTelegramUserId(telegramUserId)
                    .build();
            AppUserResponse response = callWithRetry(() -> userServiceStub.getAppUserByTelegramId(request));

            NodeUser nodeUser = userMapper.fromGrpc(response);
            cacheService.save(nodeUser);
            return nodeUser;

        } catch (Exception e) {
            throw mapException(e);
        }
    }

    @Retryable(
            retryFor = RetryableGrpcException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 300)
    )
    private <T> T callWithRetry(Supplier<T> action) {
        try {
            return action.get();
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            switch (code) {
                case UNAVAILABLE, DEADLINE_EXCEEDED, RESOURCE_EXHAUSTED -> {
                    throw new RetryableGrpcException("Retryable GRPC error: " + code);
                }
                default -> throw e;
            }
        }
    }

    private RuntimeException mapException(Exception e) {
        if (e instanceof RetryableGrpcException) {
            return new UserServiceUnavailableException("User service unavailable", e);
        }
        if (e instanceof StatusRuntimeException sre) {
            return new UserServiceProcessingException("User service error: " + sre.getStatus(), e);
        }
        return new NodeInternalProcessingException("Unexpected error in user service", e);
    }
}
