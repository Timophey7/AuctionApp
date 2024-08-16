package com.auctionservice.service.impl;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import com.auctionservice.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
public class AuctionServiceImpl implements AuctionService {

    AuctionInfoRepository auctionInfoRepository;
    BidRepository bidRepository;

    @Override
    public List<AuctionInfo> getValidAuctions() {
        List<AuctionInfo> validAuctions = new ArrayList<>();
        auctionInfoRepository.findAll().forEach(auctionInfo -> {
            if(auctionInfo.getIsValid() && startAuctionTime(auctionInfo.getStartDate(),auctionInfo.getStartTime())){
                validAuctions.add(auctionInfo);
            }
        });
        return validAuctions;
    }

    @Override
    public void saveAuction(AuctionInfo auction) {
        auction.setUniqueCode(generateHash());
        auctionInfoRepository.save(auction);
    }

    private String generateHash() {
        byte[] randomBytes = new byte[6];
        new Random().nextBytes(randomBytes);
        String base64Encoded = Base64.getEncoder().encodeToString(randomBytes);
        return base64Encoded.substring(0, 8);
    }

    @Override
    public boolean validPrice(String uniqueCode, double bidPrice) {
        AuctionInfo auctionInfoByUniqueCode = auctionInfoRepository.findAuctionInfoByUniqueCode(uniqueCode);
        return bidPrice > auctionInfoByUniqueCode.getPrice() ? true : false;
    }

    @Override
    public void setNewPrice(String uniqueCode, Bid bid,String email) {
        AuctionInfo auctionInfoByUniqueCode = auctionInfoRepository.findAuctionInfoByUniqueCode(uniqueCode);
        bid.setUniqueCode(uniqueCode);
        bid.setCustomerEmail(email);
        bidRepository.save(bid);
        auctionInfoByUniqueCode.setPrice(bid.getNewBid());
        auctionInfoRepository.save(auctionInfoByUniqueCode);
    }

    @Override
    public boolean startAuctionTime(LocalDate startDate, LocalTime startTime) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime now = LocalDateTime.now();
        if (startDateTime.isBefore(now)) {
            return true;
        }
        return false;
    }
}
