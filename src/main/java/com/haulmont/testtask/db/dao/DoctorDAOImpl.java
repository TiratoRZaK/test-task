package com.haulmont.testtask.db.dao;

import com.haulmont.testtask.db.DB;
import com.haulmont.testtask.db.dao.interfaces.DoctorDAO;
import com.haulmont.testtask.db.entity.Doctor;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DoctorDAOImpl implements DoctorDAO {
    @Override
    public List<Doctor> getAll() {
        String sql = "SELECT * FROM DOCTORS;";
        List<Doctor> list = new ArrayList<>();
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Doctor d = new Doctor(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("patronymic"),
                        rs.getString("specialization")
                );
                d.setId(rs.getLong(1));
                list.add(d);
            }
        } catch (SQLException e) {
            log.error("Ошибка запроса списка докторов из базы данных! \nПричина: ",e);
        }
        return list;
    }

    @Override
    public Doctor getById(Long id) {
        String sql = "SELECT * FROM DOCTORS WHERE id = ?;";
        Doctor d = null;
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1, id);

            ResultSet rs = stm.executeQuery();
            rs.next();

            d = new Doctor(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("patronymic"),
                    rs.getString("specialization")
            );
            d.setId(rs.getLong(1));
        } catch (SQLException e) {
            log.error("Ошибка запроса доктора из базы данных! \nПричина: ", e);
        }
        return d;
    }

    @Override
    public boolean create(Doctor item) {
        String sql = "INSERT INTO DOCTORS VALUES(NULL, ?,?,?,?);";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setString(1, item.getFirstName());
            stm.setString(2, item.getLastName());
            stm.setString(3, item.getPatronymic());
            stm.setString(4, item.getSpecialization());

            int count = stm.executeUpdate();
            if (count == 1) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на создание доктора к базе данных! \nПричина: ", e);
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM DOCTORS WHERE id = ?;";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setLong(1, id);

            int count = stm.executeUpdate();
            if(count == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на удаление доктора из базы данных! \nПричина: ", e);
        }
        return false;
    }

    @Override
    public boolean update(Doctor item) {
        String sql = "UPDATE DOCTORS SET FIRSTNAME = ?, LASTNAME = ?, PATRONYMIC = ?, SPECIALIZATION = ? WHERE id = ?;";
        try (PreparedStatement stm = DB.getConnection().prepareStatement(sql)) {
            stm.setString(1, item.getFirstName());
            stm.setString(2, item.getLastName());
            stm.setString(3, item.getPatronymic());
            stm.setString(4, item.getSpecialization());
            stm.setLong(5, item.getId());

            int count = stm.executeUpdate();
            if(count == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка запроса на обновление доктора в базе данных! \nПричина: ", e);
        }
        return false;
    }
}
