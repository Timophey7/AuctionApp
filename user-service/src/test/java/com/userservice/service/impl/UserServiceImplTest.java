package com.userservice.service.impl;

import com.userservice.model.User;
import com.userservice.reposiroty.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserServiceImpl userService;

    User user;
    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test@gmail.com");
        user.setPhone("phone");
    }

    @Test
    void saveUser() {

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);

    }

    @Test
    void findUserByEmail_Success(){
        String email = "test@gmail.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        User userByEmail = userService.findUserByEmail(email);

        verify(userRepository,times(1)).findUserByEmail(email);
        assertEquals(user,userByEmail);

    }

    @Test
    void findUserByEmail_UsernameNotFoundException(){
        String email = "test@gmail.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class, () -> {
            userService.findUserByEmail(email);
        });

        assertEquals(usernameNotFoundException.getMessage(),"user not found");


    }
}