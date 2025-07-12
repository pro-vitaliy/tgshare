package com.github.provitaliy.controller;

import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.entity.AppUser;
import org.hashids.Hashids;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class ActivationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserAppDAO userAppDAO;

    @Autowired
    Hashids hashids;

    AppUser user;

    @BeforeEach
    void beforeEach() {
        userAppDAO.deleteAll();

        String unconfirmedEmail = "mail@ex.io";
        AppUser appUser = AppUser.builder()
                .unconfirmedEmail(unconfirmedEmail)
                .isActive(false)
                .build();

        user = userAppDAO.save(appUser);
    }

    @Test
    void shouldActivateUser() throws Exception {
        Long userId = user.getId();
        String unconfirmedEmail = user.getUnconfirmedEmail();
        String encodedUserId = hashids.encode(userId);

        var request = get("/user/activateUser")
                .param("id", encodedUserId);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("Регистрация успешно завершена!"))
                .andReturn();

        AppUser actualUser = userAppDAO.findById(userId).orElseThrow();

        assertThat(actualUser.getEmail()).isEqualTo(unconfirmedEmail);
        assertThat(actualUser.getUnconfirmedEmail()).isNull();
        assertThat(actualUser.getIsActive()).isTrue();
    }

    @Test
    void shouldReturn404() throws Exception {
        Long fakeId = 999L;
        String encodedId = hashids.encode(fakeId);

        var request = get("/user/activateUser")
                .param("id", encodedId);
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("Такого пользователя не существует."));
    }
}
