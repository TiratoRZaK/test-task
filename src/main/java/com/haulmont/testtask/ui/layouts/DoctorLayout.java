package com.haulmont.testtask.ui.layouts;

import com.haulmont.testtask.bl.dto.DoctorFieldsDTO;
import com.haulmont.testtask.db.dao.DoctorDAOImpl;
import com.haulmont.testtask.db.dao.RecipeDAOImpl;
import com.haulmont.testtask.db.dao.interfaces.DoctorDAO;
import com.haulmont.testtask.db.dao.interfaces.RecipeDAO;
import com.haulmont.testtask.db.entity.Doctor;
import com.haulmont.testtask.ui.MainUI;
import com.haulmont.testtask.ui.modalWindow.AddEditWindow;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import lombok.extern.slf4j.Slf4j;

import static com.haulmont.testtask.ui.MainUI.createCustomBindValidateField;

@Slf4j
public class DoctorLayout extends VerticalLayout {
    private MainUI main;
    private Grid<Doctor> grid;
    private DoctorDAO doctorDAO = new DoctorDAOImpl();
    private RecipeDAO recipeDAO = new RecipeDAOImpl();

    public DoctorLayout(MainUI main) {
        this.main = main;
        log.info("Инициализация страницы 'Доктора'");
        addShowStatsButton();
        buildTable();
    }

    private void addShowStatsButton(){
        CheckBox showStatistic = new CheckBox("Показать статистику", false);
        showStatistic.addValueChangeListener(box -> {
            if(showStatistic.getValue()) {
                grid.addColumn(
                        doctor -> recipeDAO.getByDoctorId(doctor.getId()).size()
                ).setCaption("Выписано рецептов").setId("stats");
                grid.setColumnOrder(
                        "id", "firstName", "lastName", "patronymic", "specialization", "stats", "edit", "del");
                log.info("Добавлен столбец со статистикой");
            }else {
                grid.removeColumn("stats");
                log.info("Столбец со статистикой скрыт");
            }
        });
        addComponent(showStatistic);
        setComponentAlignment(showStatistic, Alignment.TOP_CENTER);
    }

    private void fillTableData(){
        grid.setItems(doctorDAO.getAll());
        log.info("Таблица докторов заполнена обновлёнными данными");
    }

    private void buildTable(){
        log.info("Началось постоение таблицы");
        grid = new Grid<>();
        fillTableData();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(Doctor::getId).setCaption("№").setWidth(75).setId("id");
        grid.addColumn(Doctor::getFirstName).setCaption("Имя").setId("firstName");
        grid.addColumn(Doctor::getLastName).setCaption("Фамилия").setId("lastName");
        grid.addColumn(Doctor::getPatronymic).setCaption("Отчество").setId("patronymic");
        grid.addColumn(Doctor::getSpecialization).setCaption("Специализация").setId("specialization");

        grid.addColumn(doctor -> "Изменить", new ButtonRenderer<>(this::clickButEdit)).setId("edit").setMaximumWidth(130);
        grid.addColumn(doctor -> "Удалить", new ButtonRenderer<>(this::clickButDelete)).setId("del").setMaximumWidth(130);

        grid.getHeaderRow(0).join("edit", "del").setText("Действия");

        Button addDoctorButton = new Button("Добавить нового доктора");
        addDoctorButton.addClickListener(clickEvent -> clickButAddNewDoctor());

        addComponents(grid, addDoctorButton);
        setComponentAlignment(addDoctorButton, Alignment.BOTTOM_CENTER);
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        log.info("Завершилось постоение таблицы");
    }

