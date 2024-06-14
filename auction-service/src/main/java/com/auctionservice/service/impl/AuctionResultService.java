package com.auctionservice.service.impl;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.models.Message;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionResultService {

    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final AuctionInfoRepository auctionInfoRepository;
    private final BidRepository bidRepository;

    @Scheduled(fixedRate = 60000)
    public void auctionResult() {
        List<AuctionInfo> allAuctions = auctionInfoRepository.findAll();
        for (AuctionInfo auctionInfo : allAuctions) {
            LocalDateTime localDateTime = LocalDateTime.of(auctionInfo.getEndDate(), auctionInfo.getEndTime());
            if (localDateTime.isBefore(LocalDateTime.now()) && auctionInfo.getIsValid()) {
                Bid winingBid = bidRepository.findWiningBid(auctionInfo.getUniqueCode());
                Message message = getMessageForWinner(auctionInfo.getTitle(),
                        winingBid.getCustomerEmail(), winingBid.getNewBid(), winingBid.getUniqueCode());
                Message messageForOwner = getMessageForOwner(auctionInfo.getTitle(),
                        auctionInfo.getOwnerEmail(), winingBid.getNewBid(), winingBid.getUniqueCode());
                log.info(message.toString());
                auctionInfo.setIsValid(false);
                auctionInfoRepository.save(auctionInfo);
                kafkaTemplate.send("email", message);
                kafkaTemplate.send("email", messageForOwner);
            }
        }
    }

    private Message getMessageForWinner(String productName, String email, Double price, String uniqueCode) {
        Message message = new Message();
        message.setWinnerEmail(email);
        message.setProductName(productName);
        message.setProductPrice(price);
        message.setUniqueCode(uniqueCode);
        message.setOwner(false);
        return message;
    }

    private Message getMessageForOwner(String productName, String email, Double price, String uniqueCode) {
        Message message = new Message();
        message.setWinnerEmail(email);
        message.setProductName(productName);
        message.setProductPrice(price);
        message.setUniqueCode(uniqueCode);
        message.setOwner(true);
        return message;
    }

}
