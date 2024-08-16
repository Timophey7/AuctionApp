package com.auctionservice.controller;

import com.auctionservice.models.AuthenticationRequest;
import com.auctionservice.models.User;
import com.auctionservice.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = SecurityController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @MockBean
    SecurityService securityService;

    User user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        user = new User();
        user.setId(1);
        user.setEmail("test@gmail.com");

    }

    @Test
    void registration() throws Exception {
        ResultActions perform = mockMvc.perform(get("/v1/auction/security/registration"));

        perform.andExpect(model().attribute("user",new User()));
    }

    @Test
    void sendUserInfo_BindingResultHasErrors() throws Exception {
        User user = new User();

        ResultActions perform = mockMvc.perform(
                post("/v1/auction/security/sendUserInfo")
                        .flashAttr("user",user)
        );

        perform
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/security/registration"));
    }

    @Test
    void sendUserInfo_Success() throws Exception {
        User user = new User();
        user.setId(1);
        user.setAddress("address");
        user.setPhone("phone");
        user.setPassword("password");
        user.setEmail("test@gmail.com");
        user.setFirstName("Jon");
        user.setLastName("Mutex");
        user.setUsername("jmaswdd");
        user.setNumberOfCard("12312312312");

        doNothing().when(securityService).sendUserDataToSecurityService(user);

        ResultActions perform = mockMvc.perform(
                post("/v1/auction/security/sendUserInfo")
                        .flashAttr("user",user)
        );

        perform
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/security/login"));
    }

    @Test
    void sendUserInfo_Exception() throws Exception {
        User user = new User();
        user.setId(1);
        user.setAddress("address");
        user.setPhone("phone");
        user.setPassword("password");
        user.setEmail("test@email.com");
        user.setFirstName("Jon");
        user.setLastName("Mutex");
        user.setUsername("jm");
        user.setNumberOfCard("12312312312");

        doThrow(Exception.class).when(securityService).sendUserDataToSecurityService(user);

        ResultActions perform = mockMvc.perform(
                post("/v1/auction/security/sendUserInfo")
                        .flashAttr("user",user)
        );

        perform
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/security/registration"));
    }

    @Test
    void login() throws Exception {
        ResultActions perform = mockMvc.perform(get("/v1/auction/security/login"));

        perform.andExpect(model().attribute("authentication",new AuthenticationRequest()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void verification_HasErrors() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("");
        authenticationRequest.setPassword("");

        ResultActions perform = mockMvc.perform(post("/v1/auction/security/verification")
                .flashAttr("authentication",authenticationRequest)
        );

        perform.andExpect(status().is3xxRedirection());

    }

    @Test
    void verification_Success() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@gmail.com");
        authenticationRequest.setPassword("testPassword");

        MockHttpSession session = new MockHttpSession();
        when(securityService.verifyUser(authenticationRequest)).thenReturn(true);

        ResultActions perform = mockMvc.perform(
                post("/v1/auction/security/verification")
                        .flashAttr("authentication",authenticationRequest)
                        .session(session)
        );

        perform
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/home"))
                .andExpect(request().sessionAttribute("email", "test@gmail.com"));
    }
}