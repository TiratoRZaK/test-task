package com.haulmont.testtask.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;

@Data
@NoArgsConstructor
public class Recipe implements Serializable {
    @NotNull
    @Min(0)
    private Long id;
    @NotNull
    @Pattern(regexp = "^[a-zA-Zа-яА-Я ,.0-9]+$", message = "Содержит недопустимые символы")
    @Size(min = 10, message = "Описание рецепта должно быть длиннее 10 символов")
    private String description;
    @NotNull
    @Min(0)
    private Long doctor_id;
    @NotNull
    @Min(0)
    private Long patient_id;
    @NotNull
    private Date date_create;
    @NotNull
    @Min(0)
    private int validityInDay;
    @NotNull
    private Priority priority;

    public Recipe(String description, Long doctor_id, Long patient_id, Date date_create, int validityInDay, String priority) {
        this.description = description;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.date_create = date_create;
        this.validityInDay = validityInDay;
        this.priority = Priority.valueOf(priority);
    }
}
