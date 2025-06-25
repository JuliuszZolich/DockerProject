package com.tui.Dietetyk_Plus.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.Dietetyk_Plus.database.models.DietPlan;
import com.tui.Dietetyk_Plus.database.models.Ingredient;
import com.tui.Dietetyk_Plus.database.models.Meal;
import com.tui.Dietetyk_Plus.database.models.User;
import com.tui.Dietetyk_Plus.database.models.objects.DietPlanMeal;
import com.tui.Dietetyk_Plus.database.models.objects.Journal;
import com.tui.Dietetyk_Plus.database.models.objects.MedicalData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RedisService {

    private static final String USER_KEY_PREFIX = "user:";
    private static final String MEAL_KEY_PREFIX = "meal:";
    private static final String INGREDIENT_KEY_PREFIX = "ingredient:";
    private static final String DIET_PLAN_KEY_PREFIX = "dietPlan:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate,
                        ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public void saveUser(String UUID, User user) {
        Map<String, Object> userMap = objectMapper.convertValue(user, Map.class);
        userMap.put("medicalData", objectMapper.convertValue(user.getMedicalData(), Map.class));
        redisTemplate.opsForHash().putAll(USER_KEY_PREFIX + UUID, userMap);
    }

    public String getUserKey(String email, String password) {
        Set<String> keys = redisTemplate.keys("user:*");
        if (keys.isEmpty()) return null;
        for (String key : keys) {
            String storedEmail = (String) redisTemplate.opsForHash().get(key, "email");
            String storedPassword = (String) redisTemplate.opsForHash().get(key, "password");

            if (email.equals(storedEmail) && password.equals(storedPassword)) {
                return key.substring(USER_KEY_PREFIX.length());
            }
        }
        return null;
    }

    // Used only for password recovery
    public String getUserKey(String email) {
        Set<String> keys = redisTemplate.keys("user:*");
        if (keys.isEmpty()) return null;
        for (String key : keys) {
            String storedEmail = (String) redisTemplate.opsForHash().get(key, "email");
            if (email.equals(storedEmail)) {
                return key.substring(USER_KEY_PREFIX.length());
            }
        }
        return null;
    }

    public boolean checkForUsedEmail(String email){
        Set<String> keys = redisTemplate.keys("user:*");
        if (keys.isEmpty()) return false;
        for (String key : keys) {
            String storedEmail = (String) redisTemplate.opsForHash().get(key, "email");
            if (email.equals(storedEmail)) {
                return true;
            }
        }
        return false;
    }

    public User getUser(String id) {
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries("user:"+id);
        if (userMap.isEmpty()) {
            return null;
        }
        return objectMapper.convertValue(userMap, User.class);
    }

    public void updateUser(String id, User user) {
        Map<Object, Object> existingUserMap = redisTemplate.opsForHash().entries(USER_KEY_PREFIX + id);
        Map<String, Object> userMap = objectMapper.convertValue(user, Map.class);
        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() == null) {
                continue;
            }
            if (!existingUserMap.get(key).equals(entry.getValue())) {
                existingUserMap.put(key, entry.getValue());
            }
        }
        // TODO: To jest zakomentowane z jakiegoś powodu
