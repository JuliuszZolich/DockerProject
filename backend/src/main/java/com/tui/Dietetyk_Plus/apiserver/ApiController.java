package com.tui.Dietetyk_Plus.apiserver;

import com.tui.Dietetyk_Plus.apiclient.ApiClient;
import com.tui.Dietetyk_Plus.apiserver.models.LoginCredentials;
import com.tui.Dietetyk_Plus.apiserver.models.VerificationRequest;
import com.tui.Dietetyk_Plus.database.RedisService;

import com.tui.Dietetyk_Plus.database.models.DietPlan;
import com.tui.Dietetyk_Plus.database.models.Ingredient;
import com.tui.Dietetyk_Plus.database.models.Meal;
import com.tui.Dietetyk_Plus.database.models.User;
import com.tui.Dietetyk_Plus.database.models.objects.Journal;
import com.tui.Dietetyk_Plus.database.models.objects.Macros;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/")
public class ApiController {

    private final RedisService redisService;
    private final AuthService authService;
    private final ApiClient apiClient;
    private final PasswordResetService passwordResetService;

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Autowired
    public ApiController(RedisService redisService, AuthService authService, ApiClient apiClient, PasswordResetService passwordResetService) {
        this.redisService = redisService;
        this.authService = authService;
        this.apiClient = apiClient;
        this.passwordResetService = passwordResetService;
    }

    //REGISTER I LOGIN

