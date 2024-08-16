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
            if (isAuctionCompleted(auctionInfo)) {
                Bid winningBid = bidRepository.findWiningBid(auctionInfo.getUniqueCode());
                Message messageForWinner = createMessage(auctionInfo.getTitle(), winningBid, false);
                Message messageForOwner = createMessage(auctionInfo.getTitle(), winningBid, true);

                log.info(messageForWinner.toString());
                log.info(messageForOwner.toString());

                markAuctionAsCompleted(auctionInfo);

                kafkaTemplate.send("email", messageForWinner);
                kafkaTemplate.send("email", messageForOwner);
            }
        }
    }

    private boolean isAuctionCompleted(AuctionInfo auctionInfo) {
        LocalDateTime localDateTime = LocalDateTime.of(auctionInfo.getEndDate(), auctionInfo.getEndTime());
        return localDateTime.isBefore(LocalDateTime.now()) && auctionInfo.getIsValid();
    }

    private Message createMessage(String productName, Bid winningBid, boolean isOwner) {
        Message message = new Message();
        message.setWinnerEmail(winningBid.getCustomerEmail());
        message.setProductName(productName);
        message.setProductPrice(winningBid.getNewBid());
        message.setUniqueCode(winningBid.getUniqueCode());
        message.setOwner(isOwner);
        return message;
    }

    private void markAuctionAsCompleted(AuctionInfo auctionInfo) {
        auctionInfo.setIsValid(false);
        auctionInfoRepository.save(auctionInfo);
    }
}

