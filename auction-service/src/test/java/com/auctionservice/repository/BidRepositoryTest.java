package com.auctionservice.repository;

import com.auctionservice.models.Bid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Test
    void findWiningBid() {
        Bid bid = new Bid();
        bid.setUniqueCode("test");
        bid.setCustomerEmail("test@test.com");
        bid.setNewBid(100);
        Bid bid2 = new Bid();
        bid2.setUniqueCode("test");
        bid2.setCustomerEmail("test2@test.com");
        bid2.setNewBid(200);
        bidRepository.save(bid);
        bidRepository.save(bid2);

        Bid winingBid = bidRepository.findWiningBid("test");

        assertNotNull(winingBid);
        assertEquals(winingBid.getUniqueCode(), "test");
        assertEquals(winingBid.getCustomerEmail(), "test2@test.com");
        assertEquals(winingBid.getNewBid(), 200);


    }
}