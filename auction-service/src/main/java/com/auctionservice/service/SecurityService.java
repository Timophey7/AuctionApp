package com.auctionservice.service;

import com.auctionservice.models.AuthenticationRequest;
import com.auctionservice.models.User;

public interface SecurityService {

    void sendUserDataToSecurityService(User user) throws Exception;

    boolean verifyUser(AuthenticationRequest authenticationRequest);

}
