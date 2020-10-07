package com.haulmont.testtask.ui.layouts;

import com.haulmont.testtask.db.dao.PatientDAOImpl;
import com.haulmont.testtask.db.dao.interfaces.PatientDAO;
import com.haulmont.testtask.db.entity.Patient;
import com.haulmont.testtask.db.entity.Priority;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class FilterToRecipesLayout extends HorizontalLayout {
    private RecipeLayout recipeLayout;
    private PatientDAO patientDAO = new PatientDAOImpl();
    private Label name;
    private TextField filterByDesc;
    private ComboBox<Patient> patientComboBox;
    private ComboBox<String> priorityComboBox;
    private CheckBox activityCheckBox;

    public FilterToRecipesLayout(RecipeLayout recipeLayout) {
        this.recipeLayout = recipeLayout;
        name = new Label("Фильтры: ");
        name.setContentMode(ContentMode.PREFORMATTED);

        filterByDesc = new TextField();
        filterByDesc.setCaption("Описание");
        patientComboBox = new ComboBox<>("Пациент");
        priorityComboBox = new ComboBox<>("Приоритет");
        activityCheckBox = new CheckBox("Применить фильтры",false);
        activityCheckBox.setId("activityFilters");

        addComponents(name, filterByDesc, patientComboBox, priorityComboBox, activityCheckBox);
        setComponentAlignment(activityCheckBox, Alignment.MIDDLE_CENTER);
    }

    public void buildPanelFilters() {
        filterByDesc.setPlaceholder("Фильтр по описанию...");
        filterByDesc.setValueChangeMode(ValueChangeMode.LAZY);
        filterByDesc.addValueChangeListener(change -> activityCheckBox.setValue(false));

        patientComboBox.setItems(patientDAO.getAll());
        patientComboBox.setItemCaptionGenerator(Patient::toString);
        patientComboBox.addValueChangeListener(change -> activityCheckBox.setValue(false));

        ArrayList<String> priorityNames = new ArrayList<>();
        for(Priority priority : Priority.values()){
            priorityNames.add(priority.getName());
        }
        priorityComboBox.setItems(priorityNames);
        priorityComboBox.addValueChangeListener(change -> activityCheckBox.setValue(false));

        activityCheckBox.addValueChangeListener(check -> {
            if(check.getValue()){
                log.info("Фильтры включены:" +
                        " По описанию - "+filterByDesc.getValue()+
                        " По пациенту - "+patientComboBox.getValue()+
                        " По приоритету - "+priorityComboBox.getValue()
                );
                recipeLayout.fillTableFilteredData(
                        filterByDesc.getValue(), patientComboBox.getValue(), Priority.getByName(priorityComboBox.getValue()));
            }else {
                log.info("Фильтры отключены");
                recipeLayout.fillTableData();
            }
        });
    }
}