    @PostMapping("/register")
    public ResponseEntity<?> setData(@RequestBody User user)  {
        String newUUID = generateUUID();
        redisService.saveUser(newUUID, user);
        return ResponseEntity.ok("Dane zapisane dla klucza: " + newUUID);
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<?> checkEmail(@RequestBody String email) {
        if (redisService.checkForUsedEmail(email)) {
            return ResponseEntity.status(409).body("Email już zajęty");
        }
        return ResponseEntity.ok("Email dostępny");
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginCredentials loginCredentials, HttpServletResponse response)  {
        String userKey = redisService.getUserKey(loginCredentials.getEmail(), loginCredentials.getPassword());
        if (userKey == null) {
            return ResponseEntity.status(401).body("Niepoprawne dane logowania");
        }
        User user = redisService.getUser(userKey);
        if (user == null) {
            // Nie powinno się zdarzyć
            return ResponseEntity.status(404).body("Nie znaleziono użytkownika");
        }
        String token = authService.generateToken(userKey);
        response.addHeader("Authorization", token);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String userKey) {
        authService.invalidateToken(userKey);
        return ResponseEntity.ok("Wylogowano pomyślnie");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerificationRequest verificationRequest, HttpServletResponse response) {
        if(!passwordResetService.validateResetToken(verificationRequest.getToken(), verificationRequest.getEmail())){
            return ResponseEntity.status(401).body("Niepoprawny token");
        }
        String userKey = redisService.getUserKey(verificationRequest.getEmail());
        if (userKey == null) {
            return ResponseEntity.status(401).body("Niepoprawne dane logowania");
        }
        String newToken = authService.generateToken(userKey);
        response.addHeader("Authorization", newToken);
        return ResponseEntity.ok("Token zweryfikowany");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String token, @RequestBody String password) {
        if (!authService.hasAccess(token, "resetPassword")){
            return ResponseEntity.status(401).body("Niepoprawny token");
        }
        String userId = authService.getUserId(token);
        User user = redisService.getUser(userId);
        if (user == null) {
            return ResponseEntity.status(401).body("Niepoprawne dane logowania");
        }
        password = password.replace("\"", "");
        user.setPassword(password);
        redisService.updateUser(userId, user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/checkRecoveryEmail")
    public ResponseEntity<?> checkRecoveryEmail(@RequestBody String email) {
        email = email.replace("\"", "");
        if (!redisService.checkForUsedEmail(email)) {
            return ResponseEntity.status(409).body("Email nie istnieje");
        }
        if (passwordResetService.generateResetToken(email)) {
            return ResponseEntity.ok("Kod został wysłany na email");
        } else {
            return ResponseEntity.status(500).body("Nie można wysłać kodu");
        }
    }

    // USER

    @GetMapping("/dietitians")
    public ResponseEntity<?> getDietitans(@RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getDieteticList")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        List<?> dietitans  = redisService.getDietitians();
        if (dietitans != null) {
            return ResponseEntity.ok(dietitans);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> addUsersDietetic(String dieteticId, String userKey) {
        if (!authService.hasAccess(userKey, "addUsersDietetic")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        String userId = authService.getUserId(userKey);
        User user = redisService.getUser(userId);
        if (user == null) {
            // Nie powinno się zdarzyć
            return ResponseEntity.status(401).body("Niepoprawne dane");
        }
        user.setDieteticId(dieteticId);
        redisService.updateUser(userId, user);
        return ResponseEntity.ok("Dietetyk dodany dla klucza: " + userId);
    }


    // DIETETIC


    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(@RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getPatients")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        String userId = authService.getUserId(userKey);
        List<?> patients  = redisService.getAllUsersByDietetic(userId);
        if (patients != null) {
            return ResponseEntity.ok(patients);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> removeUsersDietetic(String patientId, String userKey) {
        if (!authService.hasAccess(userKey, "removeUsersDietetic")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        User patient = redisService.getUser(patientId);
        if (patient == null) {
            // Nie powinno się zdarzyć
            return ResponseEntity.status(401).body("Niepoprawne dane");
        }
        patient.setDieteticId("");
        redisService.updateUser(patientId, patient);
        return ResponseEntity.ok("Dietetyk usunięty dla klucza: " + patientId);
    }

    @GetMapping("/update/patient/{userid}/{dietId}")
    public ResponseEntity<?> updatePatientDiet(@PathVariable String userid, @PathVariable String dietId, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "updatePatientDiet")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        User patient = redisService.getUser(userid);
        if (patient == null) {
            return ResponseEntity.status(404).body("Nie znaleziono pacjenta o podanym ID");
        }

        if (!redisService.checkIfDietPlanExists(dietId) && !userid.equals(dietId)) {
            return ResponseEntity.status(404).body("Nie znaleziono diety o podanym ID");
        }
        patient.setCurrentDietId(dietId);
        redisService.updateUser(userid, patient);
        return ResponseEntity.ok("Dieta pacjenta zaktualizowana");
    }

    // DIETETIC + USER

    @PostMapping("/update/user")
    public ResponseEntity<?> getData(@RequestHeader("Authorization") String userKey, @RequestBody User user) {
        if (!authService.hasAccess(userKey, "updateUser")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        String userId = authService.getUserId(userKey);
        User existingUser = redisService.getUser(userId);
        if (existingUser == null) {
            // Nie powinno się zdarzyć
            return ResponseEntity.status(401).body("Niepoprawne dane");
        }

        if (!existingUser.getDescription().equals(user.getDescription())){
            if (!authService.hasAccess(userKey, "updateUserDescription")) {
                return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
            }
        }

        redisService.updateUser(userId, user);
        return ResponseEntity.ok("Dane zaktualizowane dla klucza: " + userId);
    }

    @GetMapping("/update/dietetic/{type}/{id}")
    public ResponseEntity<?> changeDietetic(@PathVariable String type, @PathVariable String id, @RequestHeader("Authorization") String userKey) {
        return switch (type) {
            case "remove" -> removeUsersDietetic(id, userKey);
            case "add" -> addUsersDietetic(id, userKey);
            default -> ResponseEntity.status(400).body("Niepoprawny typ operacji");
        };
    }

    // DIET PLANS
    @PostMapping("/dietPlan")
    public ResponseEntity<?> addDietPlan(@RequestBody DietPlan dietPlan, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "addDietPlan")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        String newUUID = generateUUID();
        redisService.saveDietPlan(newUUID, dietPlan);
        return ResponseEntity.ok(newUUID);
    }

    @GetMapping("/dietPlans")
    public ResponseEntity<?> getAllDietPlans(@RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getDietPlanList")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        List<?> dietPlans = redisService.getAllDietPlans();
        if (dietPlans != null) {
            return ResponseEntity.ok(dietPlans);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dietPlan/{id}")
    public ResponseEntity<?> getDietPlan(@PathVariable String id, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getDietPlan")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        DietPlan dietPlan = redisService.getDietPlan(id);
        if (dietPlan != null) {
            return ResponseEntity.ok(dietPlan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update/dietPlan/{id}")
    public ResponseEntity<?> updateDietPlan(@PathVariable String id, @RequestBody DietPlan dietPlan, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "updateDietPlan")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        redisService.updateDietPlan(id, dietPlan);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/delete/dietPlan/{id}")
    public ResponseEntity<?> deleteDietPlan(@PathVariable String id, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "removeDietPlan")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        redisService.deleteDietPlan(id);
        return ResponseEntity.ok("Dieta o ID " + id + " została usunięta");
    }
    // MEALS


    @GetMapping("/meals")
    public ResponseEntity<?> getAllMeals(@RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getMealList")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        List<?> meals  = redisService.getAllMeals();
        return ResponseEntity.ok(meals);

    }

    @GetMapping("/meal/{id}")
    public ResponseEntity<?> getMealData(@PathVariable String id, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getMeal")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        Meal meal = redisService.getMeal(id);
        if (meal != null) {
            return ResponseEntity.ok(meal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/meal")
    public ResponseEntity<?> addMeal(@RequestBody Meal meal, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "addMeal")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        String newUUID = generateUUID();
        redisService.saveMeal(newUUID, meal);
        return ResponseEntity.ok(newUUID);
    }

    @DeleteMapping("/delete/meal/{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable String id, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "removeMeal")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        redisService.deleteMeal(id);
        return ResponseEntity.ok("Dane usunięte dla klucza: " + id);
    }

    @PostMapping("/update/meal/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable String id, @RequestBody Meal meal, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "updateMeal")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        if (redisService.getMeal(id) == null) {
            return ResponseEntity.status(404).body("Nie znaleziono posiłku o podanym ID");
        }
        redisService.updateMeal(id, meal);
        return ResponseEntity.ok("");
    }

    // INGREDIENTS

    @GetMapping("/ingredients")
    public ResponseEntity<?> getAllIngredients(@RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getIngredients")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        List<?> ingredients  = redisService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/ingredient/{id}")
    public ResponseEntity<?> getIngredientData(@PathVariable String id, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "getIngredient")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        Ingredient ingredient = redisService.getIngredient(id);
        if (ingredient != null) {
            return ResponseEntity.ok(ingredient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/ingredient")
    public ResponseEntity<?> addIngredient(@RequestBody Ingredient ingredient, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "addIngredient")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        Macros macros = new Macros();
        try {
            macros = apiClient.getNutritionalValuesForIngredient(ingredient);
        } catch (Exception e) {
            macros.setFats(0);
            macros.setFiber(0);
            macros.setKcal(0);
            macros.setProteins(0);
            macros.setSugar(0);
            macros.setCarbohydrates(0);
//            return ResponseEntity.status(500).body("Nie można pobrać wartości odżywczych");
        }
        ingredient.setMacros(macros);
        String newUUID = generateUUID();
        redisService.saveIngredient(newUUID, ingredient);
        Map.Entry<String, Macros> entry = new AbstractMap.SimpleEntry<>(newUUID, macros);
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/delete/ingredient/{id}")
    public ResponseEntity<?> deleteIngredient(@PathVariable String id, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "removeIngredient")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        redisService.deleteIngredient(id);
        return ResponseEntity.ok("Dane usunięte dla klucza: " + id);
    }

    @PostMapping("/update/ingredient/{id}")
    public ResponseEntity<?> updateIngredient(@PathVariable String id, @RequestBody Ingredient ingredient, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "updateIngredient")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        if (redisService.getIngredient(id) == null) {
            return ResponseEntity.status(404).body("Nie znaleziono składnika o podanym ID");
        }
        redisService.updateIngredient(id, ingredient);
        return ResponseEntity.ok("");
    }

    @PostMapping("/journal")
    public ResponseEntity<?> updateJournal(@RequestBody Journal[] journalData, @RequestHeader("Authorization") String userKey) {
        if (!authService.hasAccess(userKey, "updateJournal")) {
            return ResponseEntity.status(403).body("Brak uprawnień do tej operacji");
        }
        String userId = authService.getUserId(userKey);
        User user = redisService.getUser(userId);
        if (user == null) {
            return ResponseEntity.status(404).body("Nie znaleziono użytkownika");
        }
        if(redisService.updateJournal(userId, journalData)) {
            return ResponseEntity.ok("Dziennik zaktualizowany");
        } else {
            return ResponseEntity.status(500).body("Nie można zaktualizować dziennika");
        }
    }
}
