package com.auctionservice.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class User{

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String numberOfCard;

}
