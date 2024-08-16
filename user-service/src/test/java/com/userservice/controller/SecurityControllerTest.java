package com.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.model.AuthenticationRequest;
import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.reposiroty.UserRepository;
import com.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;

    private User user;
    private AuthenticationRequest authentication;
    @BeforeEach
    void setUp() {
        authentication = new AuthenticationRequest();
        authentication.setEmail("email");
        authentication.setPassword("password");
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        user.setPhone("phone");
        user.setRole(Role.USER);
        user.setAddress("address");
        user.setId(1);
        user.setFirstName("Jon");
        user.setLastName("Mutex");
        user.setNumberOfCard("1231231123");
    }

    @Test
    void registerUser_Created() throws Exception {

        doNothing().when(userService).saveUser(user);

        ResultActions perform = mockMvc.perform(post("/v1/security/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)));

        perform.andExpect(status().isCreated())
                .andExpect(content().string("success"));

    }

    @Test
    void registerUser_BadRequest() throws Exception {

        User notValidUser = new User();

        ResultActions perform = mockMvc.perform(post("/v1/security/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValidUser)));

        perform.andExpect(status().isBadRequest());
    }

    @Test
    void getUserByEmail_Ok() throws Exception {

        String email = "email";
        when(userService.findUserByEmail(email)).thenReturn(user);

        ResultActions perform = mockMvc.perform(get("/v1/security/getUserByEmail/" + email));

        perform.andExpect(status().isOk());
        verify(userService).findUserByEmail(email);


    }

    @Test
    void verifyUserShouldReturnVerified() throws Exception {

        when(userRepository.findUserByEmail(authentication.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authentication.getPassword(), user.getPassword())).thenReturn(true);

        ResultActions perform = mockMvc.perform(post("/v1/security/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authentication)));

        perform.andExpect(status().isOk())
        .andExpect(content().string("verified"));

    }

    @Test
    void verifyUserShouldReturnUserNotFound() throws Exception {

        when(userRepository.findUserByEmail(authentication.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.matches(authentication.getPassword(), user.getPassword())).thenReturn(true);

        ResultActions perform = mockMvc.perform(post("/v1/security/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authentication)));

        perform.andExpect(status().isOk())
                .andExpect(content().string("user not found"));

    }

    @Test
    void verifyUserShouldReturnWrongPassword() throws Exception {

        when(userRepository.findUserByEmail(authentication.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authentication.getPassword(), user.getPassword())).thenReturn(false);

        ResultActions perform = mockMvc.perform(post("/v1/security/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authentication)));

        perform.andExpect(status().isOk())
                .andExpect(content().string("wrong password"));

    }
}