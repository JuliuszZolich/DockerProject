package com.tui.Dietetyk_Plus.database.models.objects;

import com.tui.Dietetyk_Plus.database.models.Meal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DietPlanMeal implements Serializable {
    private String Name;
    /**
    * W przypadku otrzymywanego z diet planu: String
     * * <p>
     *W przypadku wysyłanego do użytkownika: {@link Meal}
    */
    private Object meal;
}