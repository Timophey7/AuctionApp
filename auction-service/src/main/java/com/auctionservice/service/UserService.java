package com.auctionservice.service;

import com.auctionservice.models.User;

public interface UserService {

    User findUserByEmail(String email);

}
