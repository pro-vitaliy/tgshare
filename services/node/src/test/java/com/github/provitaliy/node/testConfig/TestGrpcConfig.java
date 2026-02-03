package com.github.provitaliy.node.testConfig;

import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.node.stub.FakeAppUserGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestGrpcConfig {
    private static final String USER_SERVICE_IN_PROCESS_NAME = "userServiceInProcess";

    @Bean(destroyMethod = "shutdownNow")
    public Server grpcServer() throws Exception {
//         IMPORTANT:
//         directExecutor is required so that FakeAppUserGrpcService ThreadLocal is
//         visible in test thread. Do not remove.
        return InProcessServerBuilder
                .forName(USER_SERVICE_IN_PROCESS_NAME)
                .directExecutor()
                .addService(new FakeAppUserGrpcService())
                .build()
                .start();
    }

    @Bean
    public ManagedChannel userServiceChannel() {
        return InProcessChannelBuilder
                .forName(USER_SERVICE_IN_PROCESS_NAME)
                .directExecutor()
                .build();
    }

    @Bean
    public AppUserServiceGrpc.AppUserServiceBlockingStub appUserServiceBlockingStub(ManagedChannel channel) {
        return AppUserServiceGrpc.newBlockingStub(channel);
    }
}
