package com.notificationservice.config;

import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Properties;

@Configuration
public class JavaMailSenderConfig {

    @Bean
    public MimeMessageHelper mimeMessageHelper() {
        return new MimeMessageHelper(mimeMessage(),"utf-8");
    }

    @Bean
    public MimeMessage mimeMessage() {
        return javaMailSender().createMimeMessage();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("your email");
        mailSender.setPassword("your password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }


}
