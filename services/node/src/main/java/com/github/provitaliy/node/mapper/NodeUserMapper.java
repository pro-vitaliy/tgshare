package com.github.provitaliy.node.mapper;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import org.springframework.stereotype.Component;

@Component
public class NodeUserMapper {
    public GetOrCreateAppUserRequest toGrpc(AppUserCreateDTO userDto) {
        return GetOrCreateAppUserRequest.newBuilder()
                .setTelegramUserId(userDto.getTelegramUserId())
                .setChatId(userDto.getChatId())
                .setFirstName(userDto.getFirstName() == null ? "" : userDto.getFirstName())
                .setLastName(userDto.getLastName() == null ? "" : userDto.getLastName())
                .setUsername(userDto.getUsername() == null ? "" : userDto.getUsername())
                .build();
    }

    public NodeUser fromGrpc(AppUserResponse appUserResponse) {
        return NodeUser.builder()
                .telegramUserId(appUserResponse.getTelegramUserId())
                .chatId(appUserResponse.getChatId())
                .firstName(appUserResponse.getFirstName())
                .lastName(appUserResponse.getLastName())
                .username(appUserResponse.getUsername())
                .email(appUserResponse.getEmail())
                .isActive(appUserResponse.getIsActive())
                .state(UserState.BASIC_STATE)
                .build();
    }
}
