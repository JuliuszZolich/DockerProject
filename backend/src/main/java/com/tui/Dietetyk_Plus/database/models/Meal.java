package com.tui.Dietetyk_Plus.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal implements Serializable {
    private String name;
    private String img_b64;
    private Object ingredients;
    private String recipe;
}