package com.github.provitaliy.service;

import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.enums.UserState;
import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.QueueNames;
import com.github.provitaliy.messaging.RoutingKeys;
import com.github.provitaliy.service.constants.BotResponses;
import com.github.provitaliy.service.testConfig.TestRabbitConfig;
import com.github.provitaliy.service.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestRabbitConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class TextMessageScenarioTest extends AbstractIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserAppDAO userAppDAO;

    private final Long tgUserFakeId = 100L;

    @BeforeEach
    void beforeEach() {
        userAppDAO.deleteAll();
    }

    @Test
    void shouldProduceUnknownCommandAnswer() throws Exception {
        Long chatFakeId = 10L;
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, "unknown command", chatFakeId);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actual = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);

        String expectedAnswer = BotResponses.UNKNOWN_RESPONSE;
        assertNotNull(actual);
        assertEquals(chatFakeId, Long.parseLong(actual.getChatId()));
        assertEquals(expectedAnswer, actual.getText());
    }

    @Test
    void shouldSaveUserAndProduceHelpAnswer() throws Exception {
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, "/help");

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actual = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);

        Optional<AppUser> expectedAppUser = userAppDAO.findByTelegramUserId(tgUserFakeId);
        assertNotNull(actual);
        assertTrue(expectedAppUser.isPresent());
        assertEquals(BotResponses.HELP_RESPONSE, actual.getText());
    }

    @Test
    void shouldProcessCancelCommand() throws Exception {
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, "/cancel");
        AppUser appUser = TestDataFactory.createNotRegisteredAppUser(tgUserFakeId, UserState.WAIT_FOR_EMAIL_STATE);
        userAppDAO.save(appUser);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actual = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
        Optional<AppUser> updatedAppUser = userAppDAO.findByTelegramUserId(tgUserFakeId);

        assertNotNull(actual);
        assertTrue(updatedAppUser.isPresent());
        assertEquals(UserState.BASIC_STATE, updatedAppUser.get().getUserState());
        assertEquals(BotResponses.CANCEL_RESPONSE, actual.getText());
    }

    @Test
    void shouldProcessRegistrationIfNotRegistered() throws Exception {
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, "/registration");
        AppUser appUser = TestDataFactory.createNotRegisteredAppUser(tgUserFakeId, UserState.BASIC_STATE);
        userAppDAO.save(appUser);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actual = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
        Optional<AppUser> updatedAppUser = userAppDAO.findByTelegramUserId(tgUserFakeId);

        assertNotNull(actual);
        assertTrue(updatedAppUser.isPresent());
        assertEquals(UserState.WAIT_FOR_EMAIL_STATE, updatedAppUser.get().getUserState());
        assertEquals(BotResponses.WAIT_FOR_EMAIL_RESPONSE, actual.getText());
    }

    @Test
    void shouldProcessRegistrationIfAlreadyRegistered() throws Exception {
        String testEmail = "email@test.io";
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, "/registration");
        AppUser appUser = TestDataFactory.createRegisteredAppUser(tgUserFakeId, testEmail, UserState.BASIC_STATE);
        userAppDAO.save(appUser);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actual = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
        Optional<AppUser> updatedAppUser = userAppDAO.findByTelegramUserId(tgUserFakeId);

        assertNotNull(actual);
        assertTrue(updatedAppUser.isPresent());
        assertEquals(UserState.BASIC_STATE, updatedAppUser.get().getUserState());
        assertEquals(BotResponses.ALREADY_REGISTERED_RESPONSE, actual.getText());
    }

    @Test
    void shouldProcessValidEmail() throws Exception {
        String testEmail = "email@test.io";
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, testEmail);
        AppUser appUser = TestDataFactory.createNotRegisteredAppUser(tgUserFakeId, UserState.WAIT_FOR_EMAIL_STATE);
        userAppDAO.save(appUser);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message answerMessage = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        Message mailParamsMessage = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.REGISTRATION_MAIL_QUEUE), Objects::nonNull);

        SendMessage actualAnswer = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(answerMessage);
        MailParams actualParams = (MailParams) rabbitTemplate.getMessageConverter().fromMessage(mailParamsMessage);
        Optional<AppUser> updatedAppUser = userAppDAO.findByTelegramUserId(tgUserFakeId);

        assertNotNull(actualAnswer);
        assertNotNull(actualParams);
        assertTrue(updatedAppUser.isPresent());
        assertEquals(testEmail, updatedAppUser.get().getUnconfirmedEmail());
        assertEquals(BotResponses.EMAIL_CONFIRMATION_RESPONSE, actualAnswer.getText());
    }

    @Test
    void shouldProcessAlreadyExistsEmail() throws Exception {
        String testEmail = "email@test.io";
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, testEmail);
        AppUser appUser = TestDataFactory.createNotRegisteredAppUser(tgUserFakeId, UserState.WAIT_FOR_EMAIL_STATE);
        Long existsUserFakeId = 20L;
        AppUser existsAppUser = TestDataFactory.createRegisteredAppUser(
                existsUserFakeId,
                testEmail,
                UserState.BASIC_STATE
        );
        userAppDAO.save(appUser);
        userAppDAO.save(existsAppUser);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actualAnswer = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
        Optional<AppUser> updatedAppUser = userAppDAO.findByTelegramUserId(tgUserFakeId);

        assertNotNull(actualAnswer);
        assertTrue(updatedAppUser.isPresent());
        assertEquals(BotResponses.EMAIL_ALREADY_EXISTS, actualAnswer.getText());
        assertNotEquals(testEmail, updatedAppUser.get().getUnconfirmedEmail());
    }

    @Test
    void shouldProcessNotValidEmail() throws Exception {
        String testEmail = "incorrect@email";
        Update update = TestDataFactory.createUpdateWithTextMessage(tgUserFakeId, testEmail);
        AppUser appUser = TestDataFactory.createNotRegisteredAppUser(tgUserFakeId, UserState.WAIT_FOR_EMAIL_STATE);
        userAppDAO.save(appUser);

        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );

        Message message = Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> rabbitTemplate.receive(QueueNames.ANSWER_MESSAGE_QUEUE), Objects::nonNull);

        SendMessage actualAnswer = (SendMessage) rabbitTemplate.getMessageConverter().fromMessage(message);

        assertNotNull(actualAnswer);
        assertEquals(BotResponses.INCORRECT_EMAIL_ANSWER, actualAnswer.getText());
    }
}
