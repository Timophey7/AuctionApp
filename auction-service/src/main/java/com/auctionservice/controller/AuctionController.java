package com.auctionservice.controller;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.models.User;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import com.auctionservice.service.*;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("/v1/auction")
@Controller
public class AuctionController {

    private static final Logger log = LoggerFactory.getLogger(AuctionController.class);
    AuctionInfoRepository auctionInfoRepository;
    BidRepository bidRepository;
    UserService userService;
    AuctionService auctionService;


    @GetMapping("/home")
    @Timed("homePage")
    public String home(Model model) {
        List<AuctionInfo> validAuctions = auctionService.getValidAuctions();
        model.addAttribute("auctions", validAuctions);
        return "home";
    }

    @GetMapping("/createAuctionForm")
    public String createAuctionForm(Model model,HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errors","Войдите в аккаунт");
            return "redirect:/v1/auction/security/login";
        }
        model.addAttribute("auction", new AuctionInfo());
        return "create-auction-form";
    }

    @PostMapping("/saveAuction")
    public String saveAuction(@Valid @ModelAttribute("auction") AuctionInfo auction, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/v1/auction/createAuctionForm";
        }
        auctionService.saveAuction(auction);
        return "redirect:/v1/auction/home";
    }

    @Timed("infoAboutAuction")
    @GetMapping("/info/{uniqueCode}")
    public String auction(@PathVariable("uniqueCode") String uniqueCode, Model model) {
        AuctionInfo auctionInfoByUniqueCode = auctionInfoRepository.findAuctionInfoByUniqueCode(uniqueCode);
        model.addAttribute("auction", auctionInfoByUniqueCode);
        model.addAttribute("bid",new Bid());
        return "auction";
    }

    @PostMapping("/newBid/{uniqueCode}")
    public String newBid(
            @PathVariable("uniqueCode") String uniqueCode,
            @ModelAttribute("bid")Bid bid,
            Model model,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        if (email ==null){
            model.addAttribute("errors","Войдите в аккаунт");
            return "redirect:/v1/auction/security/login";
        }
        boolean valided = auctionService.validPrice(uniqueCode, bid.getNewBid());
        log.info("bid:" + bid.getNewBid());
        log.info(String.valueOf(valided));
        if (!valided){
            model.addAttribute("bidIsLowError","ставка слишком низкая");
            log.info(model.getAttribute("bidIsLowError").toString());
            return "redirect:/v1/auction/info/" + uniqueCode+ "?bidIsLowError=ставка слишком низкая";
        }
        auctionService.setNewPrice(uniqueCode,bid,email);
        return "redirect:/v1/auction/info/" + uniqueCode;
    }

    @GetMapping("/personalArea.css")
    public String personalArea(Model model,HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errors","Войдите в аккаунт");
            return "redirect:/v1/auction/security/login";
        }
        User userByEmail = userService.findUserByEmail(email);
        List<Bid> allByCustomerEmail = bidRepository.findAllByCustomerEmail(email);
        List<AuctionInfo> allByOwnerEmail = auctionInfoRepository.findAllByOwnerEmail(email);
        model.addAttribute("allAuctions", allByOwnerEmail);
        model.addAttribute("allBids", allByCustomerEmail);
        model.addAttribute("user", userByEmail);
        return "personalArea.css";
    }
}
