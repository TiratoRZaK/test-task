package com.haulmont.testtask.db.entity;

public enum  Priority {
    NORMAL("Нормальный"),
    CITO("Срочный"),
    STATIM("Немедленный");

    private String name;

    Priority(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Priority getByName(String name){
        for (Priority priority : values()){
            if(priority.name.equals(name)){
                return priority;
            }
        }
        return null;
    }
}
