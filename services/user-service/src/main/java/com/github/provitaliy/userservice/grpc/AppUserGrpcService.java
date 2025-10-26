package com.github.provitaliy.userservice.grpc;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.AppUserDTO;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.common.grpc.UpdateUnconfirmedEmailRequest;
import com.github.provitaliy.userservice.exception.EmailAlreadyTakenException;
import com.github.provitaliy.userservice.exception.UserNotFoundException;
import com.github.provitaliy.userservice.mapper.AppUserGrpcMapper;
import com.github.provitaliy.userservice.service.AppUserService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@RequiredArgsConstructor
@GrpcService
public class AppUserGrpcService extends AppUserServiceGrpc.AppUserServiceImplBase {
    private final AppUserService appUserService;
    private final AppUserGrpcMapper grpcMapper;

    @Override
    public void getOrCreateAppUser(GetOrCreateAppUserRequest request,
                                   StreamObserver<AppUserResponse> responseObserver) {
        AppUserCreateDTO appUserData = grpcMapper.fromGrpc(request);
        AppUserDTO appUser = appUserService.getOrCreateAppUser(appUserData);
        AppUserResponse response = grpcMapper.toGrpc(appUser);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUnconfirmedEmail(UpdateUnconfirmedEmailRequest request,
                                       StreamObserver<Empty> responseObserver) {
        try {
            appUserService.updateUnconfirmedEmail(request.getTelegramUserId(), request.getEmail());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (EmailAlreadyTakenException e) {
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
        } catch (UserNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Unexpected error").asRuntimeException());
        }
    }
}
