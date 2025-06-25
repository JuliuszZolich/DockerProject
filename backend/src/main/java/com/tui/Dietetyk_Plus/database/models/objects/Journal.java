package com.tui.Dietetyk_Plus.database.models.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journal implements Serializable {
    private String date;
    private float glucose;
    private float pressure;
    private float weight;
    private float pulse;
}