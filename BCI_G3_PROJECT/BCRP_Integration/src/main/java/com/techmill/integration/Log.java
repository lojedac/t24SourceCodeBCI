package com.techmill.integration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Log {
    InputStream input = Log.class.getClassLoader().getResourceAsStream("config/bcrpConfig.properties"); // Read
    private BufferedWriter buffered;
    private String ruta;
    private String nameClass;

    Properties prop = new Properties();

    public Log(String nameClass) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatoFecha = sdf.format(new Date());
        if (input == null) {
            this.ruta = "/srv/Temenos/log/BCRP_integration.log";
        } else {
            try {
                prop.load(input);
                this.ruta = prop.getProperty("Path_log") + "_" + formatoFecha + ".log";
            } catch (IOException e) {
                this.ruta = "/srv/Temenos/log/BCRP_integration.log";
            }
        }
        this.nameClass = nameClass;
        this.open(true);
    }

    public Log(String nameClass, String ruta) throws IOException {
        this.ruta = ruta;
        this.open(true);
        this.nameClass = nameClass;
    }

    public Log(String nameClass, String ruta, boolean reset) throws IOException {
        this.ruta = ruta;
        this.open(!reset);
        this.nameClass = nameClass;
    }

    private void open(boolean append) {
        try {
            this.buffered = new BufferedWriter(new FileWriter(this.ruta, append));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addInfo(String line) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatoFecha = sdf.format(new Date());
        this.open(true);
        String cadena = formatoFecha + " - INFO  - Interfaz: " + this.nameClass + " - " + line + "\n";
        System.out.println(cadena);
        this.buffered.write(cadena);
        this.close();
    }

    public void addError(String line) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatoFecha = sdf.format(new Date());
        this.open(true);
        String cadena = formatoFecha + " - ERROR - Interfaz: " + this.nameClass + " - " + line + "\n";
        System.out.println(cadena);
        this.buffered.write(cadena);
        this.close();
    }

    private void close() {
        try {
            this.buffered.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
