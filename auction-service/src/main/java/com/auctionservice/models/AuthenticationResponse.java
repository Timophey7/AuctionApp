package com.auctionservice.models;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String email;
    private String password;

}
