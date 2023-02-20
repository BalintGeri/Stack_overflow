package com.codecool.stackoverflowtw.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class DatabasePostgre implements Database {

    private final String URL;
    private final String username;
    private final String password;

    public DatabasePostgre(String URL, String username, String password) {
        this.URL = URL;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, username, password)) {
            return connection;
        }
        catch(SQLException e) {
            System.err.println("lerobbant a konnektelés papi " + Arrays.toString(e.getStackTrace()));
            throw new SQLException(e);
        }
    }
}
