
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormatSymbols;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.table.JTableHeader;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.RectangleEdge;

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
        ventana.setSize(1050, 600); // Aumentamos el tamaño de la ventan
        ventana.setLocationRelativeTo(null);
        ventana.setLayout(null);

        // Fondo para la ventana principal
        ImageIcon originalIcon = new ImageIcon("src/imagenes/menu_docente2.gif");
        Image imagenOriginal = originalIcon.getImage();
        //Image imagenEscalada = imagenOriginal.getScaledInstance(1050, 600, Image.SCALE_SMOOTH);
        
        ImageIcon iconoEscalado = new ImageIcon(imagenOriginal);
        JLabel fondo = new JLabel(iconoEscalado);
        fondo.setBounds(0, 0, 1050, 600);
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
        /*JLabel titulo = new JLabel("CONTROL DE DOCENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(Color.GREEN);
        titulo.setBounds(340, 30, 400, 30);*/
        JLabel titulo = new JLabel("CONTROL DE DOCENTES", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(0, 255, 180));
        titulo.setBounds(300, 20, 400, 40);
        titulo.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 150), 2));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(20, 0, 30));

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
        
        JButton btnLogin = new JButton("Perfil Docente");
        btnLogin.setBounds(50, 360, 200, 40);
        btnLogin.setBackground(azulBoton);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(fuenteBoton);
        btnLogin.addActionListener(e -> mostrarVentanaLogin());


        JButton btnSalir = new JButton("Salir");
        btnSalir.setBounds(50, 420, 200, 40);
        btnSalir.setBackground(azulBoton);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(fuenteBoton);
        btnSalir.addActionListener(e -> System.exit(0));

        /*modeloAsistencia = new DefaultTableModel(new String[]{"Fecha", "Hora", "Nombre"}, 0);
        tablaAsistencia = new JTable(modeloAsistencia);
        JScrollPane scroll = new JScrollPane(tablaAsistencia);
        scroll.setBounds(300, 120, 660, 350); // ajustado al nuevo tamaño*/
        
        // === Tabla de asistencia con estilo ===
        modeloAsistencia = new DefaultTableModel(new String[]{"Nombre", "Fecha", "Hora"}, 0);
        tablaAsistencia = new JTable(modeloAsistencia);

        tablaAsistencia.setBackground(new Color(30, 0, 50));
        tablaAsistencia.setForeground(new Color(0, 255, 180));
        tablaAsistencia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaAsistencia.setRowHeight(25);
        tablaAsistencia.setGridColor(new Color(0, 255, 150));
        tablaAsistencia.setSelectionBackground(new Color(50, 0, 70));
        tablaAsistencia.setSelectionForeground(Color.WHITE);

        JTableHeader header = tablaAsistencia.getTableHeader();
        header.setBackground(new Color(20, 20, 40));
        header.setForeground(new Color(0, 255, 200));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tablaAsistencia);
        scroll.setBounds(300, 120, 660, 350);
        scroll.getViewport().setBackground(new Color(20, 0, 30));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 150), 2));

        fondo.add(lblLogoIzquierdo);
        fondo.add(lblLogoDerecho);
        fondo.add(titulo);
        fondo.add(btnRegistrar);
        fondo.add(btnVerReporte);
        fondo.add(btnEditar);
        fondo.add(btnIniciarControl);
        fondo.add(btnLogin);
        fondo.add(btnSalir);
        fondo.add(scroll);

        ventana.setContentPane(fondo);
        ventana.setVisible(true);
    }
    
    
    
    //metodo para desplegar opcioes de editar en pantalla
    private static void mostrarOpcionesEditar() {
        String[] opciones = {"Mostrar Registros", "Actualizar registro", "Eliminar Tabla", "Eliminar Registro", "Eliminar Asistencia Total", "Eliminar Asistencia"};
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
                case "Mostrar Registros":
                    mostrarDatos();
                    break;
                case "Actualizar registro":
                    actualizarRegistro();  
                    break;
                case "Eliminar Tabla":
                    eliminarTabla();
                    break;
                case "Eliminar Registro":
                    eliminarRegistro();
                    break;
                case "Eliminar Asistencia Total":
                    eliminarAsistenciaTotal();
                    break;
                case "Eliminar Asistencia":
                    eliminarAsistencia();
                    break;
                
                
            }
        }
    }
    
   
    /*
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
    */
   
    private static void insertarDatos() {
        JDialog dialogo = new JDialog(ventana, "Registrarse", true);
        dialogo.setLayout(new BorderLayout());

        // === Colores estilo neón oscuro ===
        Color fondoOscuro = new Color(30, 0, 50);
        Color textoNeon = new Color(0, 255, 180);
        Color bordeNeon = new Color(0, 255, 150);
        Color botonFondo = new Color(40, 40, 60);
        Color botonTexto = new Color(0, 255, 200);

        JPanel panelFormulario = new JPanel(new GridLayout(11, 2, 5, 5));
        String[] etiquetas = {"Código Docente (escanear TJ):", "Nombre:", "Apellido Paterno:", "Apellido Materno:",
            "Hora Entrada:", "Hora Salida:", "Cantidad Alumnado:", "Materia:", "Aula:", "Turno:"};

        JTextField[] campos = new JTextField[10];
        JComboBox<String>[] combosHora = new JComboBox[2];
        JComboBox<String> comboTurno = new JComboBox<>(new String[]{"Mañana", "Tarde", "Noche"});

        for (int i = 0; i < 10; i++) {
            panelFormulario.add(new JLabel(etiquetas[i]));
            if (i == 4 || i == 5) {
                JComboBox<String> comboHora = new JComboBox<>();
                combosHora[i - 4] = comboHora;
                panelFormulario.add(comboHora);
            } else if (i == 9) {
                panelFormulario.add(comboTurno);
            } else {
                campos[i] = new JTextField();
                campos[i].setEditable(i != 0);
                panelFormulario.add(campos[i]);
            }
        }

        // Cambiar las horas según el turno
        comboTurno.addActionListener(e -> {
            String turnoSeleccionado = (String) comboTurno.getSelectedItem();
            String[] horas = generarHorasPorTurno(turnoSeleccionado);
            for (JComboBox<String> combo : combosHora) {
                combo.removeAllItems();
                for (String h : horas) {
                    combo.addItem(h);
                }
            }
        });
        comboTurno.setSelectedIndex(0); // Dispara el primer llenado de horas

        // Panel derecho para imagen
        JPanel panelImagen = new JPanel(new BorderLayout());
        JLabel labelImagen = new JLabel();
        labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
        labelImagen.setVerticalAlignment(SwingConstants.CENTER);
        labelImagen.setPreferredSize(new Dimension(200, 200));

        JButton btnCargarImagen = new JButton("Cargar Foto");
        final String[] rutaImagen = {null};

        
        
        btnCargarImagen.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showOpenDialog(dialogo);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File imagenSeleccionada = fileChooser.getSelectedFile();

                // === Crear carpeta 'fotos' si no existe ===
                File carpetaFotos = new File("src/fotos_docentes");
                if (!carpetaFotos.exists()) {
                    carpetaFotos.mkdirs();
                }

                // === Copiar imagen a la carpeta 'fotos' con un nombre único ===
                String nombreArchivo = campos[0].getText().trim();
                if (nombreArchivo.isEmpty()) {
                    nombreArchivo = String.valueOf(System.currentTimeMillis()); // fallback
                }

                String extension = imagenSeleccionada.getName().substring(imagenSeleccionada.getName().lastIndexOf('.'));
                File destino = new File(carpetaFotos, nombreArchivo + extension);
                try {
                    Files.copy(imagenSeleccionada.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    rutaImagen[0] = destino.getAbsolutePath(); // Ruta final guardada

                    ImageIcon icon = new ImageIcon(new ImageIcon(rutaImagen[0])
                            .getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                    labelImagen.setIcon(icon);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialogo, "Error al copiar la imagen: " + ex.getMessage());
                }
            }
        });

        // === Estilo visual (paso 2) ===
        dialogo.getContentPane().setBackground(fondoOscuro);
        panelFormulario.setBackground(fondoOscuro);
        panelImagen.setBackground(fondoOscuro);
        labelImagen.setBorder(BorderFactory.createLineBorder(bordeNeon, 2));
        labelImagen.setOpaque(true);
        labelImagen.setBackground(new Color(10, 10, 20));

        panelImagen.add(labelImagen, BorderLayout.CENTER);
        panelImagen.add(btnCargarImagen, BorderLayout.SOUTH);

        
        final SerialPort[] puerto = new SerialPort[1];
        // Escaneo RFID
        new Thread(() -> {
            
            for(SerialPort sp : SerialPort.getCommPorts()){
                if(sp.getSystemPortName().equals("COM4")){
                    puerto[0] = sp;
                    break;
                }
            }
            puerto[0].setComPortParameters(9600, 8, 1, 0);
            puerto[0].setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            if (puerto[0].openPort()) {
                Scanner scanner = new Scanner(puerto[0].getInputStream());
                while (dialogo.isVisible()) {
                    if (scanner.hasNextLine()) {
                        String codigo = scanner.nextLine().trim();
                        System.out.println("tarjeta leída: " + codigo);
                        SwingUtilities.invokeLater(() -> campos[0].setText(codigo));
                        break;
                    }
                }
                scanner.close();
                puerto[0].closePort();
            }
        }).start();
        
        dialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (puerto[0] != null && puerto[0].isOpen()) {
                    puerto[0].closePort();
                    System.out.println("Puerto cerrado al cerrar ventana.");
                }
            }
        });

        JButton btnInsertar = new JButton("Registrar");
        btnInsertar.addActionListener(e -> {
            try (PreparedStatement stmt = conexion.prepareStatement(
                    "INSERT INTO docentes VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, campos[0].getText()); // Código
                stmt.setString(2, campos[1].getText()); // Nombre
                stmt.setString(3, campos[2].getText()); // Apellido Paterno
                stmt.setString(4, campos[3].getText()); // Apellido Materno
                stmt.setString(5, (String) combosHora[0].getSelectedItem()); // Hora Entrada
                stmt.setString(6, (String) combosHora[1].getSelectedItem()); // Hora Salida
                stmt.setString(7, campos[6].getText()); // Cantidad Alumnado
                stmt.setString(8, campos[7].getText()); // Materia
                stmt.setString(9, campos[8].getText()); // Aula
                stmt.setString(10, (String) comboTurno.getSelectedItem()); // Turno

                stmt.executeUpdate(); // ¡IMPORTANTE! Ejecutar la inserción
                JOptionPane.showMessageDialog(dialogo, "Registro exitoso.");
                
                if (puerto[0] != null && puerto[0].isOpen()) {
                    puerto[0].closePort();
                    System.out.println("Puerto cerrado al registra docente.");
                } 
                
                dialogo.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });

        panelFormulario.add(new JLabel());
        panelFormulario.add(btnInsertar);

        // === Estilo visual (paso 3) ===
        for (Component comp : panelFormulario.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(textoNeon);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setBackground(new Color(40, 0, 60));
                field.setForeground(textoNeon);
                field.setCaretColor(Color.WHITE);
                field.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));

                field.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        field.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        field.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
                    }
                });
            } else if (comp instanceof JComboBox) {
                comp.setBackground(new Color(50, 0, 80));
                comp.setForeground(textoNeon);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
        }

        btnCargarImagen.setBackground(botonFondo);
        btnCargarImagen.setForeground(botonTexto);
        btnInsertar.setBackground(botonFondo);
        btnInsertar.setForeground(botonTexto);

        dialogo.add(panelFormulario, BorderLayout.CENTER);
        dialogo.add(panelImagen, BorderLayout.EAST);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
   
        
    }
    
    private static String[] generarHorasPorTurno(String turno) {
        List<String> horas = new ArrayList<>();
        int inicio = 6, fin = 22;

        switch (turno) {
            case "Mañana":
                inicio = 6;
                fin = 12;
                break;
            case "Tarde":
                inicio = 13;
                fin = 18;
                break;
            case "Noche":
                inicio = 19;
                fin = 22;
                break;
        }

        for (int h = inicio; h <= fin; h++) {
            for (int m = 0; m < 60; m += 30) {
                if ("Mañana".equals(turno) && h == 12 && m > 0) {
                    break;
                }
                horas.add(String.format("%02d:%02d", h, m));
            }
        }
        return horas.toArray(new String[0]);
    }
    
    
    /*private static void mostrarReporte() {
        JDialog dialogo = new JDialog(ventana, "Reporte de Asistencia", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        dialogo.setSize(400, 200);

        // Colores neón
        Color fondoNeon = new Color(30, 0, 60);
        Color textoNeon = new Color(0, 255, 180);
        Color bordeNeon = new Color(150, 0, 255);

        dialogo.getContentPane().setBackground(fondoNeon);

        JTextField campoCodigo = new JTextField();
        campoCodigo.setEditable(false);

        JTextField campoAnio = new JTextField();

        String[] meses = {
            "Seleccionar mes...", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        JComboBox<String> comboMes = new JComboBox<>(meses);

        JLabel lbl1 = new JLabel("Código RFID del docente (opcional):");
        JLabel lbl2 = new JLabel("Mes (opcional):");
        JLabel lbl3 = new JLabel("Año (ej. 2025, obligatorio si hay mes):");

        // Aplicar estilos a labels
        for (JLabel label : new JLabel[]{lbl1, lbl2, lbl3}) {
            label.setForeground(textoNeon);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        // Aplicar estilos a campos de texto
        for (JTextField campo : new JTextField[]{campoCodigo, campoAnio}) {
            campo.setBackground(new Color(40, 0, 60));
            campo.setForeground(textoNeon);
            campo.setCaretColor(Color.WHITE);
            campo.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
            campo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    campo.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    campo.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
                }
            });
        }

        comboMes.setBackground(new Color(50, 0, 80));
        comboMes.setForeground(textoNeon);
        comboMes.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(bordeNeon);
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);

        dialogo.add(lbl1);
        dialogo.add(campoCodigo);
        dialogo.add(lbl2);
        dialogo.add(comboMes);
        dialogo.add(lbl3);
        dialogo.add(campoAnio);
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
                resultado.getContentPane().setBackground(new Color(20, 20, 30));
                resultado.setSize(800, 400);

                JTable tabla = new JTable(modeloReporte);
                resultado.add(new JScrollPane(tabla), BorderLayout.CENTER);
                
                tabla.setShowGrid(true);
                tabla.setGridColor(new Color(138, 43, 226));
                tabla.setForeground(Color.WHITE);
                tabla.setBackground(new Color(20, 20, 30));
                tabla.setSelectionBackground(new Color(138, 43, 226));
                tabla.setSelectionForeground(Color.BLACK);
                tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
                tabla.setRowHeight(28);

                JPanel panelBotones = new JPanel();
                JButton btnLimpiar = new JButton("Limpiar Búsqueda");
                Color BotonFondo = new Color(138, 43, 226);
                Color BotonTexto = Color.WHITE;
                btnLimpiar.setBackground(BotonFondo);
                btnLimpiar.setForeground(BotonTexto);
                btnLimpiar.setFocusPainted(false);
                btnLimpiar.setFont(new Font("SansSerif", Font.BOLD, 14));
                
                
                
                //agregando imagen al los botones de exportacion
                ImageIcon iconoPDF = new ImageIcon("src/imagenes/icono_pdf.jpg");
                ImageIcon iconoEXCEL = new ImageIcon("src/imagenes/icono_excel.jpg");
                
                Image imgPDF = iconoPDF.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgEXCEL = iconoEXCEL.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                
                ImageIcon iconoPDFEscalado = new ImageIcon(imgPDF);
                ImageIcon iconoEXCELEscalado = new ImageIcon(imgEXCEL);
                
                JButton btnExportarPDF = new JButton(iconoPDFEscalado);
                btnExportarPDF.setToolTipText("Exportar PDF");
                btnExportarPDF.setBorder(BorderFactory.createLineBorder(new Color(138, 43, 226)));
                btnExportarPDF.setFocusPainted(false);
                btnExportarPDF.setContentAreaFilled(false);
                
                
                
                JButton btnExportarExcel = new JButton(iconoEXCELEscalado);
                btnExportarExcel.setToolTipText("Exportar EXCEL");
                btnExportarExcel.setFocusPainted(false);
                btnExportarExcel.setBorder(BorderFactory.createLineBorder(new Color(138, 43, 226)));
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
    }*/
    
    private static void mostrarReporte() {
        JDialog dialogo = new JDialog(ventana, "Reporte de Asistencia", true);
        dialogo.setLayout(new GridLayout(4, 2, 5, 5));
        dialogo.setSize(400, 200);

        Color fondoNeon = new Color(30, 0, 60);
        Color textoNeon = new Color(0, 255, 180);
        Color bordeNeon = new Color(150, 0, 255);

        dialogo.getContentPane().setBackground(fondoNeon);

        JTextField campoCodigo = new JTextField();
        campoCodigo.setEditable(false);
        JTextField campoAnio = new JTextField();

        String[] meses = {
            "Seleccionar mes...", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        JComboBox<String> comboMes = new JComboBox<>(meses);

        JLabel lbl1 = new JLabel("Código RFID del docente (opcional):");
        JLabel lbl2 = new JLabel("Mes (opcional):");
        JLabel lbl3 = new JLabel("Año (ej. 2025, obligatorio si hay mes):");

        for (JLabel label : new JLabel[]{lbl1, lbl2, lbl3}) {
            label.setForeground(textoNeon);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        for (JTextField campo : new JTextField[]{campoCodigo, campoAnio}) {
            campo.setBackground(new Color(40, 0, 60));
            campo.setForeground(textoNeon);
            campo.setCaretColor(Color.WHITE);
            campo.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
            campo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    campo.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    campo.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
                }
            });
        }

        comboMes.setBackground(new Color(50, 0, 80));
        comboMes.setForeground(textoNeon);
        comboMes.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(bordeNeon);
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);

        dialogo.add(lbl1);
        dialogo.add(campoCodigo);
        dialogo.add(lbl2);
        dialogo.add(comboMes);
        dialogo.add(lbl3);
        dialogo.add(campoAnio);
        dialogo.add(new JLabel());
        dialogo.add(btnBuscar);

        
        final SerialPort[] puerto = new SerialPort[1];
        final Thread[] hiloEscaneo = new Thread[1];
        final boolean[]escaneando = {false};
        Runnable escanearUID = () -> {
            if (escaneando[0]) {
                return;  // Evita escaneos múltiples
            }
            escaneando[0] = true;

            hiloEscaneo[0] = new Thread(() -> {
                for (SerialPort sp : SerialPort.getCommPorts()) {
                    if (sp.getSystemPortName().equals("COM4")) {
                        puerto[0] = sp;
                        break;
                    }
                }

                if (puerto[0] == null) {
                    System.out.println("Puerto COM4 no encontrado.");
                    escaneando[0] = false;
                    return;
                }

                puerto[0].setComPortParameters(9600, 8, 1, 0);
                puerto[0].setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

                if (puerto[0].openPort()) {
                    try (Scanner scanner = new Scanner(puerto[0].getInputStream())) {
                        while (dialogo.isVisible() && campoCodigo.getText().isEmpty()) {
                            if (scanner.hasNextLine()) {
                                String codigo = scanner.nextLine().trim();
                                System.out.println("UID escaneado: " + codigo);
                                SwingUtilities.invokeLater(() -> campoCodigo.setText(codigo));
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Error durante escaneo: " + ex.getMessage());
                    } finally {
                        if (puerto[0].isOpen()) {
                            puerto[0].closePort();
                            System.out.println("Puerto COM cerrado desde escanearUID");
                        }
                        escaneando[0] = false;
                    }
                } else {
                    System.out.println("No se pudo abrir el puerto COM4");
                    escaneando[0] = false;
                }
            });

            hiloEscaneo[0].start();
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
                resultado.getContentPane().setBackground(new Color(20, 20, 30));
                resultado.setSize(800, 400);

                JTable tabla = new JTable(modeloReporte);
                resultado.add(new JScrollPane(tabla), BorderLayout.CENTER);

                tabla.setShowGrid(true);
                tabla.setGridColor(new Color(138, 43, 226));
                tabla.setForeground(Color.WHITE);
                tabla.setBackground(new Color(20, 20, 30));
                tabla.setSelectionBackground(new Color(138, 43, 226));
                tabla.setSelectionForeground(Color.BLACK);
                tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
                tabla.setRowHeight(28);

                JPanel panelBotones = new JPanel();
                JButton btnLimpiar = new JButton("Limpiar Búsqueda");
                Color BotonFondo = new Color(138, 43, 226);
                Color BotonTexto = Color.WHITE;
                btnLimpiar.setBackground(BotonFondo);
                btnLimpiar.setForeground(BotonTexto);
                btnLimpiar.setFocusPainted(false);
                btnLimpiar.setFont(new Font("SansSerif", Font.BOLD, 14));

                ImageIcon iconoPDF = new ImageIcon("src/imagenes/icono_pdf.jpg");
                ImageIcon iconoEXCEL = new ImageIcon("src/imagenes/icono_excel.jpg");
                Image imgPDF = iconoPDF.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgEXCEL = iconoEXCEL.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                ImageIcon iconoPDFEscalado = new ImageIcon(imgPDF);
                ImageIcon iconoEXCELEscalado = new ImageIcon(imgEXCEL);

                JButton btnExportarPDF = new JButton(iconoPDFEscalado);
                btnExportarPDF.setToolTipText("Exportar PDF");
                btnExportarPDF.setBorder(BorderFactory.createLineBorder(BotonFondo));
                btnExportarPDF.setFocusPainted(false);
                btnExportarPDF.setContentAreaFilled(false);

                JButton btnExportarExcel = new JButton(iconoEXCELEscalado);
                btnExportarExcel.setToolTipText("Exportar EXCEL");
                btnExportarExcel.setFocusPainted(false);
                btnExportarExcel.setBorder(BorderFactory.createLineBorder(BotonFondo));
                btnExportarExcel.setContentAreaFilled(false);

                JButton btnEstadistica = new JButton("Estadística");
                btnEstadistica.setBackground(BotonFondo);
                btnEstadistica.setForeground(BotonTexto);
                btnEstadistica.setFocusPainted(false);
                btnEstadistica.setFont(new Font("SansSerif", Font.BOLD, 14));

                btnEstadistica.addActionListener(ev -> {
                    Map<String, Integer> conteoPorMes = new HashMap<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    for (int i = 0; i < tabla.getRowCount(); i++) {
                        try {
                            Date fecha = sdf.parse(tabla.getValueAt(i, 2).toString());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(fecha);
                            int mesIndex = cal.get(Calendar.MONTH);
                            String mesNombre = new DateFormatSymbols().getMonths()[mesIndex];
                            conteoPorMes.put(mesNombre, conteoPorMes.getOrDefault(mesNombre, 0) + 1);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }

                    DefaultCategoryDataset datasetBarra = new DefaultCategoryDataset();
                    DefaultPieDataset<String> datasetTorta = new DefaultPieDataset<>();

                    /*
                    PASADO FUNCIONAL
                    for (Map.Entry<String, Integer> entry : conteoPorMes.entrySet()) {
                        //datasetBarra.addValue(entry.getValue(), "Asistencias", entry.getKey());
                        //datasetTorta.setValue(entry.getKey(), entry.getValue());
                    }
                    */
                    
                    //FUNCION ACTUALIZADA BARRA-TORTA
                    String[] nombresMeses = new DateFormatSymbols().getMonths();
                    int mesActual = Calendar.getInstance().get(Calendar.MONTH);
                    
                    for(int i = 0; i<= mesActual; i++){
                        String mesNombre = nombresMeses[i];
                        int cantidad = conteoPorMes.getOrDefault(mesNombre, 0);
                        datasetBarra.addValue(cantidad, "Asistencias", mesNombre);
                        datasetTorta.setValue(mesNombre, cantidad);
                    }

                    /*
                     PASADO FUNCIONAL
                    JFreeChart chartBarra = ChartFactory.createBarChart(
                            "Asistencias por Mes", "Mes", "Cantidad", datasetBarra,
                            PlotOrientation.VERTICAL, false, true, false);
                    JFreeChart chartTorta = ChartFactory.createPieChart(
                            "Distribución de Asistencias", datasetTorta, true, true, false);
                    */
                    
                    //BARRA ACTUALIZADA
                    JFreeChart chartBarra = ChartFactory.createBarChart(
                            "Asistencias por Mes", "Mes", "Cantidad", datasetBarra,
                            PlotOrientation.VERTICAL, false, true, false
                    );
                    chartBarra.setBackgroundPaint(new Color(30, 0, 60));
                    chartBarra.getPlot().setBackgroundPaint(new Color(50, 0, 80));
                    chartBarra.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(0, 255, 180));
   
                    NumberAxis ejeY = (NumberAxis)chartBarra.getCategoryPlot().getRangeAxis();
                    ejeY.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                    int maximo = conteoPorMes.values().stream().max(Integer::compare).orElse(1);
                    ejeY.setRange(0, Math.max(5, maximo + 1));
                    
                    //TORTA ACTUALIZADA
                    JFreeChart chartTorta = ChartFactory.createPieChart(
                            "Distribución de Asistencias", datasetTorta, true, true, false
                    );
                    chartTorta.setBackgroundPaint(new Color(30, 0, 60));
                    
                    PiePlot plot = (PiePlot)chartTorta.getPlot();
                    plot.setBackgroundPaint(new Color(50, 0, 80));
                    plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} asistencia"));
                    plot.setLabelBackgroundPaint(new Color(20, 0, 30));
                    plot.setLabelPaint(Color.WHITE);
                    chartTorta.getLegend().setPosition(RectangleEdge.RIGHT);
                    //chartTorta.getPlot().setBackgroundPaint(new Color(50, 0, 80));

                    ChartPanel panelBarra = new ChartPanel(chartBarra);
                    ChartPanel panelTorta = new ChartPanel(chartTorta);
                    JPanel panelGraficos = new JPanel(new GridLayout(1, 2));
                    panelGraficos.add(panelBarra);
                    panelGraficos.add(panelTorta);

                    JDialog ventanaGrafico = new JDialog(resultado, "Estadísticas", true);
                    ventanaGrafico.setSize(1600, 900);//DIMENSIONES ACTUALIZADA
                    ventanaGrafico.setLayout(new BorderLayout());
                    ventanaGrafico.add(panelGraficos, BorderLayout.CENTER);
                    ventanaGrafico.setLocationRelativeTo(resultado);
                    ventanaGrafico.setVisible(true);
                });

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

                    // Cerrar puerto si está abierto
                    if (puerto[0] != null && puerto[0].isOpen()) {
                        try {
                            puerto[0].closePort();
                            System.out.println("Puerto COM cerrado desde btnLimpiar.");
                        } catch (Exception ex) {
                            System.err.println("Error al cerrar el puerto: " + ex.getMessage());
                        }
                    }

                    escanearUID.run(); // Vuelve a activar el escaneo
                });

                panelBotones.add(btnLimpiar);
                panelBotones.add(btnEstadistica);
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
        dialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (puerto[0] != null && puerto[0].isOpen()) {
                    puerto[0].closePort();
                    System.out.println("Puerto cerrado al cerrar diálogo.");
                }

                if (hiloEscaneo[0] != null && hiloEscaneo[0].isAlive()) {
                    hiloEscaneo[0].interrupt();
                    System.out.println("Hilo de escaneo interrumpido.");
                }
            }
        });
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
    
    private static void eliminarAsistenciaTotal() {
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate("DELETE FROM asistencia");
            JOptionPane.showMessageDialog(ventana, "asistencia eliminada correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ventana, "Error al eliminar la tabla: " + e.getMessage());
        }
    }
    
    private static void eliminarAsistencia() {
        String nombre = JOptionPane.showInputDialog("Nombre del docente a eliminar:");
        if (nombre != null) {
            try {
                PreparedStatement stmt = conexion.prepareStatement("DELETE FROM asistencia WHERE nombre = ?");
                stmt.setString(1, nombre);
                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(ventana, "asistencia eliminado.");
                } else {
                    JOptionPane.showMessageDialog(ventana, "No se encontró el registro.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(ventana, "Error: " + e.getMessage());
            }
        }
    }
    
    //metodo para actualizar registro si existe errores de duplicidad de nombre, etc.
    /*private static void actualizarRegistro() {
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
    }*/
    private static void actualizarRegistro() {
        JDialog dialogo = new JDialog(ventana, "Actualizar Registro de Docente", true);
        dialogo.setLayout(new GridLayout(11, 2, 5, 5));
        dialogo.setSize(400, 400);

        // Colores estilo neón oscuro (igual que insertarDatos)
        Color fondoOscuro = new Color(30, 0, 50);
        Color textoNeon = new Color(0, 255, 180);
        Color bordeNeon = new Color(0, 255, 150);
        Color botonFondo = new Color(40, 40, 60);
        Color botonTexto = new Color(0, 255, 200);

        JTextField campoCodigo = new JTextField();
        campoCodigo.setEditable(false);

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

        // Aplicar estilo a etiquetas y campos
        for (Component comp : dialogo.getContentPane().getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(textoNeon);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setBackground(new Color(40, 0, 60));
                field.setForeground(textoNeon);
                field.setCaretColor(Color.WHITE);
                field.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));

                field.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        field.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        field.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
                    }
                });
            }
        }

        btnActualizar.setBackground(botonFondo);
        btnActualizar.setForeground(botonTexto);

        // Runnable para escanear UID
        Runnable escanearUID = () -> {
            new Thread(() -> {
                SerialPort puerto = null;
                for (SerialPort sp : SerialPort.getCommPorts()) {
                    if (sp.getSystemPortName().equals("COM4")) {
                        puerto = sp;
                        break;
                    }
                }
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

        escanearUID.run();

        btnActualizar.addActionListener(e -> {
            String codigo = campoCodigo.getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, escanee primero la tarjeta RFID.");
                return;
            }

            try {
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
                dialogo.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error al actualizar el registro: " + ex.getMessage());
            }
        });

        dialogo.getContentPane().setBackground(fondoOscuro);
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
    
    //metodo de mostradatos() actualizado en estilo y carga de fotos de docente
    private static void mostrarDatos() {
        JDialog dialogo = new JDialog(ventana, "Datos registrados", true);
        dialogo.setSize(1060, 500);
        dialogo.setLocationRelativeTo(ventana);
        dialogo.getContentPane().setBackground(new Color(20, 20, 40)); // fondo oscuro neón

        DefaultTableModel modelo = new DefaultTableModel() {
            public Class<?> getColumnClass(int column) {
                if (column == 10) {
                    return ImageIcon.class; // Columna de imagen
                }
                return String.class;
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] columnas = {
            "Código", "Nombre", "Ap. Paterno", "Ap. Materno",
            "Entrada", "Salida", "Alumnado", "Materia", "Aula", "Turno", "Foto"
        };
        for (String col : columnas) {
            modelo.addColumn(col);
        }

        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM docentes")) {
            while (rs.next()) {
                Object[] fila = new Object[11];
                for (int i = 0; i < 10; i++) {
                    fila[i] = rs.getString(i + 1);
                }

                // Buscar imagen del docente
                String codigo = rs.getString(1);
                ImageIcon icono = null;
                String[] extensiones = {".jpg", ".jpeg", ".png"};
                for (String ext : extensiones) {
                    File foto = new File("src/fotos_docentes/" + codigo + ext);
                    if (foto.exists()) {
                        BufferedImage img = ImageIO.read(foto);
                        Image imgEscalada = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        icono = new ImageIcon(imgEscalada);
                        break;
                    }
                }
                fila[10] = icono; // Imagen en la última columna
                modelo.addRow(fila);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(55); // espacio para imagen
        tabla.setBackground(new Color(30, 30, 60));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(100, 0, 150)); // Bordes morados
        tabla.setSelectionBackground(new Color(60, 0, 100));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(30, 30, 60));
        dialogo.add(scroll);

        dialogo.setVisible(true);
    }
    
    // metodo para mostrar en consola cada escaneo de tarjeta rfid
    /*private static void iniciarControlRFID() {
        JDialog lectorDialog = new JDialog(ventana, "Escanear Tarjeta", true);
        lectorDialog.setSize(300, 150);
        lectorDialog.setLayout(new BorderLayout());

        JLabel mensaje = new JLabel("Por favor, escanee su tarjeta...", SwingConstants.CENTER);
        lectorDialog.add(mensaje, BorderLayout.CENTER);

        new Thread(() -> {
            SerialPort puerto = null;
            for (SerialPort sp : SerialPort.getCommPorts()) {
                if (sp.getSystemPortName().equals("COM4")) {
                    puerto = sp;
                    break;
                }
            }
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
    }*/
    
    private static volatile boolean lectorActivo = false;
    private static Thread hiloRFID = null;
    private static void iniciarControlRFID() {
        final SerialPort[] puerto = new SerialPort[1];
        if (lectorActivo) {
            JOptionPane.showMessageDialog(ventana, "El lector ya está en uso. Espera a que finalice.");
            return;
        }

        lectorActivo = true;

        JDialog lectorDialog = new JDialog(ventana, "Escanear Tarjeta", true);
        lectorDialog.setSize(350, 180);
        lectorDialog.setLayout(new BorderLayout());
        
        // Agrega esta parte después de setDefaultCloseOperation:
        lectorDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (puerto[0] != null && puerto[0].isOpen()) {
                    puerto[0].closePort();
                    System.out.println("Puerto cerrado al cerrar ventana.");
                }
            }
        });

        JLabel mensaje = new JLabel("Por favor, escanee su tarjeta...", SwingConstants.CENTER);
        lectorDialog.add(mensaje, BorderLayout.CENTER);

        JButton btnFinalizar = new JButton("Finalizar");
        lectorDialog.add(btnFinalizar, BorderLayout.SOUTH);

        AtomicBoolean continuarLectura = new AtomicBoolean(true);
        SerialPort[] puertoRef = new SerialPort[1]; // truco para usar dentro del listener

        btnFinalizar.addActionListener(e -> {
            continuarLectura.set(false);
            if (puertoRef[0] != null && puertoRef[0].isOpen()) {
                puertoRef[0].closePort();
            }
            lectorDialog.dispose();
            lectorActivo = false;
            System.out.println("Escaneo RFID finalizado manualmente.");
        });

        hiloRFID = new Thread(() -> {
            Scanner scanner = null;
            boolean puertoAbierto = false;

            try {
                for (SerialPort sp : SerialPort.getCommPorts()) {
                    if (sp.getSystemPortName().equals("COM4")) {
                        puerto[0] = sp;
                        break;
                    }
                }

                if (puerto[0] == null) {
                    JOptionPane.showMessageDialog(ventana, "No se encontró el puerto COM4.");
                    lectorActivo = false;
                    return;
                }

                puerto[0].setComPortParameters(9600, 8, 1, 0);
                puerto[0].setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

                if (!puerto[0].openPort()) {
                    JOptionPane.showMessageDialog(ventana, "No se pudo abrir el puerto COM4.");
                    lectorActivo = false;
                    return;
                }

                puertoRef[0] = puerto[0];
                puertoAbierto = true;
                scanner = new Scanner(puerto[0].getInputStream());
                HashSet<String> uidsLeidos = new HashSet<>();

                while (continuarLectura.get() && puerto[0].isOpen()) {
                    if (scanner.hasNextLine()) {
                        String uid = scanner.nextLine().trim();

                        if (!uidsLeidos.contains(uid)) {
                            System.out.println("UID detectado: " + uid);
                            registrarAsistencia(uid);
                            mostrarDatosDocente(uid);
                            uidsLeidos.add(uid);

                            new java.util.Timer().schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    uidsLeidos.remove(uid);
                                }
                            }, 2000);
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
                if (puerto[0] != null && puerto[0].isOpen()) {
                    puerto[0].closePort();
                }

                if (puertoAbierto) {
                    System.out.println("Escaneo RFID finalizado.");
                }

                lectorActivo = false;
            }
        });

        hiloRFID.start();

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
                modeloAsistencia.addRow(new String[]{nombre, fecha, hora});

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
    
    //PERFIL DEL DECENTE
    private static SerialPort puertoLogin;
    private static void mostrarVentanaLogin() {
        
        JDialog loginDialog = new JDialog(ventana, "PERFI DOCENTE", true);
        loginDialog.setSize(400, 300);
        loginDialog.setLayout(new BorderLayout());
        loginDialog.setLocationRelativeTo(ventana);
        loginDialog.getContentPane().setBackground(new Color(30, 0, 50));

        // Colores y estilo
        Color fondoOscuro = new Color(30, 0, 50);
        Color textoNeon = new Color(0, 255, 180);
        Color bordeNeon = new Color(0, 255, 150);
        Color fondoCampo = new Color(40, 0, 60);
        Color botonFondo = new Color(40, 40, 60);
        Color botonTexto = new Color(0, 255, 200);

        // === Logo superior ===
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        try {
            ImageIcon icono = new ImageIcon("src/imagenes/usb_logo.png");
            Image imgEscalada = icono.getImage().getScaledInstance(160, 130, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            lblLogo.setText("LOGO");
            lblLogo.setForeground(textoNeon);
        }

        // === Campos RFID y Nombre ===
        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentro.setBackground(fondoOscuro);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JLabel lblRFID = new JLabel("Código RFID:");
        JLabel lblNombre = new JLabel("Nombre:");

        JTextField txtRFID = new JTextField();
        txtRFID.setEditable(false);
        JTextField txtNombre = new JTextField();
        txtNombre.setEditable(false);

        for (JLabel label : new JLabel[]{lblRFID, lblNombre}) {
            label.setForeground(textoNeon);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        for (JTextField campo : new JTextField[]{txtRFID, txtNombre}) {
            campo.setBackground(fondoCampo);
            campo.setForeground(textoNeon);
            campo.setCaretColor(Color.WHITE);
            campo.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
            campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        panelCentro.add(lblRFID);
        panelCentro.add(txtRFID);
        panelCentro.add(lblNombre);
        panelCentro.add(txtNombre);

        // === Botón Iniciar Sesión ===
        JButton btnIniciarSesion = new JButton("Iniciar Perfil");
        btnIniciarSesion.setEnabled(false);
        btnIniciarSesion.setBackground(botonFondo);
        btnIniciarSesion.setForeground(botonTexto);
        btnIniciarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(fondoOscuro);
        panelInferior.add(btnIniciarSesion);

        // === Escaneo RFID ===
        new Thread(() -> {
            puertoLogin = null;
            for (SerialPort sp : SerialPort.getCommPorts()) {
                if (sp.getSystemPortName().equals("COM4")) {
                    puertoLogin = sp;
                    break;
                }
            }
            if (puertoLogin == null) {
                return;
            }

            puertoLogin.setComPortParameters(9600, 8, 1, 0);
            puertoLogin.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            if (puertoLogin.openPort()) {
                try (Scanner scanner = new Scanner(puertoLogin.getInputStream())) {
                    while (loginDialog.isVisible()) {
                        if (scanner.hasNextLine()) {
                            String codigo = scanner.nextLine().trim();
                            SwingUtilities.invokeLater(() -> {
                                txtRFID.setText(codigo);
                                mostrarNombreDocente(codigo, txtNombre, btnIniciarSesion);
                            });
                            break;
                        }
                    }
                }
                puertoLogin.closePort();
            }
        }).start();

        // === Acción iniciar sesión ===
        btnIniciarSesion.addActionListener(e -> {
            loginDialog.dispose();
            mostrarPerfilDocente(txtRFID.getText());
        });

        // === Cerrar COM4 al cerrar ventana ===
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                for (SerialPort sp : SerialPort.getCommPorts()) {
                    if (sp.getSystemPortName().equals("COM4") && sp.isOpen()) {
                        sp.closePort();
                        System.out.println("Puerto COM4 cerrado al cerrar Login.");
                    }
                    if (puertoLogin != null && puertoLogin.isOpen()) {
                        puertoLogin.closePort();
                        System.out.println("Puerto COM4 cerrado al cerrar Login.");
                    }
                }
            }
        });
        

        loginDialog.add(lblLogo, BorderLayout.NORTH);
        loginDialog.add(panelCentro, BorderLayout.CENTER);
        loginDialog.add(panelInferior, BorderLayout.SOUTH);
        loginDialog.setVisible(true);
    }
    
    
    private static void mostrarNombreDocente(String uid, JTextField txtNombre, JButton btnIniciar) {
        try {
            PreparedStatement stmt = conexion.prepareStatement("SELECT nombre FROM docentes WHERE cod_docente = ?");
            stmt.setString(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre"));
                btnIniciar.setEnabled(true);
            } else {
                txtNombre.setText("No registrado");
                btnIniciar.setEnabled(false);
            }
            stmt.close();
        } catch (SQLException e) {
            txtNombre.setText("Error");
        }
    }
    
    private static void mostrarPerfilDocente(String codigo) {
        try {
            PreparedStatement stmt = conexion.prepareStatement("SELECT * FROM docentes WHERE cod_docente = ?");
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombreCompleto = rs.getString("nombre") + " "
                        + rs.getString("apellido_paterno") + " "
                        + rs.getString("apellido_materno");
                String materia = rs.getString("materia");
                String aula = rs.getString("aula");
                String turno = rs.getString("turno");
                String entrada = rs.getString("hora_entrada");
                String salida = rs.getString("hora_salida");

                Color fondoOscuro = new Color(30, 0, 50);
                Color textoNeon = new Color(0, 255, 180);
                Color bordeNeon = new Color(0, 255, 150);

                // === Buscar imagen del docente ===
                ImageIcon foto = null;
                for (String ext : new String[]{".jpg", ".png", ".jpeg"}) {
                    File img = new File("src/fotos_docentes", codigo + ext);
                    if (img.exists()) {
                        foto = new ImageIcon(new ImageIcon(img.getAbsolutePath())
                                .getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
                        break;
                    }

                }
                

                // === Ventana principal ===
                JDialog perfil = new JDialog(ventana, "Perfil del Docente", true);
                perfil.setSize(600, 600);
                perfil.setLayout(new BorderLayout());
                perfil.setLocationRelativeTo(ventana);
                perfil.getContentPane().setBackground(fondoOscuro);

                // === Panel top con botón cerrar sesión ===
                JPanel panelTop = new JPanel(new BorderLayout());
                panelTop.setBackground(fondoOscuro);

                JButton btnCerrarSesion = new JButton("Cerrar Sesión");
                btnCerrarSesion.setBackground(new Color(40, 40, 60));
                btnCerrarSesion.setForeground(new Color(0, 255, 200));
                btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 13));
                btnCerrarSesion.setFocusPainted(false);
                btnCerrarSesion.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
                AtomicBoolean continuarLectura = new AtomicBoolean(true);
                SerialPort[] puertoRef = new SerialPort[1]; // truco para usar dentro del listener
                btnCerrarSesion.addActionListener(e -> {
                    continuarLectura.set(false);
                    if (puertoRef[0] != null && puertoRef[0].isOpen()) {
                        puertoRef[0].closePort();
                    }
                    perfil.dispose();
                    lectorActivo = false;
                    System.out.println("Escaneo RFID finalizado manualmente.");
                    mostrarVentanaLogin();
                });

                panelTop.add(btnCerrarSesion, BorderLayout.EAST);
                perfil.add(panelTop, BorderLayout.PAGE_START);

                // === Foto docente ===
                JLabel lblFoto = new JLabel(foto);
                lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
                lblFoto.setBorder(BorderFactory.createLineBorder(bordeNeon, 2));
                lblFoto.setOpaque(true);
                lblFoto.setBackground(new Color(10, 10, 20));
                perfil.add(lblFoto, BorderLayout.NORTH);

                // === Datos del docente ===
                JTextArea areaDatos = new JTextArea();
                areaDatos.setEditable(false);
                areaDatos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                areaDatos.setBackground(new Color(40, 0, 60));
                areaDatos.setForeground(textoNeon);
                areaDatos.setBorder(BorderFactory.createLineBorder(bordeNeon, 1));
                areaDatos.setText(
                        "Nombre: " + nombreCompleto + "\n"
                        + "Materia: " + materia + "\n"
                        + "Aula: " + aula + "\n"
                        + "Turno: " + turno + "\n"
                        + "Hora Entrada: " + entrada + "\n"
                        + "Hora Salida: " + salida
                );

                // === Panel central con datos y tabla ===
                JPanel panelCentro = new JPanel(new BorderLayout());
                panelCentro.setBackground(fondoOscuro);
                panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                panelCentro.add(areaDatos, BorderLayout.NORTH);

                // === Tabla de alumnos ===
                DefaultTableModel modelo = new DefaultTableModel();
                modelo.addColumn("Nro");
                modelo.addColumn("Nombre");
                modelo.addColumn("Apellido Paterno");
                modelo.addColumn("Apellido Materno");

                PreparedStatement stmtAlumnos = conexion.prepareStatement(
                        "SELECT nombre, apellido_paterno, apellido_materno FROM alumnos WHERE cod_docente = ?"
                );
                stmtAlumnos.setString(1, codigo);
                ResultSet rsAlumnos = stmtAlumnos.executeQuery();

                int nro = 1;
                while (rsAlumnos.next()) {
                    modelo.addRow(new Object[]{
                        nro++,
                        rsAlumnos.getString("nombre"),
                        rsAlumnos.getString("apellido_paterno"),
                        rsAlumnos.getString("apellido_materno")
                    });
                }

                JTable tabla = new JTable(modelo);
                tabla.setBackground(new Color(40, 0, 60));
                tabla.setForeground(textoNeon);
                tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tabla.setRowHeight(28);
                tabla.setGridColor(bordeNeon);

                JScrollPane scrollTabla = new JScrollPane(tabla);
                scrollTabla.getViewport().setBackground(new Color(20, 0, 30));
                scrollTabla.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(bordeNeon, 1),
                        "Lista de Alumnos",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        textoNeon
                ));

                panelCentro.add(scrollTabla, BorderLayout.CENTER);
                perfil.add(panelCentro, BorderLayout.CENTER);

                perfil.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(ventana, "No se encontró ningún docente con este UID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ventana, "Error al mostrar perfil: " + e.getMessage());
        }
    }
}

