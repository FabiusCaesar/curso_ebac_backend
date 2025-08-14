package dev.fabiuscaesar.jdbc.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author FabiusCaesar
 */
public class ConnectionFactory {
    private static final String url = "jdbc:postgresql://localhost:5432/jdbc_crud_mod29";
    private static final String user = "postgres";
    private static final String password = "pcctrze";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
