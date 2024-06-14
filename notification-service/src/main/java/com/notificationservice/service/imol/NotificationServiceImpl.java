package com.notificationservice.service.imol;

import com.notificationservice.model.Message;
import com.notificationservice.service.NotificationService;
import io.micrometer.core.annotation.Timed;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final MimeMessageHelper helper;
    private final JavaMailSender mailSender;

    @Override
    @Timed("sendEmailMethod")
    public void sendEmail(Message message) {
        try{
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
            helper.setFrom("auction@gmail.com");
            helper.setTo(message.getWinnerEmail());
            helper.setSubject("auction notification");
            if (message.isOwner()){
                helper.setText(htmlContentForOwner,true);
            }else {
                helper.setText(htmlContentForWinner,true);
            }
            mailSender.send(helper.getMimeMessage());
            log.info("email sent successfully to: " + message.getWinnerEmail());
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
