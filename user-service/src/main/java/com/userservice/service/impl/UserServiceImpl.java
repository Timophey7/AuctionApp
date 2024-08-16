package com.userservice.service.impl;

import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.reposiroty.UserRepository;
import com.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) throws UsernameNotFoundException {
        return  userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
