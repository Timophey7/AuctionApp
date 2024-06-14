package com.notificationservice.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Message implements Serializable {

    private String winnerEmail;
    private String productName;
    private double productPrice;
    private String uniqueCode;
    private boolean isOwner;


}
