package com.tui.Dietetyk_Plus.database.models;

import com.tui.Dietetyk_Plus.database.models.objects.DietPlanMeal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DietPlan implements Serializable {
    // Na każdy dzień lista posiłków
    private ArrayList<ArrayList<DietPlanMeal>> dietPlan;
    private String name;
    private String description;
    private int isPrivate;
}