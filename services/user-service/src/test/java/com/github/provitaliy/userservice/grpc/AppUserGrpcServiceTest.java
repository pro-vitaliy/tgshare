package com.github.provitaliy.userservice.grpc;

import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.common.grpc.TelegramUserIdRequest;
import com.github.provitaliy.userservice.entity.AppUser;
import com.github.provitaliy.userservice.repository.AppUserRepository;
import com.github.provitaliy.userservice.service.ConsumerService;
import com.github.provitaliy.userservice.util.TestUtils;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "grpc.server.inprocess-name=test-grpc")
class AppUserGrpcServiceTest {

    private ManagedChannel managedChannel;

    @Autowired
    private AppUserRepository repository;

    @MockitoBean
    private ConsumerService consumerService;

    @Value("${grpc.server.port}")
    private int grpcServerPort;

    @BeforeAll
    void setUp() {
        managedChannel = InProcessChannelBuilder
                .forName("test-grpc")
                .directExecutor()
                .build();
    }

    @AfterAll
    void tearDown() {
        managedChannel.shutdownNow();
    }

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
    }

    @Test
    void shouldGetOrCreateAppUser() {
        AppUserServiceGrpc.AppUserServiceBlockingStub stub =
                AppUserServiceGrpc.newBlockingStub(managedChannel);

        GetOrCreateAppUserRequest request = TestUtils.randomGetOrCreateAppUserRequest();
        AppUserResponse response = stub.getOrCreateAppUser(request);
        AppUser expectedAppUser = repository.findByTelegramUserId(request.getTelegramUserId()).orElseThrow();

        assertEquals(request.getChatId(), expectedAppUser.getChatId());
        assertEquals(request.getTelegramUserId(), response.getTelegramUserId());
        assertEquals(request.getFirstName(), response.getFirstName());
        assertFalse(response.getIsActive());
        assertNull(expectedAppUser.getUnconfirmedEmail());
    }

    @Test
    void shouldGetAppUserByTelegramId() {
        AppUserServiceGrpc.AppUserServiceBlockingStub stub =
                AppUserServiceGrpc.newBlockingStub(managedChannel);

        AppUser appUser = TestUtils.randomAppUser();
        repository.save(appUser);

        TelegramUserIdRequest request = TelegramUserIdRequest.newBuilder()
                .setTelegramUserId(appUser.getTelegramUserId())
                .build();

        AppUserResponse response = stub.getAppUserByTelegramId(request);

        assertEquals(appUser.getTelegramUserId(), response.getTelegramUserId());
        assertEquals(appUser.getIsActive(), response.getIsActive());
        assertEquals(appUser.getChatId(), response.getChatId());
        assertEquals(appUser.getFirstName(), response.getFirstName());
    }
}
