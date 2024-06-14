package com.auctionservice.repository;

import com.auctionservice.models.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

    @Query(value = "SELECT * FROM bid_info WHERE unique_code = :uniqueCode AND new_bid = (SELECT MAX(new_bid) FROM bid_info WHERE unique_code = :uniqueCode)",nativeQuery = true)
    Bid findWiningBid(String uniqueCode);

    List<Bid> findAllByCustomerEmail(String email);
}
