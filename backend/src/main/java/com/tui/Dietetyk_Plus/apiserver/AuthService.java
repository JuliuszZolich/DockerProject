package com.tui.Dietetyk_Plus.apiserver;


import com.tui.Dietetyk_Plus.apiserver.models.UserToken;
import com.tui.Dietetyk_Plus.database.RedisService;
import com.tui.Dietetyk_Plus.database.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;


@Service
public class AuthService {
    private static final boolean DEBUG = true;

    private static final ArrayList<String> USER_PRIVILEGED_RESOURCES = new ArrayList<>(List.of("addDietetic"));
    private static final ArrayList<String> DIETETIC_PRIVILEGED_RESOURCES = new ArrayList<>(List.of("removeUsersDietetic", "openCreator", "addMeal", "removeMeal", "getPatientList", "updateUserDescription",
            "getIngredients", "addIngredient", "getMealList"));
    private static final ArrayList<String> DEFAULT_PRIVILEGED_RESOURCES = new ArrayList<>(List.of("getDieteticList", "changeImage", "getIngredient", "resetPassword"));

    private final RedisService redisService;

    @Autowired
    public AuthService(RedisService redisService) {
        this.redisService = redisService;
    }

    private final List<UserToken> userTokens = new ArrayList<>();

    private UserToken getUserToken(String token) {
        return userTokens.stream()
                .filter(t -> t.getToken().equals(token))
                .findFirst()
                .orElse(null);
    }

    public boolean hasAccess(String token, String resource) {
        /*FIXME: Dev bypass*/
        if (DEBUG) {
            return true;
        }
        
        if (token == null || token.isEmpty()) {
            return false;
        }
        UserToken userToken = getUserToken(token);
        if (userToken == null) {
            return false;
        }
        User user = redisService.getUser(userToken.getUserId());
        if (user == null) {
            return false;
        }
        return switch (user.getRole()) {
            case "user" ->
                    USER_PRIVILEGED_RESOURCES.contains(resource) || DEFAULT_PRIVILEGED_RESOURCES.contains(resource);
            case "dietetic" ->
                    DIETETIC_PRIVILEGED_RESOURCES.contains(resource) || DEFAULT_PRIVILEGED_RESOURCES.contains(resource);
            default -> false;
        };
    }

    public String getUserId(String token){
        return getUserToken(token) == null ? null : getUserToken(token).getUserId();
    }

    public String generateToken(String userId) {
        String token = UUID.randomUUID().toString();
        userTokens.add(new UserToken(token, userId, new Date(System.currentTimeMillis() + 3600000 * 24)));
        return token;
    }

    public void invalidateToken(String token) {
        userTokens.removeIf(t -> t.getToken().equals(token));
    }
    
    @Scheduled(fixedRate = 60000)
    public void invalidateTokens() {
        userTokens.removeIf(t -> t.getExpires().before(new Date()));
    }
}
