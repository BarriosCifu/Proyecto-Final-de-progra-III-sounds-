package com.mycompany.sounds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javazoom.jl.player.Player;

public class Reproductor {
    private Player player;
    private Thread hiloReproduccion;
    private String rutaActual;
    
    private FileInputStream fis;
    private long pausaPunto; 
    private boolean enPausa;
    
    private long bytesTotales; 

    public Reproductor() {
        this.enPausa = false;
    }

    public void reproducir(String rutaArchivo) {
        detener(); 
        this.rutaActual = rutaArchivo;
        this.enPausa = false;
        
        try {
            File archivo = new File(rutaArchivo);
            this.bytesTotales = archivo.length();
        } catch (Exception e) {
            this.bytesTotales = 0;
        }
        
        iniciarHilo(0);
    }

    private void iniciarHilo(long saltarBytes) {
        hiloReproduccion = new Thread(() -> {
            try {
                fis = new FileInputStream(rutaActual);
                if (saltarBytes > 0) {
                    fis.skip(saltarBytes); 
                }
                player = new Player(fis);
                player.play();
            } catch (Exception e) {
                System.out.println("Error en la reproducción: " + e.getMessage());
            }
        });
        hiloReproduccion.start();
    }

    public void detener() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (hiloReproduccion != null) {
            hiloReproduccion.interrupt();
            hiloReproduccion = null;
        }
    }

    public void pausar() {
        if (player != null && !enPausa) {
            try {
                enPausa = true;
                pausaPunto = fis.available();
                player.close();
                if (hiloReproduccion != null) {
                    hiloReproduccion.interrupt();
                }
            } catch (IOException e) {
                System.out.println("Error al pausar: " + e.getMessage());
            }
        }
    }

    public void continuar() {
        if (enPausa && rutaActual != null) {
            enPausa = false;
            try {
                FileInputStream fisTemp = new FileInputStream(rutaActual);
                long totalBytesTemp = fisTemp.available();
                fisTemp.close();
                
                long bytesASaltar = totalBytesTemp - pausaPunto; 
                iniciarHilo(bytesASaltar);
            } catch (IOException e) {
                System.out.println("Error al reanudar: " + e.getMessage());
            }
        }
    }

    // --- EL TRUCO PARA ADELANTAR LA CANCIÓN ---
    public void saltarA(double porcentaje) {
        if (rutaActual != null && bytesTotales > 0) {
            detener(); // Apagamos el reproductor actual
            long bytesASaltar = (long) (bytesTotales * porcentaje); // Calculamos hasta qué byte saltar
            this.enPausa = false;
            iniciarHilo(bytesASaltar); // Lo encendemos de nuevo desde ese punto
        }
    }

    public double getProgreso() {
        if (fis != null && bytesTotales > 0 && !enPausa) {
            try {
                long bytesRestantes = fis.available();
                long bytesLeidos = bytesTotales - bytesRestantes;
                return (double) bytesLeidos / bytesTotales;
            } catch (IOException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    // --- FÓRMULA CORREGIDA PARA AUDIOS DE ALTA CALIDAD ---
    public int getDuracionEstimadaSegundos() {
        if (bytesTotales > 0) {
            // Un MP3 de 320 kbps consume aprox. 40,000 bytes por segundo
            return (int) (bytesTotales / 40000); 
        }
        return 0;
    }

    public boolean isReproduciendo() {
        return player != null && !enPausa;
    }
}