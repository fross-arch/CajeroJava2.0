package miplata.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectionMySql {

    private static DataBaseConnectionMySql instance;
    private final Connection connection;

    private static final String URL      = "jdbc:mysql://localhost:3306/miplata";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private DataBaseConnectionMySql() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar la base de datos", e);
        }
    }

    public static synchronized DataBaseConnectionMySql getInstance() {
        if (instance == null) {
            instance = new DataBaseConnectionMySql();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}