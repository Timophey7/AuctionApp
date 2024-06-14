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
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuctionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    public HashGenerator hashGenerator;
    @MockBean
    public AuctionInfoRepository auctionInfoRepository;
    @MockBean
    public CookieUtil cookieUtil;
    @MockBean
    public BidRepository bidRepository;
    @MockBean
    public DateService dateService;
    @MockBean
    public UserService userService;

    private Bid bid;
    private AuctionInfo auctionInfo;
    private User user;

    @BeforeEach
    void setUp() {
        auctionInfo = new AuctionInfo();
        auctionInfo.setId(1);
        auctionInfo.setTitle("title");
        auctionInfo.setDescription("description");
        auctionInfo.setPrice(390);
        auctionInfo.setUniqueCode("test");
        auctionInfo.setOwnerEmail("test@test.com");
        auctionInfo.setIsValid(true);
        bid = new Bid();
        bid.setId(1);
        bid.setCustomerEmail("test@test.com");
        bid.setNewBid(200);
        bid.setUniqueCode("test");
        user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("firs");
        user.setLastName("last");
    }

    @Test
    void homeShouldReturnStatusIsOk() throws Exception {
        List<AuctionInfo> validAuctions = List.of(auctionInfo);

        when(auctionInfoRepository.findAll()).thenReturn(validAuctions);


        when(dateService.startAuctionTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(true);

        ResultActions perform = mockMvc.perform(get("/v1/auction/home"));

        perform
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("auctions"));

    }

    @Test
    void homeShouldReturnEmptyListOfAuctions() throws Exception {
        auctionInfo.setIsValid(false);
        List<AuctionInfo> unValidAuctions = List.of(auctionInfo);
        when(auctionInfoRepository.findAll()).thenReturn(unValidAuctions);

        ResultActions perform = mockMvc.perform(get("/v1/auction/home"));

        perform.andExpect(status().isOk())
                .andExpect(model().attributeExists("auctions"))
                .andExpect(model().attribute("auctions", hasSize(0)));


    }

    @Test
    void createAuctionFormShouldReturnStatusIsOk() throws Exception {

        String email = "test@test.com";
        when(cookieUtil.getCookieValue(any(HttpServletRequest.class), anyString())).thenReturn(email);

        ResultActions perform = mockMvc.perform(get("/v1/auction/createAuctionForm"));

        perform.andExpect(status().isOk())
                .andExpect(model().attributeExists("auction"));

    }

    @Test
    void createAuctionFormShouldReturnRedirectStatus() throws Exception {
        String email = null;
        when(cookieUtil.getCookieValue(any(HttpServletRequest.class), anyString())).thenReturn(email);

        ResultActions perform = mockMvc.perform(get("/v1/auction/createAuctionForm"));

        perform.andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("auction"));
    }

    @Test
    void saveAuctionShouldReturnStatusIsRedirect() throws Exception {

        when(hashGenerator.generateHash()).thenReturn("test");
        when(auctionInfoRepository.save(any(AuctionInfo.class))).thenReturn(auctionInfo);

        ResultActions perform = mockMvc.perform(post("/v1/auction/saveAuction")
                .requestAttr("auction", auctionInfo));

        perform.andExpect(status().is3xxRedirection());
        verify(auctionInfoRepository).save(any(AuctionInfo.class));

    }

    @Test
    void auctionShouldReturnStatusIsOk() throws Exception {

        String uniqueCode = "test";
        when(auctionInfoRepository.findAuctionInfoByUniqueCode(uniqueCode)).thenReturn(auctionInfo);

        ResultActions perform = mockMvc.perform(get("/v1/auction/info/{uniqueCode}", uniqueCode)
                .param("uniqueCode", uniqueCode));

        perform.andExpect(status().isOk())
                .andExpect(model().attributeExists("auction"))
                .andExpect(model().attribute("auction", auctionInfo))
                .andExpect(model().attributeExists("bid"));

    }

    @Test
    void newBidShouldRedirectToInfoAndSaveAllInfo() throws Exception {

        String uniqueCode = "test";
        when(auctionInfoRepository.findAuctionInfoByUniqueCode(uniqueCode)).thenReturn(auctionInfo);
        when(cookieUtil.getCookieValue(any(HttpServletRequest.class), anyString())).thenReturn("test@test.com");

        ResultActions perform = mockMvc.perform(get("/v1/auction/newBid/test")
                .param("uniqueCode", uniqueCode)
                .requestAttr("bid", bid));

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/info/test"));
    }

    @Test
    void newBidShouldRedirectToInfoAndNotSaveAllInfo() throws Exception {
        AuctionInfo auctionInfo = new AuctionInfo();
        auctionInfo.setPrice(100);
        when(auctionInfoRepository.findAuctionInfoByUniqueCode("test")).thenReturn(auctionInfo);

        mockMvc.perform(get("/v1/auction/newBid/test")
                        .param("uniqueCode", "test")
                        .requestAttr("bid",bid))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/info/test"));

        verify(bidRepository, never()).save(any(Bid.class));
        verify(auctionInfoRepository, never()).save(any(AuctionInfo.class));
    }

    @Test
    void newBidShouldRedirectToLoginAndNotSaveAllInfo() throws Exception {

        when(cookieUtil.getCookieValue(any(HttpServletRequest.class), anyString())).thenReturn(null);

        mockMvc.perform(get("/newBid/test")
                        .param("uniqueCode","test")
                        .requestAttr("bid",bid))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/security/login"));

        verify(bidRepository, never()).save(any(Bid.class));
        verify(auctionInfoRepository, never()).save(any(AuctionInfo.class));
    }

    @Test
    void personalAreaShouldReturnStatusIsOk() throws Exception {
        List<Bid> bids = List.of(bid);
        List<AuctionInfo> validAuctions = List.of(auctionInfo);
        String email = "test@test.com";
        when(cookieUtil.getCookieValue(any(HttpServletRequest.class), anyString())).thenReturn(email);
        when(userService.findUserByEmail(email)).thenReturn(user);
        when(bidRepository.findAllByCustomerEmail(email)).thenReturn(bids);
        when(auctionInfoRepository.findAllByOwnerEmail(email)).thenReturn(validAuctions);

        ResultActions perform = mockMvc.perform(get("/v1/auction/personalArea"));

        perform.andExpect(status().isOk())
                .andExpect(model().attribute("allAuctions",validAuctions))
                .andExpect(model().attribute("allBids",bids))
                .andExpect(model().attribute("user",user));


    }

    @Test
    void personalAreaShouldReturnRedirectStatus() throws Exception {
        when(cookieUtil.getCookieValue(any(HttpServletRequest.class), anyString())).thenReturn(null);


        ResultActions perform = mockMvc.perform(get("/v1/auction/personalArea"));

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/security/login"));


    }
}