package com.auctionservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieUtilImplTest {

    @InjectMocks
    private CookieUtilImpl cookieUtil;

    @Test
    void createCookie() {
        String cookieName = "email";
        String cookieValue = "test";
        int maxAge = 24 * 60 * 60;

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        cookieUtil.createCookie(response, cookieName, cookieValue, maxAge);

        Mockito.verify(response, times(1)).addCookie(argThat(cookie ->
                cookie.getName().equals(cookieName) &&
                        cookie.getValue().equals(cookieValue) &&
                        cookie.getMaxAge() == maxAge &&
                        cookie.getPath().equals("/")
        ));

    }

    @Test
    void getCookieValue() {

        String name = "test";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        String cookieValue = cookieUtil.getCookieValue(request, name);

        verify(request).getCookies();
    }
}