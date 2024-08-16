package com.auctionservice.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
