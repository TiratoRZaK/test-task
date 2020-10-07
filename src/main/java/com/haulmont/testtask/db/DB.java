package com.haulmont.testtask.db;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Slf4j
public class DB {
    private static Connection connection;
    private static boolean isInit = false;
    private static String CONNECTION_STRING;
    private static String USER;
    private static String PASSWORD;
    private static String JDBC_DRIVER;

    static {
        Properties dbProp = new Properties();
        FileInputStream fis;
        try {
            fis = new FileInputStream("src/main/java/com/haulmont/testtask/db/config/db.properties");
            dbProp.load(fis);

            CONNECTION_STRING = dbProp.getProperty("database.url");
            USER = dbProp.getProperty("database.username");
            PASSWORD = dbProp.getProperty("database.password");
            JDBC_DRIVER = dbProp.getProperty("database.driverClassName");

        } catch (IOException e) {
            log.error("Файл свойств подключения к базе данных отсуствует!", e);
        }
    }

    public static Connection getConnection(){
        if(!isInit){
            new DB().createConnection();
        }
        return connection;
    }

    private void createConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
            if (connection!= null){
                log.info("Успешное подключение базы данных");
                isInit = true;
            }else{
                log.error("Проблемы с созданием подключения к базе данных");
            }
        }  catch (Exception e) {
            log.error("Невозможно подключиться к базе данных"+e);
        }
    }
}
