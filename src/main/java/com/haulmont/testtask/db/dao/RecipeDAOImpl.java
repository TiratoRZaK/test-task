package com.haulmont.testtask.db.dao;

import com.haulmont.testtask.db.DB;
import com.haulmont.testtask.db.dao.interfaces.RecipeDAO;
import com.haulmont.testtask.db.entity.Patient;
import com.haulmont.testtask.db.entity.Priority;
import com.haulmont.testtask.db.entity.Recipe;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RecipeDAOImpl implements RecipeDAO {
    @Override
    public List<Recipe> getByDoctorId(Long doctor_id) {
        String sql = "SELECT * FROM RECIPES r JOIN DOCTORS d ON r.DOCTOR_ID = d.ID AND r.DOCTOR_ID = ?";
        List<Recipe> list = new ArrayList<>();
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1,doctor_id);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Recipe d = new Recipe(
                        rs.getString("description"),
                        rs.getLong("doctor_id"),
                        rs.getLong("patient_id"),
                        rs.getDate("date_create"),
                        rs.getInt("validityInDay"),
                        rs.getString("priority")
                );
                d.setId(rs.getLong(1));
                list.add(d);
            }
        } catch (SQLException e) {
            log.error("Ошибка запроса списка пациентов из базы данных по идентификатору доктора = "+doctor_id+"! \nПричина: ", e);
        }
        return list;
    }

    @Override
    public Recipe getById(Long id) {
        String sql = "SELECT * FROM RECIPES WHERE id = ?;";
        Recipe r = null;
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1, id);

            ResultSet rs = stm.executeQuery();
            rs.next();

            r = new Recipe(
                    rs.getString("description"),
                    rs.getLong("doctor_id"),
                    rs.getLong("patient_id"),
                    rs.getDate("date_create"),
                    rs.getInt("validityInDay"),
                    rs.getString("priority")
            );
            r.setId(rs.getLong(1));
        } catch (SQLException e) {
            log.error("Ошибка запроса рецепта из базы данных. Причина: ", e);
        }
        return r;
    }

    @Override
    public boolean create(Recipe item) {
        String sql = "INSERT INTO RECIPES VALUES(NULL,?,?,?,?,?,?);";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setString(1, item.getDescription());
            stm.setLong(2, item.getPatient_id());
            stm.setLong(3, item.getDoctor_id());
            stm.setDate(4, item.getDate_create());
            stm.setInt(5, item.getValidityInDay());
            stm.setString(6, item.getPriority().toString());

            int count = stm.executeUpdate();
            if(count==1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на создание рецепта к базе данных! \nПричина: ", e);
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM RECIPES WHERE id = ?;";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1, id);

            int count = stm.executeUpdate();
            if(count == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на удаление рецепта из базы данных! \nПричина: ", e);
        }
        return false;
    }

    @Override
    public boolean update(Recipe item) {
        String sql = "UPDATE RECIPES SET DESCRIPTION = ?, DOCTOR_ID = ?, PATIENT_ID = ?, DATE_CREATE = ?, validityInDay = ?, PRIORITY = ? WHERE id = ?;";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setString(1, item.getDescription());
            stm.setLong(2, item.getDoctor_id());
            stm.setLong(3, item.getPatient_id());
            stm.setDate(4, item.getDate_create());
            stm.setInt(5, item.getValidityInDay());
            stm.setString(6, item.getPriority().toString());
            stm.setLong(7, item.getId());

            int count = stm.executeUpdate();
            if(count == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на обновление рецепта в базе данных! \nПричина: ", e);
        }
        return false;
    }

    @Override
    public List<Recipe> getAll() {
        String sql = "SELECT * FROM RECIPES;";
        List<Recipe> list = new ArrayList<>();
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Recipe r = new Recipe(
                        rs.getString("description"),
                        rs.getLong("doctor_id"),
                        rs.getLong("patient_id"),
                        rs.getDate("date_create"),
                        rs.getInt("validityInDay"),
                        rs.getString("priority")
                );
                r.setId(rs.getLong(1));
                list.add(r);
            }
        } catch (SQLException e) {
            log.error("Ошибка запроса списка рецептов из базы данных! \nПричина: ", e);
        }
        return list;
    }

    @Override
    public List<Recipe> getAllByFilters(String desc, Patient patient, Priority priority) {
        StringBuilder sql = new StringBuilder("SELECT * FROM RECIPES WHERE");
        ArrayList<String> filters = new ArrayList<>();
        if(desc != null && !desc.isEmpty()){
            filters.add(" LOWER(DESCRIPTION) LIKE LOWER('%"+desc+"%')");
        }
        if(patient != null){
            filters.add(" PATIENT_ID = "+patient.getId());
        }
        if(priority != null){
            filters.add(" PRIORITY = '"+priority.toString()+"'");
        }
        for (int i = 0; i < filters.size(); i++) {
            if(i != 0){
                sql.append(" AND ");
            }
            sql.append(filters.get(i));
        }

        List<Recipe> list = new ArrayList<>();
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql.toString())) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Recipe r = new Recipe(
                        rs.getString("description"),
                        rs.getLong("doctor_id"),
                        rs.getLong("patient_id"),
                        rs.getDate("date_create"),
                        rs.getInt("validityInDay"),
                        rs.getString("priority")
                );
                r.setId(rs.getLong(1));
                list.add(r);
            }
        } catch (SQLException e) {
            log.error("Ошибка запроса фильтрованного списка пациентов из базы данных! \nПричина: ", e);
        }
        return list;
    }
}
