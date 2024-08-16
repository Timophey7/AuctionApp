package com.auctionservice.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class User{

    private long id;
    @NotBlank
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;
    @NotBlank
    private String address;
    @NotBlank
    private String numberOfCard;

}
