package com.haulmont.testtask.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class Patient implements Serializable {
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
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Содержит недопустимые символы")
    private String patronymic;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(\\+?)[0-9]{11}$", message = "Неверный формат номера")
    private String phone;

    public Patient(String firstName, String lastName, String patronymic, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return lastName+" "+firstName+" "+patronymic;
    }
}
