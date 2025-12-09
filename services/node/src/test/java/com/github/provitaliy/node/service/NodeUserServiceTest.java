package com.github.provitaliy.node.service;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.AppUserServiceGrpc;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.common.grpc.TelegramUserIdRequest;
import com.github.provitaliy.node.exception.UserServiceProcessingException;
import com.github.provitaliy.node.exception.UserServiceUnavailableException;
import com.github.provitaliy.node.mapper.NodeUserMapper;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import com.github.provitaliy.node.util.TestUtils;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeUserServiceTest {

    @InjectMocks
    private NodeUserService nodeUserService;

    @Spy
    private NodeUserMapper userMapper;

    @Mock
    private AppUserServiceGrpc.AppUserServiceBlockingStub userServiceStub;

    @Mock
    private NodeUserCacheService cacheService;

    @Mock
    private ProducerService producerService;

    @Captor
    ArgumentCaptor<NodeUser> nodeUserCaptor;

    private NodeUser testUser;

    @BeforeEach
    void setUp() {
        testUser = TestUtils.getActivatedNodeUser();
    }

    @Test
    void shouldCreateAndCacheUser() {
        //given
        AppUserCreateDTO userCreateDto = TestUtils.getAppUserCreateDto(testUser);
        AppUserResponse appUserResponse = TestUtils.getAppUserResponse(testUser);
        when(cacheService.findByTelegramId(userCreateDto.getTelegramUserId()))
                .thenReturn(Optional.empty());
        when(userServiceStub.getOrCreateAppUser(any(GetOrCreateAppUserRequest.class)))
                .thenReturn(appUserResponse);

        // when
        nodeUserService.getOrCreateAppUser(userCreateDto);

        // then
        verify(cacheService).save(nodeUserCaptor.capture());
        NodeUser savedUser = nodeUserCaptor.getValue();

        assertEquals(userCreateDto.getTelegramUserId(), savedUser.getTelegramUserId());
        assertEquals(userCreateDto.getChatId(), savedUser.getChatId());
        assertEquals(userCreateDto.getFirstName(), savedUser.getFirstName());
        assertEquals(UserState.BASIC_STATE, savedUser.getState());
    }

    @Test
    void shouldThrowsExceptionWhenUserServiceUnavailable() {
        //given
        AppUserCreateDTO userCreateDto = TestUtils.getAppUserCreateDto(testUser);
        AppUserResponse appUserResponse = TestUtils.getAppUserResponse(testUser);
        when(cacheService.findByTelegramId(userCreateDto.getTelegramUserId()))
                .thenReturn(Optional.empty());
        when(userServiceStub.getOrCreateAppUser(any(GetOrCreateAppUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        // when & then
        assertThrows(UserServiceUnavailableException.class, () ->
                nodeUserService.getOrCreateAppUser(userCreateDto));
    }

    @Test
    void shouldThrowsExceptionWhenUserServiceInternalError() {
        //given
        AppUserCreateDTO userCreateDto = TestUtils.getAppUserCreateDto(testUser);
        AppUserResponse appUserResponse = TestUtils.getAppUserResponse(testUser);
        when(cacheService.findByTelegramId(userCreateDto.getTelegramUserId()))
                .thenReturn(Optional.empty());
        when(userServiceStub.getOrCreateAppUser(any(GetOrCreateAppUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // when & then
        assertThrows(UserServiceProcessingException.class, () ->
                nodeUserService.getOrCreateAppUser(userCreateDto));
    }

    @Test
    void shouldGetAndCacheUserByTelegramId() {
        // given
        AppUserResponse appUserResponse = TestUtils.getAppUserResponse(testUser);
        when(cacheService.findByTelegramId(testUser.getTelegramUserId()))
                .thenReturn(Optional.empty());
        when(userServiceStub.getAppUserByTelegramId(any(TelegramUserIdRequest.class)))
                .thenReturn(appUserResponse);

        // when
        nodeUserService.getByTelegramUserId(testUser.getTelegramUserId());

        // then
        verify(cacheService).save(nodeUserCaptor.capture());
        NodeUser savedUser = nodeUserCaptor.getValue();
        assertEquals(testUser.getTelegramUserId(), savedUser.getTelegramUserId());
        assertEquals(testUser.getChatId(), savedUser.getChatId());
        assertEquals(testUser.getFirstName(), savedUser.getFirstName());
        assertEquals(UserState.BASIC_STATE, savedUser.getState());
    }

    @Test
    void shouldChangeStateAndCache() {
        // when
        nodeUserService.changeState(testUser, UserState.WAIT_FOR_EMAIL_STATE);

        // then
        verify(cacheService).save(nodeUserCaptor.capture());
        NodeUser savedUser = nodeUserCaptor.getValue();
        assertEquals(UserState.WAIT_FOR_EMAIL_STATE, savedUser.getState());
    }

    @Test
    void shouldProduceEmailEnteredEvent() {
        // given
        String email = "email@ex.io";
        // when
        nodeUserService.setEmail(testUser, email);
        // then
        verify(producerService).produceRegistrationMail(any(UserEmailEnteredEvent.class));
    }
}
