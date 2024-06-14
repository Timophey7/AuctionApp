package com.userservice.model;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String email;
    private String password;

}
