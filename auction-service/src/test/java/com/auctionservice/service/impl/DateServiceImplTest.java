package com.auctionservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DateServiceImplTest {

    @InjectMocks
    DateServiceImpl dateService;

    @Test
    void startAuctionTime() {

        LocalDate localDate = LocalDate.of(2024,6,4);
        LocalTime localTime = LocalTime.of(8,0);

        boolean b = dateService.startAuctionTime(localDate, localTime);
        assertTrue(b);

    }
}