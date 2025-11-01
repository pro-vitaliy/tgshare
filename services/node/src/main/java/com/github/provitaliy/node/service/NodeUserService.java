package com.github.provitaliy.node.service;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.node.mapper.NodeUserMapper;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void changeState(NodeUser nodeUser, UserState state) {
        nodeUser.setState(state);
        cacheService.save(nodeUser);
    }

    public void setEmail(NodeUser nodeUser, String email) {
        var emailEnteredEvent = new UserEmailEnteredEvent(nodeUser.getId(), email);
        producerService.produceRegistrationMail(emailEnteredEvent);
    }

    private NodeUser createAndCacheUser(AppUserCreateDTO userCreateDto) {

//        TODO: добавить ретраи, обработку сетевых ошибок

        var request = userMapper.toGrpc(userCreateDto);
        AppUserResponse response = userServiceStub.getOrCreateAppUser(request);

        NodeUser nodeUser = userMapper.fromGrpc(response);
        nodeUser.setState(UserState.BASIC_STATE);
        cacheService.save(nodeUser);

        return nodeUser;
    }
}
