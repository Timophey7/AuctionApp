package com.auctionservice.service.impl;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.models.Message;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionResultServiceTest {

    @Mock
    KafkaTemplate<String, Message> kafkaTemplate;

    @Mock
    AuctionInfoRepository auctionInfoRepository;

    @Mock
    BidRepository bidRepository;

    @InjectMocks
    private AuctionResultService auctionResultService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void auctionResult_shouldSendEmailsForCompletedAuction() {
        AuctionInfo auctionInfo = new AuctionInfo();
        auctionInfo.setUniqueCode("12345");
        auctionInfo.setTitle("Product");
        auctionInfo.setEndDate(LocalDateTime.now().minusDays(1).toLocalDate());
        auctionInfo.setEndTime(LocalDateTime.now().minusDays(1).toLocalTime());
        auctionInfo.setIsValid(true);

        Bid winningBid = new Bid();
        winningBid.setCustomerEmail("winner@email.com");
        winningBid.setNewBid(100.0);
        winningBid.setUniqueCode(auctionInfo.getUniqueCode());

        when(auctionInfoRepository.findAll()).thenReturn(List.of(auctionInfo));
        when(bidRepository.findWiningBid(auctionInfo.getUniqueCode())).thenReturn(winningBid);

        auctionResultService.auctionResult();


        verify(auctionInfoRepository, times(1)).findAll();
        verify(bidRepository, times(1)).findWiningBid(auctionInfo.getUniqueCode());
    }

    @Test
    void auctionResult_shouldNotSendEmailsForNonCompletedAuction() {
        AuctionInfo auctionInfo = new AuctionInfo();
        auctionInfo.setUniqueCode("12345");
        auctionInfo.setTitle("Product");
        auctionInfo.setEndDate(LocalDateTime.now().plusDays(1).toLocalDate());
        auctionInfo.setEndTime(LocalDateTime.now().plusDays(1).toLocalTime());
        auctionInfo.setIsValid(true);

        auctionResultService.auctionResult();

        verify(auctionInfoRepository, never()).save(auctionInfo);
        verify(bidRepository, never()).findWiningBid(anyString());
    }

    @Test
    void auctionResult_shouldNotSendEmailsForInvalidAuction() {
        AuctionInfo auctionInfo = new AuctionInfo();
        auctionInfo.setUniqueCode("12345");
        auctionInfo.setTitle("Product");
        auctionInfo.setEndDate(LocalDateTime.now().minusDays(1).toLocalDate());
        auctionInfo.setEndTime(LocalDateTime.now().minusDays(1).toLocalTime());
        auctionInfo.setIsValid(false);

        when(auctionInfoRepository.findAll()).thenReturn(List.of(auctionInfo));

        auctionResultService.auctionResult();

        verify(kafkaTemplate, never()).send(anyString(), any(Message.class));
        verify(auctionInfoRepository, never()).save(auctionInfo);
        verify(auctionInfoRepository, times(1)).findAll();
        verify(bidRepository, never()).findWiningBid(anyString());
    }
}