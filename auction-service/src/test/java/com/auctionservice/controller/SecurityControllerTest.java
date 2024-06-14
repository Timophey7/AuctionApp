package com.auctionservice.controller;

import com.auctionservice.models.User;
import com.auctionservice.service.CookieUtil;
import jakarta.ws.rs.core.MediaType;
import org.jose4j.jwk.Use;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SecurityController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CookieUtil cookieUtil;
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email@email.com");
    }

    @Test
    void registration() throws Exception {

        ResultActions perform = mockMvc.perform(get("/v1/security/registration"));

        perform.andExpect(status().isOk())
                .andExpect(model().attributeExists("user"));

    }

    @Test
    void sendUserInfo() throws Exception {

    }

    @Test
    void login() throws Exception {

        ResultActions perform = mockMvc.perform(get("/v1/security/login"));

        perform.andExpect(status().isOk())
                .andExpect(model().attributeExists("authentication"));

    }

    @Test
    void verification() {


    }
}