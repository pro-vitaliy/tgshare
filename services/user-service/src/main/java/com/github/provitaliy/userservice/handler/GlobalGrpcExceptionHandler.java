package com.github.provitaliy.userservice.handler;

import com.github.provitaliy.userservice.exception.UserNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(UserNotFoundException.class)
    public StatusRuntimeException handleUserNotFoundException(UserNotFoundException ex) {
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleUnknown(Exception ex) {
        log.error("Unexpected error in gRPC service", ex);
        return Status.INTERNAL
                .withDescription("Internal server error")
                .asRuntimeException();
    }
}
