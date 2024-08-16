package com.auctionservice.service.impl;

import com.auctionservice.models.AuthenticationRequest;
import com.auctionservice.models.User;
import com.auctionservice.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class SecurityServiceImpl implements SecurityService {

    private final WebClient.Builder webClientBuilder;

    @Override
    public void sendUserDataToSecurityService(User user) throws Exception{
        webClientBuilder.build().post().uri("http://user-service/v1/security/register")
                .bodyValue(user).retrieve().bodyToMono(String.class).block();
    }

    @Override
    public boolean verifyUser(AuthenticationRequest authenticationRequest) {
        String infoString = webClientBuilder.build().post().uri("http://user-service/v1/security/verify")
                .bodyValue(authenticationRequest).retrieve().bodyToMono(String.class).block();
        return infoString.equals("verified") ? true : false;
    }
}
