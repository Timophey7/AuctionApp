package com.auctionservice.controller;

import com.auctionservice.models.AuthenticationResponse;
import com.auctionservice.models.User;
import com.auctionservice.service.CookieUtil;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/auction/security")
public class SecurityController {


    private final WebClient.Builder webClientBuilder;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/sendUserInfo")
    public String sendUserInfo(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/v1/auction/security/registration";
        }
        String answer = webClientBuilder.build().post().uri("http://user-service/v1/security/register")
                .bodyValue(user).retrieve().bodyToMono(String.class).block();
        return "redirect:/v1/auction/security/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("authentication", new AuthenticationResponse());
        return "login";
    }

    @GetMapping("/verification")
    @Timed("verificationPage")
    public String verification(@Valid @ModelAttribute("authentication") AuthenticationResponse authenticationResponse, BindingResult result
            , HttpServletResponse response, HttpSession session) {
        if (result.hasErrors()) {
            return "redirect:/v1/auction/security/login";
        }
        String infoString = webClientBuilder.build().post().uri("http://user-service/v1/security/verify")
                .bodyValue(authenticationResponse).retrieve().bodyToMono(String.class).block();
        if (infoString.equals("verified")) {
            session.setAttribute("email", authenticationResponse.getEmail());
            return "redirect:/v1/auction/auction/home";
        }
        return "redirect:/v1/auction/security/login";
    }

}
