
package com.mycompany.controldocentes;

/*
 *integrantes:  Kelvin Calle
 *              Denilson Apaza
 */
import com.fazecast.jSerialComm.SerialPort;
import com.mycompany.controldocentes.conexionBD.ConexionBD;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Color;

import org.apache.poi.ss.usermodel.*;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ControlDocentes {

    private static Connection conexion;
    private static JFrame ventana;
    private static JTable tablaAsistencia;
    private static DefaultTableModel modeloAsistencia;

    public static void main(String[] args) {
        conectarBaseDatos();
        crearVentana();
    }
    
    //conexion a la base de datos de mysql 
    private static void conectarBaseDatos() {
        conexion = ConexionBD.getConnection();
    }
    //ventana principal
    private static void crearVentana() {
        ventana = new JFrame("Control de Docentes");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1000, 600); // Aumentamos el tamaño de la ventana
        ventana.setLocationRelativeTo(null);
        ventana.setLayout(null);

        // Fondo para la ventana principal
        ImageIcon originalIcon = new ImageIcon("src/imagenes/menu_docente.jpg");
        Image imagenOriginal = originalIcon.getImage();
        Image imagenEscalada = imagenOriginal.getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
        JLabel fondo = new JLabel(iconoEscalado);
        fondo.setBounds(0, 0, 1000, 600);
        fondo.setLayout(null);

        // Logo izquierdo escalado
        ImageIcon logoIzqOriginal = new ImageIcon("src/imagenes/usb_logo.png");
        Image imgIzqEscalada = logoIzqOriginal.getImage().getScaledInstance(150, 75, Image.SCALE_SMOOTH);
        ImageIcon logoIzqEscalado = new ImageIcon(imgIzqEscalada);
        JLabel lblLogoIzquierdo = new JLabel(logoIzqEscalado);
        lblLogoIzquierdo.setBounds(20, 10, 150, 75);

        // Logo derecho escalado
        ImageIcon logoDerOriginal = new ImageIcon("src/imagenes/ACN_logo.png");
        Image imgDerEscalada = logoDerOriginal.getImage().getScaledInstance(150, 75, Image.SCALE_SMOOTH);
        ImageIcon logoDerEscalado = new ImageIcon(imgDerEscalada);
        JLabel lblLogoDerecho = new JLabel(logoDerEscalado);
        lblLogoDerecho.setBounds(850, 10, 130, 75);


        // Título
        JLabel titulo = new JLabel("CONTROL DE DOCENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(Color.GREEN);
        titulo.setBounds(340, 30, 400, 30);

        // Estilo común de botones
        Color azulBoton = new Color(0, 102, 204);
        Font fuenteBoton = new Font("Arial", Font.BOLD, 14);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(50, 120, 200, 40);
        btnRegistrar.setBackground(azulBoton);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(fuenteBoton);
        btnRegistrar.addActionListener(e -> insertarDatos());

        JButton btnVerReporte = new JButton("Ver Reporte");
        btnVerReporte.setBounds(50, 180, 200, 40);
        btnVerReporte.setBackground(azulBoton);
        btnVerReporte.setForeground(Color.WHITE);
        btnVerReporte.setFont(fuenteBoton);
        btnVerReporte.addActionListener(e -> mostrarReporte());

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(50, 240, 200, 40);
        btnEditar.setBackground(azulBoton);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFont(fuenteBoton);
        btnEditar.addActionListener(e -> mostrarOpcionesEditar());

        JButton btnIniciarControl = new JButton("Iniciar Control");
        btnIniciarControl.setBounds(50, 300, 200, 40);
        btnIniciarControl.setBackground(azulBoton);
        btnIniciarControl.setForeground(Color.WHITE);
        btnIniciarControl.setFont(fuenteBoton);
        btnIniciarControl.addActionListener(e -> iniciarControlRFID());

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBounds(50, 360, 200, 40);
        btnSalir.setBackground(azulBoton);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(fuenteBoton);
        btnSalir.addActionListener(e -> System.exit(0));

        modeloAsistencia = new DefaultTableModel(new String[]{"Fecha", "Hora", "Nombre"}, 0);
        tablaAsistencia = new JTable(modeloAsistencia);
        JScrollPane scroll = new JScrollPane(tablaAsistencia);
        scroll.setBounds(300, 120, 660, 350); // ajustado al nuevo tamaño

        fondo.add(lblLogoIzquierdo);
        fondo.add(lblLogoDerecho);
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
    
    
    //metodo para desplegar opcioes de editar en pantalla
    private static void mostrarOpcionesEditar() {
        String[] opciones = {"Eliminar Tabla", "Eliminar Registro", "Mostrar Registros", "Actualizar registro"};
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
                case "Actualizar registro":
                    actualizarRegistro();  
                    break;
                case "Mostrar Registros":
                    mostrarDatos();
                    break;
            }
        }
    }
    //metodo para registrar nuevos docentes
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
        // con este THREAD hacemos el funcionamiento del escaneo de tarjeta rfid
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
    
    
    // aqui podremos mostrar el reporte con escaneo de tarjeta
    
    private static void mostrarReporte() {
        JDialog dialogo = new JDialog(ventana, "Reporte de Asistencia", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        dialogo.setSize(400, 200);

        JTextField campoCodigo = new JTextField();
        campoCodigo.setEditable(false);

        JTextField campoAnio = new JTextField();

        String[] meses = {
            "Seleccionar mes...", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
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

        escanearUID.run();

        campoCodigo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            boolean escaneando = false;

            private void reactivarSiVacio() {
                if (campoCodigo.getText().trim().isEmpty() && !escaneando) {
                    escaneando = true;
                    escanearUID.run();
                    new javax.swing.Timer(1000, evt -> escaneando = false).start();
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                reactivarSiVacio();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                reactivarSiVacio();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
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
                resultado.setSize(800, 400);

                JTable tabla = new JTable(modeloReporte);
                resultado.add(new JScrollPane(tabla), BorderLayout.CENTER);

                JPanel panelBotones = new JPanel();
                JButton btnLimpiar = new JButton("Limpiar Búsqueda");
                //agregando imagen al los botones de exportacion
                ImageIcon iconoPDF = new ImageIcon("src/imagenes/icono_pdf.jpg");
                ImageIcon iconoEXCEL = new ImageIcon("src/imagenes/icono_excel.jpg");
                
                Image imgPDF = iconoPDF.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgEXCEL = iconoEXCEL.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                
                ImageIcon iconoPDFEscalado = new ImageIcon(imgPDF);
                ImageIcon iconoEXCELEscalado = new ImageIcon(imgEXCEL);
                
                JButton btnExportarPDF = new JButton(iconoPDFEscalado);
                btnExportarPDF.setToolTipText("Exportar PDF");
                btnExportarPDF.setBorderPainted(false);
                btnExportarPDF.setContentAreaFilled(false);
                
                
                
                JButton btnExportarExcel = new JButton(iconoEXCELEscalado);
                btnExportarExcel.setToolTipText("Exportar EXCEL");
                btnExportarExcel.setBorderPainted(false);
                btnExportarExcel.setContentAreaFilled(false);
               
                
                // Exportar PDF
                btnExportarPDF.addActionListener(ev -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File("reporte.pdf"));
                    if (fileChooser.showSaveDialog(resultado) == JFileChooser.APPROVE_OPTION) {
                        try {
                            Document document = new Document();
                            PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                            document.open();

                            PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
                            for (int i = 0; i < tabla.getColumnCount(); i++) {
                                pdfTable.addCell(tabla.getColumnName(i));
                            }
                            for (int i = 0; i < tabla.getRowCount(); i++) {
                                for (int j = 0; j < tabla.getColumnCount(); j++) {
                                    pdfTable.addCell(tabla.getValueAt(i, j).toString());
                                }
                            }

                            document.add(new Paragraph("Reporte de Asistencia"));
                            document.add(pdfTable);
                            document.close();
                            JOptionPane.showMessageDialog(resultado, "PDF exportado correctamente.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(resultado, "Error al exportar PDF: " + ex.getMessage());
                        }
                    }
                });

                // Exportar Excel
                btnExportarExcel.addActionListener(ev -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File("reporte.xlsx"));
                    if (fileChooser.showSaveDialog(resultado) == JFileChooser.APPROVE_OPTION) {
                        try (Workbook workbook = new XSSFWorkbook()) {
                            Sheet sheet = workbook.createSheet("Reporte");
                            Row header = sheet.createRow(0);
                            for (int i = 0; i < tabla.getColumnCount(); i++) {
                                header.createCell(i).setCellValue(tabla.getColumnName(i));
                            }
                            for (int i = 0; i < tabla.getRowCount(); i++) {
                                Row row = sheet.createRow(i + 1);
                                for (int j = 0; j < tabla.getColumnCount(); j++) {
                                    row.createCell(j).setCellValue(tabla.getValueAt(i, j).toString());
                                }
                            }
                            FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile());
                            workbook.write(fileOut);
                            fileOut.close();
                            JOptionPane.showMessageDialog(resultado, "Excel exportado correctamente.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(resultado, "Error al exportar Excel: " + ex.getMessage());
                        }
                    }
                });

                btnLimpiar.addActionListener(ev -> {
                    campoCodigo.setText("");
                    resultado.dispose();
                    escanearUID.run();
                });

                panelBotones.add(btnLimpiar);
                panelBotones.add(btnExportarPDF);
                panelBotones.add(btnExportarExcel);
                resultado.add(panelBotones, BorderLayout.SOUTH);

                resultado.setLocationRelativeTo(dialogo);
                resultado.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });

        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }
    
    // eliminar tabla de la base de datos 
    private static void eliminarTabla() {
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS asistencia");
            JOptionPane.showMessageDialog(ventana, "Tabla eliminada correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ventana, "Error al eliminar la tabla: " + e.getMessage());
        }
    }
    //metodo para eliminar el registro una ves que haya concluido su servicio educatico 
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
    
    //metodo para actualizar registro si existe errores de duplicidad de nombre, etc.
    private static void actualizarRegistro() {
        JDialog dialogo = new JDialog(ventana, "Actualizar Registro de Docente", true);
        dialogo.setLayout(new GridLayout(11, 2, 5, 5));
        dialogo.setSize(400, 400);

        JTextField campoCodigo = new JTextField();
        campoCodigo.setEditable(false);// no editable el campo cod_docente para auto llenado de datos

        JTextField campoNombre = new JTextField();
        JTextField campoApellidoPaterno = new JTextField();
        JTextField campoApellidoMaterno = new JTextField();
        JTextField campoHoraEntrada = new JTextField();
        JTextField campoHoraSalida = new JTextField();
        JTextField campoCantidadAlumnado = new JTextField();
        JTextField campoMateria = new JTextField();
        JTextField campoAula = new JTextField();
        JTextField campoTurno = new JTextField();

        dialogo.add(new JLabel("Código RFID del docente (escaneado):"));
        dialogo.add(campoCodigo);
        dialogo.add(new JLabel("Nombre:"));
        dialogo.add(campoNombre);
        dialogo.add(new JLabel("Apellido Paterno:"));
        dialogo.add(campoApellidoPaterno);
        dialogo.add(new JLabel("Apellido Materno:"));
        dialogo.add(campoApellidoMaterno);
        dialogo.add(new JLabel("Hora Entrada:"));
        dialogo.add(campoHoraEntrada);
        dialogo.add(new JLabel("Hora Salida:"));
        dialogo.add(campoHoraSalida);
        dialogo.add(new JLabel("Cantidad Alumnado:"));
        dialogo.add(campoCantidadAlumnado);
        dialogo.add(new JLabel("Materia:"));
        dialogo.add(campoMateria);
        dialogo.add(new JLabel("Aula:"));
        dialogo.add(campoAula);
        dialogo.add(new JLabel("Turno:"));
        dialogo.add(campoTurno);

        JButton btnActualizar = new JButton("Actualizar");
        dialogo.add(new JLabel());
        dialogo.add(btnActualizar);

        // Runnable para escanear el UID
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
                            SwingUtilities.invokeLater(() -> {
                                campoCodigo.setText(codigo);
                                cargarDatosDocente(codigo, campoNombre, campoApellidoPaterno, campoApellidoMaterno,
                                        campoHoraEntrada, campoHoraSalida, campoCantidadAlumnado,
                                        campoMateria, campoAula, campoTurno);
                            });
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

        // Botón para actualizar el registro
        btnActualizar.addActionListener(e -> {
            String codigo = campoCodigo.getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, escanee primero la tarjeta RFID.");
                return;
            }

            try {
                // Actualizar los datos del docente en la base de datos
                PreparedStatement stmt = conexion.prepareStatement(
                        "UPDATE docentes SET nombre=?, apellido_paterno=?, apellido_materno=?, hora_entrada=?, hora_salida=?, cantidad_alumnado=?, materia=?, aula=?, turno=? WHERE cod_docente=?"
                );
                stmt.setString(1, campoNombre.getText());
                stmt.setString(2, campoApellidoPaterno.getText());
                stmt.setString(3, campoApellidoMaterno.getText());
                stmt.setString(4, campoHoraEntrada.getText());
                stmt.setString(5, campoHoraSalida.getText());
                stmt.setString(6, campoCantidadAlumnado.getText());
                stmt.setString(7, campoMateria.getText());
                stmt.setString(8, campoAula.getText());
                stmt.setString(9, campoTurno.getText());
                stmt.setString(10, codigo);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialogo, "Datos actualizados correctamente.");
                dialogo.dispose();  // Cierra el diálogo después de actualizar
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error al actualizar el registro: " + ex.getMessage());
            }
        });

        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }

