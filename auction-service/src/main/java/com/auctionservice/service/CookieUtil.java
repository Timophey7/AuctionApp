package com.auctionservice.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieUtil {

    void createCookie(HttpServletResponse response, String name, String value, int maxAge);
    String getCookieValue(HttpServletRequest request, String name);

}
