package com.auctionservice.repository;

import com.auctionservice.models.Bid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BidRepositoryTest {

    @Autowired
    BidRepository bidRepository;

    @Test
    void findWiningBid() {
        Bid bidWin = new Bid();
        bidWin.setNewBid(110);
        bidWin.setCustomerEmail("test@gmail.com");
        bidWin.setUniqueCode("werty123");
        Bid bidLost = new Bid();
        bidLost.setNewBid(101);
        bidLost.setCustomerEmail("test2@gmail.com");
        bidLost.setUniqueCode("werty123");
        bidRepository.save(bidWin);
        bidRepository.save(bidLost);

        Bid winingBid = bidRepository.findWiningBid("werty123");

        assertEquals(bidWin,winingBid);

    }
}