    private void clickButAddNewDoctor() {
        log.info("Открылось окно добавления нового доктора");
        AddEditWindow addEditWindow = new AddEditWindow("Добавление нового доктора");

        Doctor doctor = new Doctor();
        DoctorFieldsDTO dto = createDoctorFields(doctor);

        Button butOK = new Button("ОК", eventAdd -> {
            if(dto.getBinder().isValid()){
                boolean result = doctorDAO.create(
                        new Doctor(
                                dto.getFName().getValue(),
                                dto.getLName().getValue(),
                                dto.getPatronymic().getValue(),
                                dto.getSpecialization().getValue()
                        )
                );
                if(result){
                    log.info("Успешное добавление доктора: "+dto);
                    Notification.show("Данные о докторе успешно обновлены", Notification.Type.TRAY_NOTIFICATION);
                    addEditWindow.close();
                    log.info("Форма добавления закрыта");
                    fillTableData();
                }else {
                    log.error("Ошибка добавления доктора: "+dto);
                    Notification.show("Ошибка сохранения данных доктора", Notification.Type.ERROR_MESSAGE);
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

        addEditWindow.addComponentInLayout(dto.getFields());
        addEditWindow.addComponentInLayout(buttonsLayout);
        addEditWindow.getForm().setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);
        main.addWindow(addEditWindow);
    }
    private void clickButDelete(ClickableRenderer.RendererClickEvent<Doctor> clickEvent){
        boolean resultDel = doctorDAO.delete(clickEvent.getItem().getId());
        if(resultDel){
            log.info("Успешное удаление доктора: "+clickEvent.getItem());
            Notification.show("Доктор "+ clickEvent.getItem().getFirstName() +" успешно удалён", Notification.Type.TRAY_NOTIFICATION);
            fillTableData();
        }else {
            log.error("Ошибка удаление доктора: "+clickEvent.getItem());
            Notification.show("Невозможно удалить доктора с выписанными рецептами", Notification.Type.ERROR_MESSAGE);
        }
    }
    private void clickButEdit(ClickableRenderer.RendererClickEvent<Doctor> clickEvent){
        log.info("Открылось окно изменения доктора: "+clickEvent.getItem());
        AddEditWindow addEditWindow = new AddEditWindow("Изменение данных доктора");

        Doctor doctor = doctorDAO.getById(clickEvent.getItem().getId());
        DoctorFieldsDTO dto = createDoctorFields(doctor);

        Button butOK = new Button("ОК", eventClick -> {
            if(dto.getBinder().isValid()){
                doctor.setFirstName(dto.getFName().getValue());
                doctor.setLastName(dto.getLName().getValue());
                doctor.setPatronymic(dto.getPatronymic().getValue());
                doctor.setSpecialization(dto.getSpecialization().getValue());
                boolean result = doctorDAO.update(doctor);
                if(result){
                    log.info("Данные доктора успешно обновлены: "+dto);
                    Notification.show("Данные о докторе успешно обновлены", Notification.Type.TRAY_NOTIFICATION);
                    addEditWindow.close();
                    log.info("Окно изменения доктора закрывается");
                    fillTableData();
                }else {
                    log.error("Ошибка добавления доктора: "+dto);
                    Notification.show("Ошибка сохранения данных доктора", Notification.Type.ERROR_MESSAGE);
                }
            }else {
                log.info("Введены некорректные данные: "+dto.getBinder());
                Notification.show("Не все поля заполнены корректно", Notification.Type.ERROR_MESSAGE);
            }
        });

        Button butCancel = new Button("Отменить");
        butCancel.addClickListener(click -> addEditWindow.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(butOK, butCancel);
        buttonsLayout.setComponentAlignment(butOK, Alignment.BOTTOM_LEFT);
        buttonsLayout.setComponentAlignment(butCancel, Alignment.BOTTOM_RIGHT);

        addEditWindow.addComponentInLayout(dto.getFields());
        addEditWindow.addComponentInLayout(buttonsLayout);
        addEditWindow.getForm().setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);
        main.addWindow(addEditWindow);
    }

    private DoctorFieldsDTO createDoctorFields(Doctor doctor){
        BeanValidationBinder<Doctor> binder = new BeanValidationBinder<>(Doctor.class);
        binder.setBean(doctor);

        log.info("Связка полей окна с атрибутами валидатора успешно завершена");
        return DoctorFieldsDTO.builder()
                .fName(createCustomBindValidateField("Имя", "Иван", binder, "firstName"))
                .lName(createCustomBindValidateField("Фамилия", "Иванов" ,binder, "lastName"))
                .patronymic(createCustomBindValidateField("Отчество" ,"Иванович", binder, "patronymic"))
                .specialization(createCustomBindValidateField("Специализация" ,"Терапевт", binder, "specialization"))
                .binder(binder).build();
    }
}
