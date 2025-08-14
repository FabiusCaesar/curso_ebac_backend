package dev.fabiuscaesar.jdbc;

import dev.fabiuscaesar.jdbc.config.ConnectionFactory;

import java.sql.Connection;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
