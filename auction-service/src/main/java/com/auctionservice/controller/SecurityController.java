package com.auctionservice.controller;

import com.auctionservice.models.AuthenticationRequest;
import com.auctionservice.models.User;
import com.auctionservice.service.SecurityService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/auction/security")
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);
    private final SecurityService securityService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/sendUserInfo")
    public String sendUserInfo(@Valid @ModelAttribute("user") User user, BindingResult result,Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors",result.getAllErrors());
            return "redirect:/v1/auction/security/registration";
        }
        try {
            securityService.sendUserDataToSecurityService(user);
            return "redirect:/v1/auction/security/login";
        }catch (Exception exception){
            model.addAttribute("errors",exception.getMessage());
            return "redirect:/v1/auction/security/registration";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("authentication", new AuthenticationRequest());
        return "login";
    }

    @Timed("verificationPage")
    @PostMapping("/verification")
    public String verification(
            @Valid @ModelAttribute("authentication") AuthenticationRequest authenticationRequest,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        log.info(authenticationRequest.getEmail());
        if (result.hasErrors()) {
            model.addAttribute("errors",result.getAllErrors());
            return "redirect:/v1/auction/security/login";
        }
        if (securityService.verifyUser(authenticationRequest)) {
            session.setAttribute("email", authenticationRequest.getEmail());
            return "redirect:/v1/auction/home";
        }
        return "redirect:/v1/auction/security/login";
    }

}
