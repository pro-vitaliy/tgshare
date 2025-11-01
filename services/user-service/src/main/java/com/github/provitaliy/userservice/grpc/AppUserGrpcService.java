package com.github.provitaliy.userservice.grpc;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.AppUserDTO;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.userservice.mapper.AppUserGrpcMapper;
import com.github.provitaliy.userservice.service.AppUserService;
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
}
