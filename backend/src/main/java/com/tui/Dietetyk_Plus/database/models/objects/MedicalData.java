package com.tui.Dietetyk_Plus.database.models.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalData implements Serializable {
     private int height;
     private int weight;
     private boolean gender;
     private String[] diseases;
     private String[] allergies;
     private Journal[] journal;
}