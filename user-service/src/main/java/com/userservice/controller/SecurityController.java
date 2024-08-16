package com.userservice.controller;

import com.userservice.model.AuthenticationRequest;
import com.userservice.model.User;
import com.userservice.reposiroty.UserRepository;
import com.userservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
   public ResponseEntity<String> registerUser(@Valid @RequestBody User user, BindingResult result) {
       if (result.hasErrors()){
           return ResponseEntity.badRequest().build();
       }
       userService.saveUser(user);
       return new ResponseEntity<>(
               "success",
               HttpStatus.CREATED
       );
   }

   @GetMapping("/getUserByEmail/{userEmail}")
   public ResponseEntity<?> getUserByEmail(@PathVariable String userEmail) {
       try {
           return ResponseEntity.ok(userService.findUserByEmail(userEmail));
       }catch (UsernameNotFoundException exception){
           return ResponseEntity.notFound().build();
       }

   }

   @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody AuthenticationRequest authenticationRequest) {
       User user = userRepository.findUserByEmail(authenticationRequest.getEmail())
               .orElse(null);
       if (user == null) {
           return ResponseEntity.badRequest().body("user not found");
       }
       if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
           return ResponseEntity.badRequest().body("wrong password");
       }
       return ResponseEntity.ok("verified");
   }



}
