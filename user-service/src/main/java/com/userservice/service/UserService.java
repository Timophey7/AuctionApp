package com.userservice.service;

import com.userservice.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    public void saveUser(User user);

    User findUserByEmail(String email) throws UsernameNotFoundException;

}
