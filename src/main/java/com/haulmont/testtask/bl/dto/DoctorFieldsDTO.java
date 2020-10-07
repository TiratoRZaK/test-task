package com.haulmont.testtask.bl.dto;

import com.haulmont.testtask.db.entity.Doctor;
import com.vaadin.data.Binder;
import com.vaadin.ui.TextField;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DoctorFieldsDTO {
    private TextField fName;
    private TextField lName;
    private TextField patronymic;
    private TextField specialization;
    private Binder<Doctor> binder;

    public TextField[] getFields(){
        return new TextField[]{
                fName, lName, patronymic, specialization
        };
    }
}
