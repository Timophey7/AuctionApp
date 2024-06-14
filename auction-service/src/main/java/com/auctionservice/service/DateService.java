package com.auctionservice.service;

import java.time.LocalDate;
import java.time.LocalTime;

public interface DateService {

    boolean startAuctionTime(LocalDate startDate, LocalTime startTime);

}
