package com.haulmont.testtask.db.dao;

import com.haulmont.testtask.db.DB;
import com.haulmont.testtask.db.dao.interfaces.PatientDAO;
import com.haulmont.testtask.db.entity.Patient;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PatientDAOImpl implements PatientDAO {
    @Override
    public Patient getById(Long id) {
        String sql = "SELECT * FROM PATIENTS WHERE id = ?;";
        Patient p = null;
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1, id);

            ResultSet rs = stm.executeQuery();
            rs.next();

            p = new Patient(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("patronymic"),
                    rs.getString("phone")
            );
            p.setId(rs.getLong(1));
        } catch (SQLException e) {
            log.error("Ошибка запроса пациента из базы данных. Причина: ", e);
        }
        return p;
    }

    @Override
    public boolean create(Patient item) {
        String sql = "INSERT INTO PATIENTS VALUES(NULL, ?,?,?,?);";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setString(1, item.getFirstName());
            stm.setString(2, item.getLastName());
            stm.setString(3, item.getPatronymic());
            stm.setString(4, item.getPhone());

            int count = stm.executeUpdate();
            if(count==1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на создание пациента к базе данных! \nПричина: ",e);
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM PATIENTS WHERE id = ?;";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1, id);

            int count = stm.executeUpdate();
            if(count == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на удаление пациента из базы данных! \nПричина: ",e);
        }
        return false;
    }

    @Override
    public boolean update(Patient item) {
        String sql = "UPDATE PATIENTS SET FIRSTNAME = ?, LASTNAME = ?, PATRONYMIC = ?, PHONE = ? WHERE id = ?;";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setString(1, item.getFirstName());
            stm.setString(2, item.getLastName());
            stm.setString(3, item.getPatronymic());
            stm.setString(4, item.getPhone());
            stm.setLong(5, item.getId());

            int count = stm.executeUpdate();
            if(count == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на обновление пациента в базе данных! \nПричина: ",e);
        }
        return false;
    }

    @Override
    public List<Patient> getAll() {
        String sql = "SELECT * FROM PATIENTS;";
        List<Patient> list = new ArrayList<>();
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Patient p = new Patient(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("patronymic"),
                        rs.getString("phone")
                );
                p.setId(rs.getLong(1));
                list.add(p);
            }
        } catch (SQLException e) {
            log.error("Ошибка запроса списка пациентов из базы данных! \nПричина: ",e);
        }
        return list;
    }
}
