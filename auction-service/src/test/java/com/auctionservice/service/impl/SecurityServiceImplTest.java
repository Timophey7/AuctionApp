package com.auctionservice.service.impl;

import com.auctionservice.models.AuthenticationRequest;
import com.auctionservice.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @InjectMocks
    SecurityServiceImpl securityService;

    @Mock
    WebClient.Builder webClientBuilder;

    @Test
    void sendUserDataToSecurityService() throws Exception {

        User user = new User();
        user.setId(1);
        user.setEmail("test@gmail.com");

        WebClient mockWebClient = mock(WebClient.class);
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec mockUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("http://user-service/v1/security/register"))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(user)).thenReturn(mockUriSpec);
        when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just("exists"));

        when(webClientBuilder.build()).thenReturn(mockWebClient);

        securityService.sendUserDataToSecurityService(user);


    }

    @Test
    void verifyUser_True() {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@gmail.com");
        authenticationRequest.setPassword("werty123");

        WebClient mockWebClient = mock(WebClient.class);
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec mockUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("http://user-service/v1/security/verify"))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(authenticationRequest)).thenReturn(mockUriSpec);
        when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just("verified"));

        when(webClientBuilder.build()).thenReturn(mockWebClient);

        boolean verifyUser = securityService.verifyUser(authenticationRequest);

        assertTrue(verifyUser);

    }

    @Test
    void verifyUser_False() {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@gmail.com");
        authenticationRequest.setPassword("werty123");

        WebClient mockWebClient = mock(WebClient.class);
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec mockUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("http://user-service/v1/security/verify"))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(authenticationRequest)).thenReturn(mockUriSpec);
        when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just("error"));

        when(webClientBuilder.build()).thenReturn(mockWebClient);

        boolean verifyUser = securityService.verifyUser(authenticationRequest);

        assertFalse(verifyUser);

    }
}