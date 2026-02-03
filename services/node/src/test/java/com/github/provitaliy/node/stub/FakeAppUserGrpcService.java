package com.github.provitaliy.node.stub;

import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;

import java.time.Instant;

public class FakeAppUserGrpcService extends AppUserServiceGrpc.AppUserServiceImplBase {

    private static final ThreadLocal<Boolean> userStatus = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static void setUserStatus(Boolean status) {
        userStatus.set(status);
    }

    public static void clearUserStatus() {
        userStatus.remove();
    }

    @Override
    public void getOrCreateAppUser(GetOrCreateAppUserRequest request, StreamObserver<AppUserResponse> responseObserver) {
        AppUserResponse response = AppUserResponse.newBuilder()
                .setId(1L)
                .setTelegramUserId(request.getTelegramUserId())
                .setChatId(request.getChatId())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setUsername(request.getUsername())
                .setEmail("test@test.com")
                .setIsActive(userStatus.get())
                .setFirstLoginDate(
                        Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond())
                                .build()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
