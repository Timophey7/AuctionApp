package com.auctionservice.service.impl;

import com.auctionservice.service.HashGenerator;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Random;

@Service
public class HashGeneratorImpl implements HashGenerator {
    @Override
    public String generateHash() {
        byte[] randomBytes = new byte[6];
        new Random().nextBytes(randomBytes);
        String base64Encoded = Base64.getEncoder().encodeToString(randomBytes);
        return base64Encoded.substring(0, 8);
    }
}
