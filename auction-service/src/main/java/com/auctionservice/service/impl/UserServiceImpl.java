package com.auctionservice.service.impl;

import com.auctionservice.models.User;
import com.auctionservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final WebClient.Builder webClientBuilder;

    @Override
    public User findUserByEmail(String email) {
        User user = webClientBuilder.build().get().uri("http://user-service/v1/security/getUserByEmail/" + email)
                .retrieve().bodyToMono(User.class).block();
        log.info(user.toString());
        return user;
    }
}
