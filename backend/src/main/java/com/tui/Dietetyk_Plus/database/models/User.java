package com.tui.Dietetyk_Plus.database.models;

import com.tui.Dietetyk_Plus.database.models.objects.MedicalData;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private String name;
    private String surname;
    private String img_b64;
    private String birthdate;
    private String phone;
    private String email;
    private String password;

    private String role; // "user" or "dietetic"

    private int activityLevel; // User only
    private int dietRating; // User only
    private String mealsCount; // User only
    private String jobType; // User only
    private String dietPurpose; // User only
    private String dieteticId; // User only
    private String currentDietId; // User only
    private MedicalData medicalData; // User only
    private String lastUpdated; // User only

    private String description; //Dietetic only
}