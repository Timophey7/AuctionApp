package com.userservice.controller;

import com.userservice.model.AuthenticationResponse;
import com.userservice.model.User;
import com.userservice.reposiroty.UserRepository;
import com.userservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/security")
@RequiredArgsConstructor
public class SecurityController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

   @PostMapping("/register")
   @Timed("UserRegisterPage")
   public ResponseEntity<String> registerUser(@RequestBody User user) {
       userService.saveUser(user);
       return new ResponseEntity<>(
               "success",
               HttpStatus.CREATED
       );
   }

   @GetMapping("/getUserByEmail/{userEmail}")
   public User getUserByEmail(@PathVariable String userEmail) {
       User user = userRepository.findUserByEmail(userEmail)
               .orElse(null);
       return user;
   }

   @PostMapping("/verify")
   @Timed("verifyUser")
    public String verifyUser(@RequestBody AuthenticationResponse authenticationResponse) {
       User user = userRepository.findUserByEmail(authenticationResponse.getEmail())
               .orElse(null);
       if (user == null) {
           return "user not found";
       }
       if (!passwordEncoder.matches(authenticationResponse.getPassword(), user.getPassword())) {
           return "wrong password";
       }
       return "verified";
   }



}
