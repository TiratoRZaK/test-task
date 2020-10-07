package com.haulmont.testtask.ui.layouts;

import com.haulmont.testtask.bl.dto.RecipeDTO;
import com.haulmont.testtask.db.dao.DoctorDAOImpl;
import com.haulmont.testtask.db.dao.PatientDAOImpl;
import com.haulmont.testtask.db.dao.RecipeDAOImpl;
import com.haulmont.testtask.db.dao.interfaces.DoctorDAO;
import com.haulmont.testtask.db.dao.interfaces.PatientDAO;
import com.haulmont.testtask.db.dao.interfaces.RecipeDAO;
import com.haulmont.testtask.db.entity.Doctor;
import com.haulmont.testtask.db.entity.Patient;
import com.haulmont.testtask.db.entity.Priority;
import com.haulmont.testtask.db.entity.Recipe;
import com.haulmont.testtask.ui.MainUI;
import com.haulmont.testtask.ui.modalWindow.AddEditWindow;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
public class RecipeLayout extends VerticalLayout {
    private MainUI main;
    private PatientDAO patientDAO = new PatientDAOImpl();
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private RecipeDAO recipeDAO = new RecipeDAOImpl();

    private FilterToRecipesLayout filters;
    private Grid<Recipe> grid;

    public RecipeLayout(MainUI main) {
        this.main = main;
        log.info("Инициализация страницы 'Рецепты'");

        filters = new FilterToRecipesLayout(this);

        buildTable();

        Button butAddRecipe = new Button("Добавить новый рецепт");
        butAddRecipe.addClickListener(clickEvent -> clickButAddNewRecipe());

        filters.buildPanelFilters();

        addComponents(filters, grid, butAddRecipe);
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        setComponentAlignment(filters, Alignment.TOP_CENTER);
        setComponentAlignment(butAddRecipe, Alignment.BOTTOM_CENTER);
    }


    public void fillTableFilteredData(String desc, Patient patient, Priority priority) {
        grid.setItems(recipeDAO.getAllByFilters(desc, patient, priority));
        log.info("Таблица рецептов заполнена отфильтрованными данными: " +
                "по описанию="+desc+", по пациенту="+patient+", по приоритету="+priority);
    }

    public void fillTableData(){
        ((CheckBox)filters.getComponent(4)).setValue(false);
        grid.setItems(recipeDAO.getAll());
        log.info("Таблица рецептов заполнена обновлёнными данными");
    }

    private void buildTable(){
        log.info("Началось постоение таблицы");
        grid = new Grid<>();
        fillTableData();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addColumn(Recipe::getId).setCaption("№").setWidth(75);
        grid.addColumn(Recipe::getDescription).setCaption("Описание").setExpandRatio(-10);
        grid.addColumn(recipe -> patientDAO.getById(recipe.getPatient_id())).setCaption("Пациент");
        grid.addColumn(recipe -> doctorDAO.getById(recipe.getDoctor_id())).setCaption("Доктор");
        grid.addColumn(Recipe::getDate_create).setCaption("Дата выписки");
        grid.addColumn(Recipe::getValidityInDay).setCaption("Срок действия");
        grid.addColumn(recipe -> recipe.getPriority().getName()).setCaption("Приоритет");
        grid.addColumn(recipe -> "Изменить", new ButtonRenderer<>(this::clickButEdit)).setId("edit").setMaximumWidth(130);
        grid.addColumn(recipe -> "Удалить", new ButtonRenderer<>(this::clickButDelete)).setId("del").setMaximumWidth(130);

        grid.getHeaderRow(0).join("edit", "del").setText("Действия");
        log.info("Завершилось постоение таблицы");
    }

