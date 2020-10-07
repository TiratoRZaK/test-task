package com.haulmont.testtask.bl.dto;

import com.haulmont.testtask.db.entity.Doctor;
import com.haulmont.testtask.db.entity.Patient;
import com.haulmont.testtask.db.entity.Recipe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RecipeDTO extends Recipe {
    @NotNull
    private Patient patient;
    @NotNull
    private Doctor doctor;
    @NotNull
    @NotEmpty
    private String priorityName;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[0-9]+$", message = "Должно быть положительным числом")
    private String validityInDayToString;
    @NotNull
    private LocalDate localDate;

    public RecipeDTO(String description, Patient patient, Doctor doctor, Date date_create, int validityInDay, String priority) {
        super(description, doctor.getId(), patient.getId(), date_create, validityInDay, priority);
        this.patient = patient;
        this.doctor = doctor;
        priorityName = priority;
        validityInDayToString = String.valueOf(validityInDay);
        localDate = date_create.toLocalDate();
    }
}
