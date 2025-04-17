
package com.mycompany.controldocentes;

/**
 *integrantes:  Kelvin Calle
 *              Denilson Apaza
 */
// Asegúrate de importar todas las librerías necesarias
// Asegúrate de importar todas las librerías necesarias 
import com.fazecast.jSerialComm.SerialPort;
import java.awt.*;
import java.sql.*;
import java.util.Scanner;
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
        ventana = new JFrame("Control de Docentes");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(600, 400);
        ventana.setLayout(null);

        ImageIcon originalIcon = new ImageIcon("src/imagenes/menu_docente.jpg");
        Image imagenOriginal = originalIcon.getImage();
        Image imagenEscalada = imagenOriginal.getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
        JLabel fondo = new JLabel(iconoEscalado);
        fondo.setBounds(0, 0, 600, 400);

        JLabel titulo = new JLabel("CONTROL DE DOCENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(180, 20, 400, 30);

        JComboBox<String> opciones = new JComboBox<>(new String[]{
            "Seleccionar una opción...",
            "Insertar Datos",
            "Eliminar Registro",
            "Mostrar Registros",
            "Eliminar Tabla",
            "Iniciar Control"
        });
        opciones.setBounds(180, 80, 250, 30);

        opciones.addActionListener(e -> {
            String opcion = (String) opciones.getSelectedItem();
            switch (opcion) {
                case "Insertar Datos":
                    insertarDatos();
                    break;
                case "Eliminar Registro":
                    eliminarRegistro();
                    break;
                case "Mostrar Registros":
                    mostrarDatos();
                    break;
                case "Eliminar Tabla":
                    eliminarTabla();
                    break;
                case "Iniciar Control":
                    iniciarControlRFID();
                    break;
            }
        });

        ventana.setContentPane(fondo);
        fondo.setLayout(null);
        fondo.add(titulo);
        fondo.add(opciones);

        ventana.setVisible(true);
    }

    private static void insertarDatos() {
        JDialog dialogo = new JDialog(ventana, "Insertar Datos", true);
        dialogo.setLayout(new GridLayout(11, 2, 5, 5));

        String[] etiquetas = {"Código Docente (escaneado):", "Nombre:", "Apellido Paterno:", "Apellido Materno:",
            "Hora Entrada:", "Hora Salida:", "Cantidad Alumnado:", "Materia:", "Aula:", "Turno:"};
        JTextField[] campos = new JTextField[10];

        for (int i = 0; i < 10; i++) {
            dialogo.add(new JLabel(etiquetas[i]));
            campos[i] = new JTextField();
            campos[i].setEditable(i != 0);
            dialogo.add(campos[i]);
        }

        new Thread(() -> {
            SerialPort puerto = SerialPort.getCommPorts()[0];
            puerto.setComPortParameters(9600, 8, 1, 0);
            puerto.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            if (puerto.openPort()) {
                Scanner scanner = new Scanner(puerto.getInputStream());
                while (dialogo.isVisible()) {
                    if (scanner.hasNextLine()) {
                        String codigo = scanner.nextLine().trim();
                        System.out.println("RFID leído: " + codigo);
                        SwingUtilities.invokeLater(() -> campos[0].setText(codigo));
                        break;
                    }
                }
                scanner.close();
                puerto.closePort();
            }
        }).start();

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

        dialogo.add(new JLabel());
        dialogo.add(btnInsertar);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }

    private static void iniciarControlRFID() {
        JDialog lectorDialog = new JDialog(ventana, "Escanear Tarjeta", true);
        lectorDialog.setSize(300, 150);
        lectorDialog.setLayout(new BorderLayout());

        JLabel mensaje = new JLabel("Por favor, escanee su tarjeta RFID...", SwingConstants.CENTER);
        lectorDialog.add(mensaje, BorderLayout.CENTER);

        new Thread(() -> {
            SerialPort puerto = SerialPort.getCommPorts()[0];
            puerto.setComPortParameters(9600, 8, 1, 0);
            puerto.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            if (puerto.openPort()) {
                Scanner scanner = new Scanner(puerto.getInputStream());
                while (lectorDialog.isVisible()) {
                    if (scanner.hasNextLine()) {
                        String uid = scanner.nextLine().trim();
                        System.out.println("UID detectado: " + uid);
                        mostrarDatosDocente(uid);
                        lectorDialog.dispose();
                        break;
                    }
                }
                scanner.close();
                puerto.closePort();
            }
        }).start();

        lectorDialog.setLocationRelativeTo(ventana);
        lectorDialog.setVisible(true);
    }

    private static void mostrarDatosDocente(String uid) {
        try {
            String query = "SELECT cod_docente, nombre, apellido_paterno, apellido_materno, materia, aula, hora_entrada FROM docentes WHERE cod_docente = ?";
            PreparedStatement stmt = conexion.prepareStatement(query);
            stmt.setString(1, uid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String codDocente = rs.getString("cod_docente");
                String nombre = rs.getString("nombre");
                String apPaterno = rs.getString("apellido_paterno");
                String apMaterno = rs.getString("apellido_materno");
                String nombreCompleto = nombre + " " + apPaterno + " " + apMaterno;

                String datos = String.format("""
                    Bienvenido/a docente.
                    Código: %s
                    Nombre: %s
                    Materia: %s
                    Aula: %s
                    Hora Entrada: %s
                    """,
                        codDocente, nombreCompleto, rs.getString("materia"), rs.getString("aula"), rs.getString("hora_entrada"));

                JOptionPane.showMessageDialog(ventana, datos);

                // Registrar asistencia
                String insert = "INSERT INTO asistencia (uid, nombre, fecha) VALUES (?, ?, NOW())";
                PreparedStatement insertarAsistencia = conexion.prepareStatement(insert);
                insertarAsistencia.setString(1, codDocente);
                insertarAsistencia.setString(2, nombreCompleto);
                insertarAsistencia.executeUpdate();
                insertarAsistencia.close();
            } else {
                JOptionPane.showMessageDialog(ventana, "No se encontró ningún docente con este UID.");
            }
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ventana, "Error al consultar o registrar asistencia: " + e.getMessage());
        }
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