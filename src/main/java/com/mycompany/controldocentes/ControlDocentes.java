
package com.mycompany.controldocentes;

/**
 *integrantes:  Kelvin Calle
 *              Denilson Apaza
 */
import com.fazecast.jSerialComm.SerialPort;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ControlDocentes {

    private static Connection conexion;
    private static JFrame ventana;
    private static JTable tablaAsistencia;
    private static DefaultTableModel modeloAsistencia;

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
        ventana.setSize(800, 500);
        ventana.setLayout(null);

        // Fondo
        ImageIcon originalIcon = new ImageIcon("src/imagenes/menu_docente.jpg");
        Image imagenOriginal = originalIcon.getImage();
        Image imagenEscalada = imagenOriginal.getScaledInstance(800, 500, Image.SCALE_SMOOTH);
        ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
        JLabel fondo = new JLabel(iconoEscalado);
        fondo.setBounds(0, 0, 800, 500);

        JLabel titulo = new JLabel("CONTROL DE DOCENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(250, 20, 400, 30);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(50, 80, 200, 40);
        btnRegistrar.addActionListener(e -> insertarDatos());

        JButton btnVerReporte = new JButton("Ver Reporte");
        btnVerReporte.setBounds(50, 130, 200, 40);
        btnVerReporte.addActionListener(e -> mostrarReporte());

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(50, 180, 200, 40);
        btnEditar.addActionListener(e -> mostrarOpcionesEditar());

        JButton btnIniciarControl = new JButton("Iniciar Control");
        btnIniciarControl.setBounds(50, 230, 200, 40);
        btnIniciarControl.addActionListener(e -> iniciarControlRFID());

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBounds(50, 280, 200, 40);
        btnSalir.addActionListener(e -> System.exit(0));

        // Tabla de asistencia en tiempo real
        modeloAsistencia = new DefaultTableModel(new String[]{"Fecha", "Hora", "Nombre"}, 0);
        tablaAsistencia = new JTable(modeloAsistencia);
        JScrollPane scroll = new JScrollPane(tablaAsistencia);
        scroll.setBounds(300, 80, 460, 300);

        fondo.setLayout(null);
        fondo.add(titulo);
        fondo.add(btnRegistrar);
        fondo.add(btnVerReporte);
        fondo.add(btnEditar);
        fondo.add(btnIniciarControl);
        fondo.add(btnSalir);
        fondo.add(scroll);

        ventana.setContentPane(fondo);
        ventana.setVisible(true);
    }

    private static void mostrarOpcionesEditar() {
        String[] opciones = {"Eliminar Tabla", "Eliminar Registro", "Mostrar Registros"};
        String seleccion = (String) JOptionPane.showInputDialog(
                ventana,
                "Seleccione una opción:",
                "Editar",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccion != null) {
            switch (seleccion) {
                case "Eliminar Tabla":
                    eliminarTabla();
                    break;
                case "Eliminar Registro":
                    eliminarRegistro();
                    break;
                case "Mostrar Registros":
                    mostrarDatos();
                    break;
            }
        }
    }

    private static void insertarDatos() {
        JDialog dialogo = new JDialog(ventana, "Registrarse", true);
        dialogo.setLayout(new GridLayout(11, 2, 5, 5));

        String[] etiquetas = {"Código Docente (escanear TJ):", "Nombre:", "Apellido Paterno:", "Apellido Materno:",
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
                        System.out.println("tarjeta leída: " + codigo);
                        SwingUtilities.invokeLater(() -> campos[0].setText(codigo));
                        break;
                    }
                }
                scanner.close();
                puerto.closePort();
            }
        }).start();

        JButton btnInsertar = new JButton("registrar");
        btnInsertar.addActionListener(e -> {
            try (PreparedStatement stmt = conexion.prepareStatement(
                    "INSERT INTO docentes VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (int i = 0; i < 10; i++) {
                    stmt.setString(i + 1, campos[i].getText());
                }
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialogo, "registro exitoso.");
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

    private static void mostrarReporte() {
        JDialog dialogo = new JDialog(ventana, "Reporte de Asistencia", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        dialogo.setSize(400, 200);

        JTextField campoCodigo = new JTextField();
        campoCodigo.setEditable(false);

        JTextField campoAnio = new JTextField();

        String[] meses = {
            "Seleccionar mes...",
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        JComboBox<String> comboMes = new JComboBox<>(meses);

        dialogo.add(new JLabel("Código RFID del docente (opcional):"));
        dialogo.add(campoCodigo);
        dialogo.add(new JLabel("Mes (opcional):"));
        dialogo.add(comboMes);
        dialogo.add(new JLabel("Año (ej. 2025, obligatorio si hay mes):"));
        dialogo.add(campoAnio);

        JButton btnBuscar = new JButton("Buscar");
        dialogo.add(new JLabel());
        dialogo.add(btnBuscar);

        Runnable escanearUID = () -> {
            new Thread(() -> {
                SerialPort puerto = SerialPort.getCommPorts()[0];
                puerto.setComPortParameters(9600, 8, 1, 0);
                puerto.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                if (puerto.openPort()) {
                    Scanner scanner = new Scanner(puerto.getInputStream());
                    while (dialogo.isVisible() && campoCodigo.getText().isEmpty()) {
                        if (scanner.hasNextLine()) {
                            String codigo = scanner.nextLine().trim();
                            System.out.println("UID escaneado: " + codigo);
                            SwingUtilities.invokeLater(() -> campoCodigo.setText(codigo));
                            break;
                        }
                    }
                    scanner.close();
                    puerto.closePort();
                }
            }).start();
        };

        // Iniciar escaneo desde el principio
        escanearUID.run();

        // Detectar si se borra manualmente el campo para volver a escanear
        campoCodigo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            boolean escaneando = false;

            private void reactivarSiVacio() {
                if (campoCodigo.getText().trim().isEmpty() && !escaneando) {
                    escaneando = true;
                    escanearUID.run();
                    // Espera breve para evitar múltiples hilos
                    new javax.swing.Timer(1000, evt -> escaneando = false).start();
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                reactivarSiVacio();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                reactivarSiVacio();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // No se usa en campos de texto planos
            }
        });

        btnBuscar.addActionListener(e -> {
            String codigo = campoCodigo.getText().trim();
            int mes = comboMes.getSelectedIndex();
            String anio = campoAnio.getText().trim();

            if (mes > 0 && anio.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Debe ingresar el año si selecciona un mes.");
                return;
            }

            if (mes == 0 && anio.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Debe ingresar al menos el año o el año y el mes.");
                return;
            }

            try {
                StringBuilder sql = new StringBuilder("SELECT uid, nombre, fecha FROM asistencia WHERE 1=1");
                if (!codigo.isEmpty()) {
                    sql.append(" AND uid = ?");
                }
                if (!anio.isEmpty()) {
                    sql.append(" AND YEAR(fecha) = ?");
                }
                if (mes > 0) {
                    sql.append(" AND MONTH(fecha) = ?");
                }
                sql.append(" ORDER BY fecha ASC");

                PreparedStatement stmt = conexion.prepareStatement(sql.toString());

                int index = 1;
                if (!codigo.isEmpty()) {
                    stmt.setString(index++, codigo);
                }
                if (!anio.isEmpty()) {
                    stmt.setInt(index++, Integer.parseInt(anio));
                }
                if (mes > 0) {
                    stmt.setInt(index++, mes);
                }

                ResultSet rs = stmt.executeQuery();

                DefaultTableModel modeloReporte = new DefaultTableModel();
                modeloReporte.addColumn("Código");
                modeloReporte.addColumn("Nombre");
                modeloReporte.addColumn("Fecha");
                modeloReporte.addColumn("Hora");

                boolean hayResultados = false;
                while (rs.next()) {
                    hayResultados = true;
                    String[] partes = rs.getString("fecha").split(" ");
                    modeloReporte.addRow(new Object[]{
                        rs.getString("uid"),
                        rs.getString("nombre"),
                        partes[0],
                        partes.length > 1 ? partes[1] : ""
                    });
                }

                if (!hayResultados) {
                    JOptionPane.showMessageDialog(dialogo, "No se encontraron registros.");
                    return;
                }

                JDialog resultado = new JDialog(dialogo, "Resultados", true);
                resultado.setLayout(new BorderLayout());
                resultado.setSize(700, 350);

                JTable tabla = new JTable(modeloReporte);
                resultado.add(new JScrollPane(tabla), BorderLayout.CENTER);

                JButton btnLimpiar = new JButton("Limpiar Búsqueda");
                btnLimpiar.addActionListener(ev -> {
                    campoCodigo.setText("");
                    resultado.dispose();
                    escanearUID.run(); // reactivar escaneo
                });
                resultado.add(btnLimpiar, BorderLayout.SOUTH);

                resultado.setLocationRelativeTo(dialogo);
                resultado.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });

        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }

    private static void eliminarTabla() {
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS asistencia");
            JOptionPane.showMessageDialog(ventana, "Tabla eliminada correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ventana, "Error al eliminar la tabla: " + e.getMessage());
        }
    }

    private static void eliminarRegistro() {
        String nombre = JOptionPane.showInputDialog("Nombre del docente a eliminar:");
        if (nombre != null) {
            try {
                PreparedStatement stmt = conexion.prepareStatement("DELETE FROM docentes WHERE nombre = ?");
                stmt.setString(1, nombre);
                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(ventana, "Registro eliminado.");
                } else {
                    JOptionPane.showMessageDialog(ventana, "No se encontró el registro.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(ventana, "Error: " + e.getMessage());
            }
        }
    }

    private static void mostrarDatos() {
        JDialog dialogo = new JDialog(ventana, "Datos registrados", true);
        dialogo.setSize(900, 300);
        DefaultTableModel modelo = new DefaultTableModel();

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

        JTable tabla = new JTable(modelo);
        dialogo.add(new JScrollPane(tabla));
        dialogo.setVisible(true);
    }

    private static void iniciarControlRFID() {
        JDialog lectorDialog = new JDialog(ventana, "Escanear Tarjeta", true);
        lectorDialog.setSize(300, 150);
        lectorDialog.setLayout(new BorderLayout());

        JLabel mensaje = new JLabel("Por favor, escanee su tarjeta...", SwingConstants.CENTER);
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
                        registrarAsistencia(uid);
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

    private static void registrarAsistencia(String uid) {
        try {
            PreparedStatement stmt = conexion.prepareStatement("SELECT nombre FROM docentes WHERE cod_docente = ?");
            stmt.setString(1, uid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());

                // Mostrar saludo
                JOptionPane.showMessageDialog(ventana, "Hola " + nombre + ", ¡Bienvenido!");

                // Agregar a tabla en tiempo real
                modeloAsistencia.addRow(new String[]{fecha, hora, nombre});

                // Aquí puedes insertar en una tabla de asistencia si la tienes en la base
            } else {
                JOptionPane.showMessageDialog(ventana, "UID no registrado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ventana, "Error: " + e.getMessage());
        }
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
    
}