    private void clickButAddNewRecipe(){
        log.info("Открылось окно добавления нового рецепта");
        AddEditWindow addEditWindow = new AddEditWindow("Добавление нового рецепта");

        BeanValidationBinder<RecipeDTO> binder = new BeanValidationBinder<>(RecipeDTO.class);
        RecipeDTO recipe = new RecipeDTO();
        binder.setBean(recipe);

        TextArea desc = initBindedTextField(
                new TextArea(),"Описание", "Описание рецепта...", binder, "description");

        ComboBox<Patient> patientComboBox = createComboBoxField(
                patientDAO.getAll(),
                Patient::toString,
                "Пациент",
                binder,
                "patient"
        );

        ComboBox<Doctor> doctorComboBox = createComboBoxField(
                doctorDAO.getAll(),
                Doctor::toString,
                "Доктор",
                binder,
                "doctor"
        );


        ComboBox<Priority> priorityComboBox = createComboBoxField(
                Arrays.asList(Priority.values()),
                Priority::getName,
                "Приоритет",
                binder,
                "priority"
        );

        DateField dateStartField = new DateField("Дата создания");
        dateStartField.setDefaultValue(LocalDate.now());
        dateStartField.setTextFieldEnabled(false);
        binder.forField(dateStartField).bind("localDate");

        TextField validityInDay = initBindedTextField(new TextField(), "Срок действия(дни)","Количество дней", binder, "validityInDayToString");

        Button butOK = new Button("ОК", eventClick -> {
            if(binder.isValid()) {
                recipe.setDescription(desc.getValue());
                recipe.setPatient_id(patientComboBox.getValue().getId());
                recipe.setDoctor_id(doctorComboBox.getValue().getId());
                recipe.setDate_create(Date.valueOf(dateStartField.getValue()));
                recipe.setValidityInDay(Integer.parseInt(validityInDay.getValue()));
                recipe.setPriority((priorityComboBox.getValue()));

                boolean result = recipeDAO.create(recipe);
                if(result){
                    log.info("Успешное добавление рецепта: "+recipe);
                    Notification.show("Данные о рецепте успешно сохранены", Notification.Type.TRAY_NOTIFICATION);
                    addEditWindow.close();
                    log.info("Форма добавления закрыта");
                    fillTableData();
                }else {
                    log.error("Ошибка добавления доктора: "+recipe);
                    Notification.show("Ошибка сохранения рецепта", Notification.Type.ERROR_MESSAGE);
                }
            }else {
                log.info("Введены некорректные данные");
                Notification.show("Не все поля заполнены корректно", Notification.Type.ERROR_MESSAGE);
            }
        });

        Button butCancel = new Button("Отменить");
        butCancel.addClickListener(click -> addEditWindow.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(butOK, butCancel);
        buttonsLayout.setComponentAlignment(butOK, Alignment.BOTTOM_LEFT);
        buttonsLayout.setComponentAlignment(butCancel, Alignment.BOTTOM_RIGHT);

        addEditWindow.addComponentInLayout(desc, patientComboBox, doctorComboBox, dateStartField, validityInDay, priorityComboBox, buttonsLayout);
        addEditWindow.getForm().setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);

        main.addWindow(addEditWindow);
    }

    private <T> ComboBox<T> createComboBoxField(
            Collection<T> items, ItemCaptionGenerator<T> iCG, String caption, Binder<RecipeDTO> binder, String nameBindField){
        ComboBox<T> comboBox = new ComboBox<>(caption);
        comboBox.setEmptySelectionAllowed(false);
        if(iCG != null){
            comboBox.setItemCaptionGenerator(iCG);
        }
        comboBox.setItems(items);
        binder.forField(comboBox).bind(nameBindField);
        return comboBox;
    }

    private <T extends AbstractTextField> T initBindedTextField(
            T newField, String caption, String placeholder, Binder<RecipeDTO> binder, String nameBindField){
        newField.setCaption(caption);
        newField.setPlaceholder(placeholder);
        binder.forField(newField).bind(nameBindField);
        return newField;
    }

    private void clickButDelete(ClickableRenderer.RendererClickEvent<Recipe> clickEvent){
        boolean resultDel = recipeDAO.delete(clickEvent.getItem().getId());
        if(resultDel){
            log.info("Успешное удаление рецепта: "+clickEvent.getItem());
            Notification.show("Рецепт успешно удалён", Notification.Type.TRAY_NOTIFICATION);
            grid.setItems(recipeDAO.getAll());
        }else {
            log.error("Ошибка удаление рецепта: "+clickEvent.getItem());
            Notification.show("Ошибка удаления рецепта", Notification.Type.ERROR_MESSAGE);
        }
    }

