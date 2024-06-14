package com.auctionservice.controller;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.models.User;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import com.auctionservice.service.CookieUtil;
import com.auctionservice.service.DateService;
import com.auctionservice.service.HashGenerator;
import com.auctionservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/v1/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final HashGenerator hashGenerator;
    private final AuctionInfoRepository auctionInfoRepository;
    private final CookieUtil cookieUtil;
    private final BidRepository bidRepository;
    private final DateService dateService;
    private final UserService userService;


    @GetMapping("/home")
    @Timed("homePage")
    public String home(Model model) {
        List<AuctionInfo> validAuctions = new ArrayList<>();
        auctionInfoRepository.findAll().forEach(auctionInfo -> {
            if(auctionInfo.getIsValid() && dateService.startAuctionTime(auctionInfo.getStartDate(),auctionInfo.getStartTime())){
                validAuctions.add(auctionInfo);
            }
        });
        model.addAttribute("auctions", validAuctions);
        return "home";
    }


    @GetMapping("/createAuctionForm")
    public String createAuctionForm(Model model,HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/v1/auction/security/login";
        }
        model.addAttribute("auction", new AuctionInfo());
        return "create-auction-form";
    }

    @PostMapping("/saveAuction")
    public String saveAuction(@Valid @ModelAttribute("auction") AuctionInfo auction, BindingResult bindingResult, Model model) {
        auction.setUniqueCode(hashGenerator.generateHash());
        auctionInfoRepository.save(auction);
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

    @GetMapping("/newBid/{uniqueCode}")
    public String newBid(@PathVariable("uniqueCode") String uniqueCode,
                         @Valid @ModelAttribute("bid")Bid bid, BindingResult result, Model model,
                         HttpSession session) {
        AuctionInfo auctionInfoByUniqueCode = auctionInfoRepository.findAuctionInfoByUniqueCode(uniqueCode);
        if (result.hasErrors() || bid.getNewBid()<auctionInfoByUniqueCode.getPrice()) {
            return "redirect:/v1/auction/info/" + uniqueCode;
        }
        String email = (String) session.getAttribute("email");
        if (email ==null){
            return "redirect:/v1/auction/security/login";
        }
        bid.setUniqueCode(uniqueCode);
        bid.setCustomerEmail(email);
        bidRepository.save(bid);
        auctionInfoByUniqueCode.setPrice(bid.getNewBid());
        auctionInfoRepository.save(auctionInfoByUniqueCode);
        return "redirect:/v1/auction/info/" + uniqueCode;
    }

    @GetMapping("/personalArea")
    public String personalArea(Model model,HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/v1/auction/security/login";
        }
        User userByEmail = userService.findUserByEmail(email);
        List<Bid> allByCustomerEmail = bidRepository.findAllByCustomerEmail(email);
        List<AuctionInfo> allByOwnerEmail = auctionInfoRepository.findAllByOwnerEmail(email);
        model.addAttribute("allAuctions", allByOwnerEmail);
        model.addAttribute("allBids", allByCustomerEmail);
        model.addAttribute("user", userByEmail);
        return "personalArea";
    }

}
