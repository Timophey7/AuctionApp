package com.notificationservice.service;

import com.notificationservice.model.Message;

public interface NotificationService {

    void sendEmail(Message message);

}
