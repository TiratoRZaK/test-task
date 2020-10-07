package com.haulmont.testtask.ui;

import com.haulmont.testtask.ui.layouts.DoctorLayout;
import com.haulmont.testtask.ui.layouts.PatientLayout;
import com.haulmont.testtask.ui.layouts.RecipeLayout;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Theme(ValoTheme.THEME_NAME)
public class MainUI extends UI {
    @Override
    protected void init(VaadinRequest request) {
        log.info("Старт приложения");
        TabSheet tabSheet = new TabSheet();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponent(tabSheet);

        DoctorLayout doctorLayout = new DoctorLayout(this);
        doctorLayout.setCaption("Доктора");
        tabSheet.addTab(doctorLayout).setIcon(VaadinIcons.SPECIALIST);

        PatientLayout patientLayout = new PatientLayout(this);
        patientLayout.setCaption("Пациенты");
        tabSheet.addTab(patientLayout).setIcon(VaadinIcons.MEH_O);

        RecipeLayout recipeLayout = new RecipeLayout(this);
        recipeLayout.setCaption("Рецепты");
        tabSheet.addTab(recipeLayout).setIcon(VaadinIcons.CLIPBOARD_TEXT);

        setContent(mainLayout);
    }

    public static TextField createCustomBindValidateField(String caption, String placeHolder, Binder binder, String nameFieldToBind){
        TextField customField = new TextField(caption);
        customField.setPlaceholder(placeHolder);
        binder.forField(customField).bind(nameFieldToBind);
        log.info("Формирование кастомного поля: "+customField);
        return customField;
    }
}