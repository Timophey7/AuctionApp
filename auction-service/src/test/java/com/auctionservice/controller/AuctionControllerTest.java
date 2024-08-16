package com.auctionservice.controller;

import com.auctionservice.models.AuctionInfo;
import com.auctionservice.models.Bid;
import com.auctionservice.models.User;
import com.auctionservice.repository.AuctionInfoRepository;
import com.auctionservice.repository.BidRepository;
import com.auctionservice.service.AuctionService;
import com.auctionservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AuctionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuctionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuctionInfoRepository auctionInfoRepository;

    @MockBean
    BidRepository bidRepository;

    @MockBean
    UserService userService;

    @MockBean
    AuctionService auctionService;

    private static final String UNIQUE_CODE = "werty123";


    ObjectMapper objectMapper;
    AuctionInfo auctionInfo;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        auctionInfo = new AuctionInfo();
        auctionInfo.setId(1);
        auctionInfo.setIsValid(true);
        auctionInfo.setStartDate(LocalDate.now());
        auctionInfo.setStartTime(LocalTime.now());
        auctionInfo.setEndDate(LocalDate.now().plusDays(1));
        auctionInfo.setEndTime(LocalTime.now().plusHours(1));
        auctionInfo.setPrice(1000);
        auctionInfo.setUniqueCode(UNIQUE_CODE);
        auctionInfo.setOwnerEmail("test@gmail.com");
        auctionInfo.setTitle("Test");
        auctionInfo.setPhotoUrl("testUrl");
        auctionInfo.setDescription("testDescription");
    }

    @Test
    void home() throws Exception {
        when(auctionService.getValidAuctions()).thenReturn(List.of(auctionInfo));

        ResultActions perform = mockMvc.perform(get("/v1/auction/home"));

        perform.andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("auctions",List.of(auctionInfo)));

    }

    @Test
    void createAuctionForm_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email","test@gmail.com");

        ResultActions perform = mockMvc.perform(get("/v1/auction/createAuctionForm")
                .session(session)
        );

        perform.andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("auction",new AuctionInfo()));
    }

    @Test
    void createAuctionForm_UserNotRegister() throws Exception {
        MockHttpSession session = new MockHttpSession();


        ResultActions perform = mockMvc.perform(get("/v1/auction/createAuctionForm")
                .session(session)
        );

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/security/login"));
    }

    @Test
    void saveAuction_Success() throws Exception {
        doNothing().when(auctionService).saveAuction(auctionInfo);

        ResultActions perform = mockMvc.perform(post("/v1/auction/saveAuction")
                .flashAttr("auction",auctionInfo)
        );

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/home"));
    }

    @Test
    void auction_Success() throws Exception {

        when(auctionInfoRepository.findAuctionInfoByUniqueCode(UNIQUE_CODE)).thenReturn(auctionInfo);

        ResultActions perform = mockMvc.perform(get("/v1/auction/info/" + UNIQUE_CODE));

        perform.andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("auction",auctionInfo))
                .andExpect(model().attribute("bid",new Bid()));
    }

    @Test
    void newBid_Success() throws Exception{
        Bid bid = new Bid();
        bid.setId(1);
        bid.setNewBid(2000.0);
        bid.setUniqueCode(UNIQUE_CODE);
        bid.setCustomerEmail("test@gmail.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email","test@gmail.com");
        when(auctionService.validPrice(UNIQUE_CODE,bid.getNewBid())).thenReturn(true);
        doNothing().when(auctionService).setNewPrice(UNIQUE_CODE,bid,"test@gmail.com");

        ResultActions perform = mockMvc.perform(post("/v1/auction/newBid/" + UNIQUE_CODE)
                        .flashAttr("bid",bid)
                .session(session)
        );

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/info/"+UNIQUE_CODE));

    }

    @Test
    void newBid_NotAuthorize() throws Exception{
        Bid bid = new Bid();
        bid.setId(1);
        bid.setNewBid(1100);
        bid.setUniqueCode(UNIQUE_CODE);
        bid.setCustomerEmail("test@gmail.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email",null);

        ResultActions perform = mockMvc.perform(post("/v1/auction/newBid/" + UNIQUE_CODE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bid))
                .session(session)
        );

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/security/login"));

    }

    @Test
    void newBid_BidIsLow() throws Exception{
        Bid bid = new Bid();
        bid.setId(1);
        bid.setNewBid(10);
        bid.setUniqueCode(UNIQUE_CODE);
        bid.setCustomerEmail("test@gmail.com");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email","test@gmail.com");
        when(auctionService.validPrice(UNIQUE_CODE,bid.getNewBid())).thenReturn(false);

        ResultActions perform = mockMvc.perform(post("/v1/auction/newBid/" + UNIQUE_CODE)
                .flashAttr("bid",bid)
                .session(session)
        );

        perform.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/info/"+UNIQUE_CODE+"?bidIsLowError=ставка слишком низкая"));
    }


    @Test
    void personalArea_Success() throws Exception {
        Bid bid = new Bid();
        String email = "test@gmail.com";
        User user = new User();
        user.setId(1);
        user.setUsername("testUserName");
        user.setEmail(email);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email",email);

        when(userService.findUserByEmail(email)).thenReturn(user);
        when(bidRepository.findAllByCustomerEmail(email)).thenReturn(List.of(bid));
        when(auctionInfoRepository.findAllByOwnerEmail(email)).thenReturn(List.of(auctionInfo));

        ResultActions perform = mockMvc.perform(get("/v1/auction/personalArea.css")
                .session(session)
        );

        perform
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("allAuctions",List.of(auctionInfo)))
                .andExpect(model().attribute("allBids",List.of(bid)))
                .andExpect(model().attribute("user",user));
    }

    @Test
    void personalArea_NotAuthorize() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email",null);


        ResultActions perform = mockMvc.perform(get("/v1/auction/personalArea.css")
                .session(session)
        );

        perform
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/auction/security/login"));
    }
}