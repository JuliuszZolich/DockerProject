package com.tui.Dietetyk_Plus.database.models.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Macros implements Serializable {
    private float kcal;
    private float proteins;
    private float carbohydrates;
    private float fats;
    private float fiber;
    private float sugar;
}