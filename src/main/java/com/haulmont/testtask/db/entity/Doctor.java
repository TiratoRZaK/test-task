package com.haulmont.testtask.db.entity;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Doctor implements Serializable {
    @NotNull
    @Min(0)
    private Long id;
    @NotNull
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Содержит недопустимые символы")
    @Size(min=2, max=45, message = "Имя должно содержать от 2 до 45 символов")
    private String firstName;
    @NotNull
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Содержит недопустимые символы")
    @Size(min=2, max=45, message = "Фамилия должна содержать от 2 до 45 символов")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Zа-яА-Я]*$", message = "Содержит недопустимые символы")
    private String patronymic;
    @NotNull
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Содержит недопустимые символы")
    @Size(min=2, max=45, message = "Специализация должна содержать от 2 до 45 символов")
    private String specialization;

    public Doctor(String firstName, String lastName, String patronymic, String specialization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.specialization = specialization;
    }

    public String toString(){
        return lastName+" "+firstName+" "+patronymic;
    }
}
