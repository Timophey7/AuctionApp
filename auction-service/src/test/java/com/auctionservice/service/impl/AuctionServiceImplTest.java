package com.auctionservice.service.impl;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {

    @InjectMocks
    AuctionServiceImpl auctionService;

    @Mock
    AuctionInfoRepository auctionInfoRepository;

    @Mock
    BidRepository bidRepository;

    AuctionInfo auctionInfo;

    private static final String UNIQUE_CODE = "werty123";

    @BeforeEach
    void setUp() {
        auctionInfo = new AuctionInfo();
        auctionInfo.setId(1);
        auctionInfo.setIsValid(true);
        auctionInfo.setStartDate(LocalDate.now());
        auctionInfo.setStartTime(LocalTime.now());
        auctionInfo.setEndDate(LocalDate.now().plusDays(1));
        auctionInfo.setEndTime(LocalTime.now().plusHours(1));
        auctionInfo.setPrice(1000);
        auctionInfo.setUniqueCode(UNIQUE_CODE);
        auctionInfo.setOwnerEmail("test@gmail.com");
    }

    @Test
    void getValidAuctions() {
        AuctionInfo auctionInfo1 = new AuctionInfo();
        auctionInfo1.setIsValid(false);
        when(auctionInfoRepository.findAll()).thenReturn(List.of(auctionInfo,auctionInfo1));

        List<AuctionInfo> validAuctions = auctionService.getValidAuctions();

        assertEquals(1,validAuctions.size());
        assertEquals(auctionInfo,validAuctions.get(0));
    }

    @Test
    void saveAuction() {

        when(auctionInfoRepository.save(auctionInfo)).thenReturn(auctionInfo);

        auctionService.saveAuction(auctionInfo);

        verify(auctionInfoRepository,times(1)).save(auctionInfo);

    }

    @Test
    void validPrice_True() {

        when(auctionInfoRepository.findAuctionInfoByUniqueCode(UNIQUE_CODE)).thenReturn(auctionInfo);

        boolean valided = auctionService.validPrice(UNIQUE_CODE, 1200);

        assertTrue(valided);
        verify(auctionInfoRepository,times(1)).findAuctionInfoByUniqueCode(UNIQUE_CODE);
    }

    @Test
    void validPrice_False() {

        when(auctionInfoRepository.findAuctionInfoByUniqueCode(UNIQUE_CODE)).thenReturn(auctionInfo);

        boolean valided = auctionService.validPrice(UNIQUE_CODE, 900);

        assertFalse(valided);
        verify(auctionInfoRepository,times(1)).findAuctionInfoByUniqueCode(UNIQUE_CODE);
    }

    @Test
    void setNewPrice() {
        Bid bid = new Bid();
        when(auctionInfoRepository.findAuctionInfoByUniqueCode(UNIQUE_CODE)).thenReturn(auctionInfo);
        when(bidRepository.save(bid)).thenReturn(bid);
        when(auctionInfoRepository.save(auctionInfo)).thenReturn(auctionInfo);

        auctionService.setNewPrice(UNIQUE_CODE,bid,"test@gmail.com");

        verify(auctionInfoRepository,times(1)).findAuctionInfoByUniqueCode(UNIQUE_CODE);
        verify(auctionInfoRepository,times(1)).save(auctionInfo);
        verify(bidRepository,times(1)).save(bid);

    }

}