    private void clickButEdit(ClickableRenderer.RendererClickEvent<Recipe> clickEvent){
        log.info("Открылось окно изменения рецепта: "+clickEvent.getItem());
        AddEditWindow addEditWindow = new AddEditWindow("Изменение данных рецепта");

        BeanValidationBinder<RecipeDTO> binder = new BeanValidationBinder<>(RecipeDTO.class);
        Recipe recipeInDb = recipeDAO.getById(clickEvent.getItem().getId());
        RecipeDTO recipe = new RecipeDTO(
                recipeInDb.getDescription(),
                patientDAO.getById(recipeInDb.getPatient_id()),
                doctorDAO.getById(recipeInDb.getDoctor_id()),
                recipeInDb.getDate_create(),
                recipeInDb.getValidityInDay(),
                recipeInDb.getPriority().toString()
        );
        recipe.setId(recipeInDb.getId());
        binder.setBean(recipe);

        TextArea desc = initBindedTextField(
                new TextArea(),"Описание", "Описание рецепта...", binder, "description");
        desc.setValue(recipe.getDescription());

        ComboBox<Patient> patientComboBox = createComboBoxField(
                patientDAO.getAll(),
                Patient::toString,
                "Пациент",
                binder,
                "patient"
        );
        patientComboBox.setSelectedItem(binder.getBean().getPatient());

        ComboBox<Doctor> doctorComboBox = createComboBoxField(
                doctorDAO.getAll(),
                Doctor::toString,
                "Доктор",
                binder,
                "doctor"
        );
        doctorComboBox.setSelectedItem(binder.getBean().getDoctor());

        ComboBox<Priority> priorityComboBox = createComboBoxField(
                Arrays.asList(Priority.values()),
                Priority::getName,
                "Приоритет",
                binder,
                "priority"
        );
        priorityComboBox.setSelectedItem(recipe.getPriority());

        DateField dateStartField = new DateField("Дата создания");
        dateStartField.setValue(LocalDate.now());
        binder.forField(dateStartField).bind("localDate");
        dateStartField.setValue(recipe.getLocalDate());

        TextField validityInDay = initBindedTextField(
                new TextField(), "Срок действия(дни)","Количество дней", binder, "validityInDayToString");
        validityInDay.setValue(recipe.getValidityInDayToString());

        Button butOK = new Button("ОК", eventClick -> {
            if(binder.isValid()) {
                recipe.setDescription(desc.getValue());
                recipe.setPatient_id(patientComboBox.getValue().getId());
                recipe.setDoctor_id(doctorComboBox.getValue().getId());
                recipe.setDate_create(Date.valueOf(dateStartField.getValue()));
                recipe.setValidityInDay(Integer.parseInt(validityInDay.getValue()));
                recipe.setPriority(priorityComboBox.getValue());
                boolean result = recipeDAO.update(recipe);
                if(result){
                    log.info("Данные рецепта успешно обновлены: "+recipe);
                    Notification.show("Данные о рецепте успешно обновлены", Notification.Type.TRAY_NOTIFICATION);
                    addEditWindow.close();
                    log.info("Окно изменения рецепта закрывается");
                    fillTableData();
                }else {
                    log.error("Ошибка добавления рецепта: "+recipe);
                    Notification.show("Ошибка сохранения рецепта", Notification.Type.ERROR_MESSAGE);
                }
            }else {
                log.info("Введены некорректные данные: "+binder);
                Notification.show("Заполены не все обязательные поля", Notification.Type.ERROR_MESSAGE);
            }
        });

        Button butCancel = new Button("Отменить");
        butCancel.addClickListener(click -> addEditWindow.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(butOK, butCancel);
        buttonsLayout.setComponentAlignment(butOK, Alignment.BOTTOM_LEFT);
        buttonsLayout.setComponentAlignment(butCancel, Alignment.BOTTOM_RIGHT);

        addEditWindow.addComponentInLayout(desc, patientComboBox, doctorComboBox, dateStartField, validityInDay, priorityComboBox, buttonsLayout);
        addEditWindow.getForm().setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);
        main.addWindow(addEditWindow);
    }
}
