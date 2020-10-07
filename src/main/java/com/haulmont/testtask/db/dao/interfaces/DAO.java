package com.haulmont.testtask.db.dao.interfaces;

import java.util.List;

public interface DAO<T> {
    T getById(Long id);
    boolean create(T item);
    boolean delete(Long id);
    boolean update(T item);
    List<T> getAll();
}
