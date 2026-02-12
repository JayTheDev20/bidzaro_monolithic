package com.cateringmarketplace.module.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseService {

    public void sendPushNotification(String fcmToken, String title, String body, String data) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Cannot send push notification: FCM token is null");
            return;
        }
        
        // In a real implementation, this would use FirebaseMessaging.getInstance().send(...)
        log.info("Sending Push Notification to {}: Title='{}', Body='{}'", fcmToken, title, body);
    }
}
