package com.haulmont.testtask.ui.layouts;

import com.haulmont.testtask.bl.dto.PatientFieldsDTO;
import com.haulmont.testtask.db.dao.PatientDAOImpl;
import com.haulmont.testtask.db.dao.interfaces.PatientDAO;
import com.haulmont.testtask.db.entity.Patient;
import com.haulmont.testtask.ui.MainUI;
import com.haulmont.testtask.ui.modalWindow.AddEditWindow;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import lombok.extern.slf4j.Slf4j;

import static com.haulmont.testtask.ui.MainUI.createCustomBindValidateField;

@Slf4j
public class PatientLayout extends VerticalLayout {
    private MainUI main;
    private Grid<Patient> grid = new Grid<>();
    private PatientDAO patientDAO = new PatientDAOImpl();

    public PatientLayout(MainUI main) {
        this.main = main;
        log.info("Инициализация страницы 'Пациенты'");
        buildTable();
    }

    private void fillTableData(){
        grid.setItems(patientDAO.getAll());
        log.info("Таблица пациентов заполнена обновлёнными данными");
    }

    private void buildTable(){
        log.info("Началось постоение таблицы");
        grid = new Grid<>();
        fillTableData();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(Patient::getId).setCaption("№").setWidth(75);
        grid.addColumn(Patient::getFirstName).setCaption("Имя");
        grid.addColumn(Patient::getLastName).setCaption("Фамилия");
        grid.addColumn(Patient::getPatronymic).setCaption("Отчество");
        grid.addColumn(Patient::getPhone).setCaption("Телефон");

        grid.addColumn(patient -> "Изменить", new ButtonRenderer<>(this::clickButEdit)).setId("edit").setMaximumWidth(130);
        grid.addColumn(patient -> "Удалить", new ButtonRenderer<>(this::clickButDelete)).setId("del").setMaximumWidth(130);

        grid.getHeaderRow(0).join("edit", "del").setText("Действия");

        Button addPatientButton = new Button("Добавить нового пациента");
        addPatientButton.addClickListener(clickEvent -> clickButAddNewDoctor());

        addComponents(grid, addPatientButton);
        setComponentAlignment(addPatientButton, Alignment.BOTTOM_CENTER);
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        log.info("Завершилось постоение таблицы");
    }
    private void clickButAddNewDoctor(){
        log.info("Открылось окно добавления нового пациента");
        AddEditWindow addEditWindow = new AddEditWindow("Добавление нового пациента");

        Patient patient = new Patient();
        PatientFieldsDTO dto = createPatientFields(patient);

        Button butOK = new Button("ОК", eventAdd -> {
            if(dto.getBinder().isValid()){
                boolean result = patientDAO.create(
                        new Patient(
                                dto.getFName().getValue(),
                                dto.getLName().getValue(),
                                dto.getPatronymic().getValue(),
                                dto.getPhone().getValue()
                        )
                );
                if(result){
                    log.info("Успешное добавление пациента: "+dto);
                    Notification.show("Данные о пациенте успешно обновлены", Notification.Type.TRAY_NOTIFICATION);
                    addEditWindow.close();
                    log.info("Форма добавления закрыта");
                    fillTableData();
                }else {
                    log.error("Ошибка добавления пациента: "+dto);
                    Notification.show("Ошибка сохранения данных пациента", Notification.Type.ERROR_MESSAGE);
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
    private void clickButDelete(ClickableRenderer.RendererClickEvent<Patient> clickEvent){
        boolean resultDel = patientDAO.delete(clickEvent.getItem().getId());
        if(resultDel){
            log.info("Успешное удаление пациента: "+clickEvent.getItem());
            Notification.show("Пациент "+ clickEvent.getItem().getFirstName() +" успешно удалён", Notification.Type.TRAY_NOTIFICATION);
            fillTableData();
        }else {
            log.error("Ошибка удаление пациента: "+clickEvent.getItem());
            Notification.show("Невозможно удалить пациента с активными рецептами", Notification.Type.ERROR_MESSAGE);
        }
    }
    private void clickButEdit(ClickableRenderer.RendererClickEvent<Patient> clickEvent){
        log.info("Открылось окно изменения пациента: "+clickEvent.getItem());
        AddEditWindow addEditWindow = new AddEditWindow("Изменение данных пациента");

        Patient patient = patientDAO.getById(clickEvent.getItem().getId());
        PatientFieldsDTO dto = createPatientFields(patient);

        Button butOK = new Button("ОК", eventClick -> {
            if(dto.getBinder().isValid()){
                patient.setFirstName(dto.getFName().getValue());
                patient.setLastName(dto.getLName().getValue());
                patient.setPatronymic(dto.getPatronymic().getValue());
                patient.setPhone(dto.getPhone().getValue());
                boolean result = patientDAO.update(patient);
                if(result){
                    log.info("Данные пациента успешно обновлены: "+dto);
                    Notification.show("Данные о пациенте успешно обновлены", Notification.Type.TRAY_NOTIFICATION);
                    addEditWindow.close();
                    log.info("Окно изменения пациента закрывается");
                    fillTableData();
                }else {
                    log.error("Ошибка добавления пациента: "+dto);
                    Notification.show("Ошибка сохранения данных пациента", Notification.Type.ERROR_MESSAGE);
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

    private PatientFieldsDTO createPatientFields(Patient patient){
        BeanValidationBinder<Patient> binder = new BeanValidationBinder<>(Patient.class);
        binder.setBean(patient);

        log.info("Связка полей окна с атрибутами валидатора успешно завершена");
        return PatientFieldsDTO.builder()
                .fName(createCustomBindValidateField("Имя", "Иван", binder, "firstName"))
                .lName(createCustomBindValidateField("Фамилия", "Иванов" ,binder, "lastName"))
                .patronymic(createCustomBindValidateField("Отчество" ,"Иванович", binder, "patronymic"))
                .phone(createCustomBindValidateField("Телефон" ,"+78008886644", binder, "phone"))
                .binder(binder).build();
    }
}