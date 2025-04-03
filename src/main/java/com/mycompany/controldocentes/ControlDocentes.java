/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.controldocentes;

/**
 *
 * @author zatan
 */
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class ControlDocentes {
    private static Connection conexion;
    private static JFrame ventana;
    private static JTable tabla;
    private static DefaultTableModel modelo;

    public static void main(String[] args) {
        conectarBaseDatos();
        crearVentana();
    }
    
    private static void conectarBaseDatos() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/kelvin", "user", "");
            System.out.println("Conexión exitosa.");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    private static void crearVentana() {
        ventana = new JFrame("Gestión de Docentes");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(400, 100);
        ventana.setLayout(new FlowLayout());

        JButton btnInsertar = new JButton("Insertar Datos");
        btnInsertar.addActionListener(e -> insertarDatos());

        JButton btnEliminar = new JButton("Eliminar Registro");
        btnEliminar.addActionListener(e -> eliminarRegistro());

        JButton btnMostrar = new JButton("Mostrar Registros");
        btnMostrar.addActionListener(e -> mostrarDatos());

        JButton btnEliminarTabla = new JButton("Eliminar Tabla");
        btnEliminarTabla.addActionListener(e -> eliminarTabla());

        ventana.add(btnInsertar);
        ventana.add(btnEliminar);
        ventana.add(btnMostrar);
        ventana.add(btnEliminarTabla);
        ventana.setVisible(true);
    }

    private static void insertarDatos() {
        JDialog dialogo = new JDialog(ventana, "Insertar Datos", true);
        dialogo.setLayout(new GridLayout(11, 2));

        String[] etiquetas = {"Código Docente:", "Nombre:", "Apellido Paterno:", "Apellido Materno:",
            "Hora Entrada:", "Hora Salida:", "Cantidad Alumnado:", "Materia:", "Aula:", "Turno:"};
        JTextField[] campos = new JTextField[10];

        for (int i = 0; i < 10; i++) {
            dialogo.add(new JLabel(etiquetas[i]));
            campos[i] = new JTextField();
            dialogo.add(campos[i]);
        }

        JButton btnInsertar = new JButton("Insertar");
        btnInsertar.addActionListener(e -> {
            try (PreparedStatement stmt = conexion.prepareStatement(
                    "INSERT INTO docentes VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (int i = 0; i < 10; i++) {
                    stmt.setString(i + 1, campos[i].getText());
                }
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialogo, "Datos insertados correctamente.");
                dialogo.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });

        dialogo.add(btnInsertar);
        dialogo.pack();
        dialogo.setVisible(true);
    }

    private static void eliminarRegistro() {
        String codDocente = JOptionPane.showInputDialog("Ingrese Código de Docente a eliminar:");
        try (PreparedStatement stmt = conexion.prepareStatement("DELETE FROM docentes WHERE cod_docente = ?")) {
            stmt.setString(1, codDocente);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(ventana, "Registro eliminado correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
        }
    }

    private static void eliminarTabla() {
        try (Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS docentes");
            JOptionPane.showMessageDialog(ventana, "Tabla eliminada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
        }
    }

    private static void mostrarDatos() {
        JDialog dialogo = new JDialog(ventana, "Datos Insertados", true);
        dialogo.setSize(600, 300);
        modelo = new DefaultTableModel();

        String[] columnas = {"Código", "Nombre", "Ap. Paterno", "Ap. Materno", "Entrada", "Salida", "Alumnado", "Materia", "Aula", "Turno"};
        for (String col : columnas) {
            modelo.addColumn(col);
        }

        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM docentes")) {
            while (rs.next()) {
                Object[] fila = new Object[10];
                for (int i = 0; i < 10; i++) {
                    fila[i] = rs.getString(i + 1);
                }
                modelo.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
        }

        tabla = new JTable(modelo);
        dialogo.add(new JScrollPane(tabla));
        dialogo.setVisible(true);
    }

}
