package com.tui.Dietetyk_Plus.database.models;

import com.tui.Dietetyk_Plus.database.models.objects.Macros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient implements Serializable {
    private String name;
    private String unit;
    private int categoryId;
    private Macros macros;
}