// Método para cargar los datos del docente en el formulario
    private static void cargarDatosDocente(String codigo, JTextField campoNombre, JTextField campoApellidoPaterno,
            JTextField campoApellidoMaterno, JTextField campoHoraEntrada,
            JTextField campoHoraSalida, JTextField campoCantidadAlumnado,
            JTextField campoMateria, JTextField campoAula, JTextField campoTurno) {
        try {
            PreparedStatement stmt = conexion.prepareStatement("SELECT * FROM docentes WHERE cod_docente = ?");
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campoNombre.setText(rs.getString("nombre"));
                campoApellidoPaterno.setText(rs.getString("apellido_paterno"));
                campoApellidoMaterno.setText(rs.getString("apellido_materno"));
                campoHoraEntrada.setText(rs.getString("hora_entrada"));
                campoHoraSalida.setText(rs.getString("hora_salida"));
                campoCantidadAlumnado.setText(rs.getString("cantidad_alumnado"));
                campoMateria.setText(rs.getString("materia"));
                campoAula.setText(rs.getString("aula"));
                campoTurno.setText(rs.getString("turno"));
            } else {
                JOptionPane.showMessageDialog(ventana, "No se encontró un docente con ese código.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "Error al cargar los datos: " + e.getMessage());
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
    // metodo para mostrar en consola cada escaneo de tarjeta rfid
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

