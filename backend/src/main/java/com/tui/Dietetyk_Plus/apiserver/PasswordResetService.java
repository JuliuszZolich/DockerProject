package com.tui.Dietetyk_Plus.apiserver;

import com.courier.api.Courier;
import com.courier.api.requests.SendMessageRequest;
import com.courier.api.resources.send.types.*;
import com.tui.Dietetyk_Plus.database.RedisService;
import com.tui.Dietetyk_Plus.database.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {
    private static final String RESET_PASSWORD_KEY = "reset_password:";

    private final RedisService redisService;
    private final Courier courierClient;

    private static final String RESET_PASSWORD_KEY_PREFIX = "reset_password:"; // Renamed for clarity

    @Autowired
    public PasswordResetService(
            RedisService redisService,
            @Value("${courier.api_key}") String courierApiKey) {
        this.redisService = redisService;
        this.courierClient = Courier.builder()
                .authorizationToken(courierApiKey)
                .build();
    }

    public boolean generateResetToken(String userEmail) {
        if (!redisService.checkForUsedEmail(userEmail)) {
            System.err.println("Email not registered for password reset: " + userEmail);
            return false;
        }

        String token = String.format("%06d", new Random().nextInt(0, 999999));

        redisService.saveRecoveryKey(RESET_PASSWORD_KEY_PREFIX + token, userEmail, 15, TimeUnit.MINUTES);

        String userKey = redisService.getUserKey(userEmail);
        if (userKey == null) {
            System.err.println("User key not found in Redis for email: " + userEmail);
            return false;
        }
        User user = redisService.getUser(userKey);
        if (user == null) {
            System.err.println("User object not found in Redis for key: " + userKey);
            return false;
        }

        MessageRecipient recipient = MessageRecipient.of(Recipient.of(UserRecipient.builder()
                .email(userEmail)
                .build()));

        Message message = Message.of(TemplateMessage.builder()
                .template("7BTAAWQF4MMJWYJ0TK7FK93YM3M3")
                .data(Map.of("code", token))
                .to(recipient)
                .build());

        SendMessageRequest messageRequest = SendMessageRequest.builder().message(message).build();
        courierClient.send(messageRequest);

        return true;
    }

    public void invalidateToken(String token) {
        redisService.deleteKey(RESET_PASSWORD_KEY + token);
    }

    public boolean validateResetToken(String token, String email) {
        String dbEmail = redisService.getRecoveryKey(RESET_PASSWORD_KEY + token);
        if (dbEmail == null) {
            return false;
        }
        if (!dbEmail.equals(email)) {
            return false;
        }
        invalidateToken(token);
        return true;
    }


}