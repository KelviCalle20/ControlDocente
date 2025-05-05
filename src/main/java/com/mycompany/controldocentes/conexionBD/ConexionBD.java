package com.mycompany.controldocentes.conexionBD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static Connection conexion;

    public static Connection getConnection() {
        if (conexion == null) {
            try {
                conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/kelvin", "user", "");
                System.out.println("Conexión exitosa.");
            } catch (SQLException e) {
                System.out.println("Error de conexión: " + e.getMessage());
            }
        }
        return conexion;
    }
}
