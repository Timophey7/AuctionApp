package com.auctionservice.service.impl;

import com.auctionservice.service.DateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DateServiceImpl implements DateService {
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
