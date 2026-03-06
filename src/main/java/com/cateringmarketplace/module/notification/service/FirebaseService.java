package com.cateringmarketplace.module.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

/**
 * Service for sending Firebase push notifications using Firebase Admin SDK.
 */
@Service
@Slf4j
public class FirebaseService {

    @Value("${firebase.credentials-path:firebase-credentials.json}")
    private String credentialsPath;

    @Value("${firebase.project-id:}")
    private String projectId;

    private boolean firebaseReady = false;

    @PostConstruct
    public void init() {
        if (projectId == null || projectId.isEmpty()) {
            log.warn("Firebase project-id not configured. Push notifications will be disabled.");
            return;
        }

        // Avoid re-initialization if already done
        if (!FirebaseApp.getApps().isEmpty()) {
            firebaseReady = true;
            log.info("Firebase already initialized.");
            return;
        }

        try {
            InputStream serviceAccount = loadCredentials();
            if (serviceAccount == null) {
                log.warn("Firebase credentials file not found at: {}. Push notifications disabled.", credentialsPath);
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);
            firebaseReady = true;
            log.info("Firebase initialized successfully for project: {}", projectId);

        } catch (IOException e) {
            log.warn("Failed to initialize Firebase: {}. Push notifications will be disabled.", e.getMessage());
            firebaseReady = false;
        } catch (Exception e) {
            log.warn("Unexpected error initializing Firebase: {}. Push notifications will be disabled.", e.getMessage());
            firebaseReady = false;
        }
    }

    /**
     * Sends a push notification to a single device.
     *
     * @param fcmToken Device FCM token
     * @param title    Notification title
     * @param body     Notification body
     * @param data     Optional custom data payload (conversation id, order id, etc.)
     */
    public void sendPushNotification(String fcmToken, String title, String body, String data) {
        if (!firebaseReady) {
            log.debug("Firebase not ready – push notification skipped (title: {})", title);
            return;
        }
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Cannot send push notification: FCM token is null or empty");
            return;
        }

        try {
            Map<String, String> dataMap = new HashMap<>();
            if (data != null && !data.isEmpty()) {
                dataMap.put("extra", data);
            }

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(dataMap)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("Push notification sent. MessageId: {}, Title: {}", messageId, title);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to token {}: {} (code: {})",
                    fcmToken, e.getMessage(), e.getMessagingErrorCode());
        } catch (Exception e) {
            log.error("Unexpected error sending push notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a push notification with full data map.
     */
    public void sendPushNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (!firebaseReady) {
            log.debug("Firebase not ready – push notification skipped (title: {})", title);
            return;
        }
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Cannot send push notification: FCM token is null or empty");
            return;
        }

        try {
            Message.Builder builder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .build())
                            .build());

            if (data != null && !data.isEmpty()) {
                builder.putAllData(data);
            }

            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.info("Push notification sent. MessageId: {}, Title: {}", messageId, title);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification: {} (code: {})",
                    e.getMessage(), e.getMessagingErrorCode());
        } catch (Exception e) {
            log.error("Unexpected error sending push notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a push notification to multiple devices (multicast).
     *
     * @param tokens List of FCM tokens
     * @param title  Notification title
     * @param body   Notification body
     * @param data   Optional data payload
     */
    public void sendMulticastNotification(java.util.List<String> tokens, String title, String body,
                                          Map<String, String> data) {
        if (!firebaseReady || tokens == null || tokens.isEmpty()) {
            log.debug("Firebase not ready or no tokens – multicast skipped");
            return;
        }

        try {
            MulticastMessage.Builder builder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build());

            if (data != null && !data.isEmpty()) {
                builder.putAllData(data);
            }

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(builder.build());
            log.info("Multicast notification sent. Success: {}, Failure: {}",
                    response.getSuccessCount(), response.getFailureCount());

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a topic-based notification (broadcast to subscribed devices).
     */
    public void sendTopicNotification(String topic, String title, String body, Map<String, String> data) {
        if (!firebaseReady) {
            log.debug("Firebase not ready – topic notification skipped");
            return;
        }

        try {
            Message.Builder builder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                builder.putAllData(data);
            }

            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.info("Topic notification sent to '{}'. MessageId: {}", topic, messageId);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send topic notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Checks if Firebase is configured and ready.
     */
    public boolean isReady() {
        return firebaseReady;
    }

    private InputStream loadCredentials() {
        // 1. Try classpath
        InputStream stream = getClass().getClassLoader().getResourceAsStream(credentialsPath);
        if (stream != null) {
            log.info("Firebase credentials loaded from classpath: {}", credentialsPath);
            return stream;
        }

        // 2. Try filesystem
        try {
            FileInputStream fileStream = new FileInputStream(credentialsPath);
            log.info("Firebase credentials loaded from filesystem: {}", credentialsPath);
            return fileStream;
        } catch (IOException ignored) {
            // not found on filesystem either
        }

        return null;
    }
}
