package com.auctionservice.repository;

import com.auctionservice.models.AuctionInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionInfoRepository extends JpaRepository<AuctionInfo, Integer> {

    AuctionInfo findAuctionInfoByUniqueCode(String uniqueCode);

    List<AuctionInfo> findAllByOwnerEmail(String email);

}
