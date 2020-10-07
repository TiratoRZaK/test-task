package com.haulmont.testtask.db.dao.interfaces;

import com.haulmont.testtask.db.entity.Patient;
import com.haulmont.testtask.db.entity.Priority;
import com.haulmont.testtask.db.entity.Recipe;

import java.util.List;

public interface RecipeDAO extends DAO<Recipe> {
    List<Recipe> getByDoctorId(Long doctor_id);
    List<Recipe> getAllByFilters(String desc, Patient patient, Priority priority);
}
