package com.haulmont.testtask.ui.modalWindow;

import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Window;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEditWindow extends Window {
    private FormLayout form;

    public AddEditWindow(String caption) {
        super(caption);
        setModal(true);
        setWidth(30, Unit.PERCENTAGE);
        form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);
        setContent(form);
        center();
    }

    public void addComponentInLayout(Component...components) {
        for (Component component : components) {
            form.addComponent(component);
        }
    }
}
