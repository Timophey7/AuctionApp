package com.auctionservice.service;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AuctionService {

    List<AuctionInfo> getValidAuctions();

    void saveAuction(AuctionInfo auctionInfo);

    boolean validPrice(String uniqueCode, double bidPrice);

    void setNewPrice(String uniqueCode, Bid bid,String email);

    boolean startAuctionTime(LocalDate startDate, LocalTime startTime);

}
