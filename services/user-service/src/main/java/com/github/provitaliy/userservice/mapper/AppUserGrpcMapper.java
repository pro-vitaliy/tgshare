package com.github.provitaliy.userservice.mapper;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.AppUserDTO;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class AppUserGrpcMapper {

    public AppUserCreateDTO fromGrpc(GetOrCreateAppUserRequest request) {
        return AppUserCreateDTO.builder()
                .telegramUserId(request.getTelegramUserId())
                .chatId(request.getChatId())
                .firstName(request.getFirstName().isEmpty() ? null : request.getFirstName())
                .lastName(request.getLastName().isEmpty() ? null : request.getLastName())
                .username(request.getUsername().isEmpty() ? null : request.getUsername())
                .build();
    }

    public AppUserResponse toGrpc(AppUserDTO appUserData) {
        return AppUserResponse.newBuilder()
                .setId(appUserData.getId())
                .setTelegramUserId(appUserData.getTelegramUserId())
                .setChatId(appUserData.getChatId())
                .setFirstName(appUserData.getFirstName() == null ? "" : appUserData.getFirstName())
                .setLastName(appUserData.getLastName() == null ? "" : appUserData.getLastName())
                .setUsername(appUserData.getUsername() == null ? "" : appUserData.getUsername())
                .setEmail(appUserData.getEmail() == null ? "" : appUserData.getEmail())
                .setIsActive(appUserData.getIsActive() != null && appUserData.getIsActive())
                .setFirstLoginDate(toTimestamp(appUserData.getFirstLoginDate()))
                .build();
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();
    }
}
