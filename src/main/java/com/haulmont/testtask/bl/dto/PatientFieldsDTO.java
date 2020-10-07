package com.haulmont.testtask.bl.dto;

import com.haulmont.testtask.db.entity.Patient;
import com.vaadin.data.Binder;
import com.vaadin.ui.TextField;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatientFieldsDTO {
    private TextField fName;
    private TextField lName;
    private TextField patronymic;
    private TextField phone;
    private Binder<Patient> binder;

    public TextField[] getFields(){
        return new TextField[]{
                fName, lName, patronymic, phone
        };
    }
}