//        existingUserMap.put("medicalData", objectMapper.convertValue(existingUserMap.getMedicalData(), Map.class));
        redisTemplate.opsForHash().putAll(USER_KEY_PREFIX + id, existingUserMap);
    }

    public void saveRecoveryKey(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public String getRecoveryKey(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }


    //    TODO Dodać publiczną klasę na dane dla dietetyków, a nie wysyłać Usera
    public List<Entry<String,User>> getAllUsersByDietetic(String dieteticId) {
        if (dieteticId == null || dieteticId.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> keys = redisTemplate.keys(USER_KEY_PREFIX + "*");
        if (keys.isEmpty()) {
            return null;
        }
        return keys.stream()
                .map(key -> Map.entry(key.substring(USER_KEY_PREFIX.length()), getUser(key.substring(USER_KEY_PREFIX.length()))))
                .filter(entry -> entry.getValue() != null && dieteticId.equals(entry.getValue().getDieteticId()))
                .collect(Collectors.toList());
    }

    public List<Entry<String,User>> getDietitians() {
        Set<String> keys = redisTemplate.keys(USER_KEY_PREFIX + "*");
        if (keys.isEmpty()) {
            return null;
        }
        return keys.stream()
                .map(key -> Map.entry(key.substring(USER_KEY_PREFIX.length()), getUser(key.substring(USER_KEY_PREFIX.length()))))
                .filter(entry -> entry.getValue() != null && "dietetic".equals(entry.getValue().getRole()))
                .collect(Collectors.toList());
    }

    public void deleteUser(String id) {
        redisTemplate.delete(USER_KEY_PREFIX + id);
    }

    public Ingredient getIngredient(String id) {
        Map<Object, Object> ingredientMap = redisTemplate.opsForHash().entries(INGREDIENT_KEY_PREFIX + id);
        if (ingredientMap.isEmpty()) {
            return null;
        }
        return objectMapper.convertValue(ingredientMap, Ingredient.class);
    }

    private String getIngredientsId(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }
        AtomicReference<String> id = new AtomicReference<>();
        this.getAllIngredients().stream()
                .filter(entry -> entry.getValue().equals(ingredient))
                .findFirst()
                .ifPresent(entry -> {
                    id.set(entry.getKey());
                });
        if (id.get() == null) {
            // Jeśli nie znaleziono składnika, to dodaj go do bazy danych
            String uuid = UUID.randomUUID().toString();
            this.saveIngredient(uuid, ingredient);
            return "ingredient:" + uuid;
        }
        return id.get();
    }

    public List<Entry<String, Ingredient>> getAllIngredients(){
        Set<String> keys = redisTemplate.keys(INGREDIENT_KEY_PREFIX + "*");
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return keys.stream()
                .map(key->  Map.entry(key.substring(INGREDIENT_KEY_PREFIX.length()), getIngredient(key.substring(INGREDIENT_KEY_PREFIX.length()))))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toList());
    }

    public Map<String,String> getIngredientsForMeal(String mealId){
        Meal meal = getMeal(mealId);
        if (meal == null) {
            return null;
        }
        return (Map<String,String>)meal.getIngredients();
    }

    public void saveIngredient(String UUID, Ingredient ingredient) {
        Map<String, Object> ingredientMap = objectMapper.convertValue(ingredient, Map.class);
        redisTemplate.opsForHash().putAll(INGREDIENT_KEY_PREFIX + UUID, ingredientMap);
    }

    public void deleteIngredient(String ingredientId) {
        redisTemplate.delete(INGREDIENT_KEY_PREFIX + ingredientId);
    }

    public void updateIngredient(String id, Ingredient ingredient) {
        Map<Object, Object> existingIngredientMap = redisTemplate.opsForHash().entries(INGREDIENT_KEY_PREFIX + id);
        Map<String, Object> ingredientMap = objectMapper.convertValue(ingredient, Map.class);
        for (Map.Entry<String, Object> entry : ingredientMap.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() == null) {
                continue;
            }
            if (!existingIngredientMap.get(key).equals(entry.getValue())) {
                existingIngredientMap.put(key, entry.getValue());
            }
        }
        redisTemplate.opsForHash().putAll(INGREDIENT_KEY_PREFIX + id, existingIngredientMap);
    }

    public Meal getMeal(String id) {
        Map<Object, Object> mealMap = redisTemplate.opsForHash().entries(MEAL_KEY_PREFIX + id);
        if (mealMap.isEmpty()) {
            return null;
        }
        return objectMapper.convertValue(mealMap, Meal.class);
    }

    public List<Entry<String, Meal>> getAllMeals(){
        Set<String> keys = redisTemplate.keys(MEAL_KEY_PREFIX + "*");
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return keys.stream()
                .map(key->  Map.entry(key.substring(MEAL_KEY_PREFIX.length()), getMeal(key.substring(MEAL_KEY_PREFIX.length()))))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toList());
    }

    public void saveMeal(String UUID, Meal meal) {
        Map<String, Object> mealMap = objectMapper.convertValue(meal, Map.class);
        redisTemplate.opsForHash().putAll(MEAL_KEY_PREFIX + UUID, mealMap);
    }

    public void deleteMeal(String mealId) {
        redisTemplate.delete(MEAL_KEY_PREFIX + mealId);
    }

    public void updateMeal(String id, Meal meal) {
        Map<Object, Object> existingMealMap = redisTemplate.opsForHash().entries(MEAL_KEY_PREFIX + id);
        Map<String, Object> mealMap = objectMapper.convertValue(meal, Map.class);
        for (Map.Entry<String, Object> entry : mealMap.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() == null) {
                continue;
            }
            if (!existingMealMap.get(key).equals(entry.getValue())) {
                existingMealMap.put(key, entry.getValue());
            }
        }
        redisTemplate.opsForHash().putAll(MEAL_KEY_PREFIX + id, existingMealMap);
    }

    private DietPlan convertRedisDietPlanToSendableDietPlan(@NotNull DietPlan dietPlan) {
        // Na kążdy dzień
        for (ArrayList<DietPlanMeal> day: dietPlan.getDietPlan()){
            // Na każdy posiłek
            for (DietPlanMeal meal: day){
                // Jeśli meal jest w formie Entry, to pobierz Meal z bazy danych
                if (meal.getMeal() instanceof String){
                    meal.setMeal(this.getMeal((String) meal.getMeal()));
                }
            }
        }
        return dietPlan;
    }

    private String getMealId(Meal meal) {
        if (meal == null) {
            return null;
        }
        String id = null;
        for (Entry<String, Meal> entry : this.getAllMeals()) {
            if (entry.getValue().equals(meal)) {
                id = entry.getKey();
                break;
            }
        }
        return id;
    }

    private DietPlan convertSendableDietPlanToRedisDietPlan(@NotNull DietPlan dietPlan) {
        // Zamień DietPlanMeal na Entry<String, UUID (String)>
        for (ArrayList<DietPlanMeal> day: dietPlan.getDietPlan()){
            for (DietPlanMeal meal: day){
                if (meal.getMeal() instanceof Meal || meal.getMeal() instanceof HashMap<?,?>) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String mealId = this.getMealId(objectMapper.convertValue(meal.getMeal(), Meal.class));
                    if (mealId != null) {
                        meal.setMeal(mealId);
                    }
                }
            }
        }
        return dietPlan;
    }

    public void saveDietPlan(String UUID, DietPlan dietPlan) {
        DietPlan convertedDietPlan = convertSendableDietPlanToRedisDietPlan(dietPlan);
        String key = DIET_PLAN_KEY_PREFIX + UUID;
        redisTemplate.opsForValue().set(key, convertedDietPlan);
    }

    public List<Entry<String, DietPlan>> getAllDietPlans(){
        Set<String> keys = redisTemplate.keys("dietPlan:*");
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<DietPlan> dietPlans = new ArrayList<>();
        List<String> dietKeys = new ArrayList<>();
        for (String key : keys) {
            Object dietPlanObj = redisTemplate.opsForValue().get(key);
            if (dietPlanObj != null) {
                DietPlan dietPlan = objectMapper.convertValue(dietPlanObj, DietPlan.class);
                if (dietPlan.getIsPrivate() == 1){
                    continue;
                }
                dietPlans.add(convertRedisDietPlanToSendableDietPlan(dietPlan));
                dietKeys.add(key.replace(DIET_PLAN_KEY_PREFIX, ""));
            }
        }

        return IntStream.range(0, dietPlans.size())
                .mapToObj(i -> Map.entry(dietKeys.get(i), dietPlans.get(i)))
                .collect(Collectors.toList());
    }

    public DietPlan getDietPlan(String id) {
        String key = DIET_PLAN_KEY_PREFIX + id;
        Object dietPlanObj = redisTemplate.opsForValue().get(key);
        if (dietPlanObj == null)   {
            return null;
        }

        DietPlan dietPlan = objectMapper.convertValue(dietPlanObj, DietPlan.class);
        return convertRedisDietPlanToSendableDietPlan(dietPlan);
    }

    public void updateDietPlan(String id, DietPlan dietPlan) {
        String key = DIET_PLAN_KEY_PREFIX + id;
        DietPlan updatedDietPlan = convertSendableDietPlanToRedisDietPlan(dietPlan);
        redisTemplate.opsForValue().set(key, updatedDietPlan);
    }

    public void deleteDietPlan(String id) {
        String key = DIET_PLAN_KEY_PREFIX + id;
        redisTemplate.delete(key);
    }

    public boolean checkIfDietPlanExists(String id) {
        String key = DIET_PLAN_KEY_PREFIX + id;
        return redisTemplate.hasKey(key);
    }

    public boolean updateJournal(String id, Journal[] journal) {
        String key = USER_KEY_PREFIX + id;
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
        if (userMap.isEmpty()) {
            return false;
        }
        User user = objectMapper.convertValue(userMap, User.class);
        MedicalData medicalData = user.getMedicalData();
        if (medicalData == null) {
            return false;
        }
        medicalData.setJournal(journal);
        user.setMedicalData(medicalData);
        saveUser(id, user);
        return true;
    }
}