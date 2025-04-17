
package com.mycompany.controldocentes;

/**
 *integrantes:  Kelvin Calle
 *              Denilson Apaza
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
        ventana = new JFrame("Menú Principal - Gestión de Docentes");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(600, 400);
        ventana.setLocationRelativeTo(null); // Centra la ventana

        JMenuBar menuBar = new JMenuBar();

        JMenu menuOpciones = new JMenu("Opciones");

        JMenuItem itemInsertar = new JMenuItem("Insertar Datos");
        itemInsertar.addActionListener(e -> insertarDatos());

        JMenuItem itemEliminar = new JMenuItem("Eliminar Registro");
        itemEliminar.addActionListener(e -> eliminarRegistro());

        JMenuItem itemMostrar = new JMenuItem("Mostrar Registros");
        itemMostrar.addActionListener(e -> mostrarDatos());

        JMenuItem itemEliminarTabla = new JMenuItem("Eliminar Tabla");
        itemEliminarTabla.addActionListener(e -> eliminarTabla());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));

        menuOpciones.add(itemInsertar);
        menuOpciones.add(itemEliminar);
        menuOpciones.add(itemMostrar);
        menuOpciones.add(itemEliminarTabla);
        menuOpciones.addSeparator();
        menuOpciones.add(itemSalir);

        menuBar.add(menuOpciones);
        ventana.setJMenuBar(menuBar);

        JLabel etiquetaBienvenida = new JLabel("Bienvenido al Sistema de Gestión de Docentes", JLabel.CENTER);
        etiquetaBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        ventana.add(etiquetaBienvenida);

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
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }

    private static void eliminarRegistro() {
        String codDocente = JOptionPane.showInputDialog(ventana, "Ingrese Código de Docente a eliminar:");
        if (codDocente != null && !codDocente.trim().isEmpty()) {
            try (PreparedStatement stmt = conexion.prepareStatement("DELETE FROM docentes WHERE cod_docente = ?")) {
                stmt.setString(1, codDocente);
                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(ventana, "Registro eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(ventana, "No se encontró el código ingresado.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
            }
        }
    }

    private static void eliminarTabla() {
        int confirmacion = JOptionPane.showConfirmDialog(ventana,
                "¿Estás seguro de eliminar la tabla 'docentes'?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Statement stmt = conexion.createStatement()) {
                stmt.executeUpdate("DROP TABLE IF EXISTS docentes");
                JOptionPane.showMessageDialog(ventana, "Tabla eliminada correctamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
            }
        }
    }

    private static void mostrarDatos() {
        JDialog dialogo = new JDialog(ventana, "Datos Insertados", true);
        dialogo.setSize(700, 300);
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
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }
}
