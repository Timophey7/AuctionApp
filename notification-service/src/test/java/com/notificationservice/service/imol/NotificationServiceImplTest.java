package com.notificationservice.service.imol;

import com.notificationservice.model.Message;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {


    @Mock
    private MimeMessageHelper helper;
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private NotificationServiceImpl service;

    private Message message;

    @BeforeEach
    public void setUp() {
        message = new Message();
        message.setOwner(false);
        message.setUniqueCode("test");
        message.setWinnerEmail("test@test.com");
        message.setProductName("product");
        message.setProductPrice(100);
    }

    @Test
    void sendEmailForWinner() throws MessagingException {
        String productName = message.getProductName();
        String productCode = message.getUniqueCode();
        double winningPrice = message.getProductPrice();
        String htmlContentForOwner = "<html><body>" +
                "<h1>Поздравляем! Ваш товар купили!</h1>" +
                "<p>Вы успешно продали  товар: <b>" + productName + "</b> с кодом <b>" + productCode + "</b>.</p>" +
                "<p>По финальной ставке: <b>" + winningPrice + "</b>.</p>" +
                "<p>Деньги в течении дня придут вам на карту</p>" +
                "</body></html>";
        String htmlContentForWinner = "<html><body>" +
                "<h1>Поздравляем! Вы выиграли аукцион!</h1>" +
                "<p>Вы успешно выиграли аукцион товара: <b>" + productName + "</b> с кодом <b>" + productCode + "</b>.</p>" +
                "<p>Ваша финальная ставка: <b>" + winningPrice + "</b>.</p>" +
                "<p>Подробнее о товаре и о том, как получить его, вы можете узнать, обратившись к администрации аукциона.</p>" +
                "</body></html>";

        service.sendEmail(message);

        verify(helper).setFrom("auction@gmail.com");
        verify(helper).setTo(message.getWinnerEmail());
        verify(helper).setSubject("auction notification");
        verify(helper).setText(htmlContentForWinner,true);
        verify(mailSender).send(helper.getMimeMessage());

    }

    @Test
    void sendEmailForOwner() throws MessagingException {
        message.setOwner(true);
        String productName = message.getProductName();
        String productCode = message.getUniqueCode();
        double winningPrice = message.getProductPrice();
        String htmlContentForOwner = "<html><body>" +
                "<h1>Поздравляем! Ваш товар купили!</h1>" +
                "<p>Вы успешно продали  товар: <b>" + productName + "</b> с кодом <b>" + productCode + "</b>.</p>" +
                "<p>По финальной ставке: <b>" + winningPrice + "</b>.</p>" +
                "<p>Деньги в течении дня придут вам на карту</p>" +
                "</body></html>";
        String htmlContentForWinner = "<html><body>" +
                "<h1>Поздравляем! Вы выиграли аукцион!</h1>" +
                "<p>Вы успешно выиграли аукцион товара: <b>" + productName + "</b> с кодом <b>" + productCode + "</b>.</p>" +
                "<p>Ваша финальная ставка: <b>" + winningPrice + "</b>.</p>" +
                "<p>Подробнее о товаре и о том, как получить его, вы можете узнать, обратившись к администрации аукциона.</p>" +
                "</body></html>";

        service.sendEmail(message);

        verify(helper).setFrom("auction@gmail.com");
        verify(helper).setTo(message.getWinnerEmail());
        verify(helper).setSubject("auction notification");
        verify(helper).setText(htmlContentForOwner,true);
        verify(mailSender).send(helper.getMimeMessage());

    }
}