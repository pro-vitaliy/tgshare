package com.github.provitaliy.node.configuration;

import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class GrpcClientConfig {

    @Value("${user-service.host}")
    private String userServiceHost;

    @Value("${user-service.port}")
    private int userServicePort;

    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public AppUserServiceGrpc.AppUserServiceBlockingStub appUserServiceBlockingStub(ManagedChannel channel) {
        return AppUserServiceGrpc.newBlockingStub(channel);
    }
}
