package com.userservice.model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.util.Date;

@Data
public class AuctionInfo {

    private int id;
    private String title;
    private String description;
    private int price;
    private Date startDate;
    private Date endDate;

}
