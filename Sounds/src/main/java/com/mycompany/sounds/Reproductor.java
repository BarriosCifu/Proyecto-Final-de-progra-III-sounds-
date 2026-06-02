
package com.mycompany.sounds;
import java.io.FileInputStream;
import java.io.IOException;
import javazoom.jl.player.Player;
public class Reproductor {
    private Player player;
    private Thread hiloReproduccion;
    private String rutaActual;
    
    // Variables para manejar la pausa (JLayer básico requiere guardar el estado)
    private FileInputStream fis;
    private long pausaPunto; 
    private boolean enPausa;

    public Reproductor() {
        this.enPausa = false;
    }

    public void reproducir(String rutaArchivo) {
        detener(); // Limpiamos cualquier reproducción anterior
        this.rutaActual = rutaArchivo;
        this.enPausa = false;
        iniciarHilo(0);
    }

    private void iniciarHilo(long saltarBytes) {
        hiloReproduccion = new Thread(() -> {
            try {
                fis = new FileInputStream(rutaActual);
                if (saltarBytes > 0) {
                    fis.skip(saltarBytes); // Saltamos a la posición donde se pausó
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
                pausaPunto = fis.available(); // Guardamos cuántos bytes faltan por leer
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
                long totalBytes = fisTemp.available();
                fisTemp.close();
                
                // Calculamos cuántos bytes saltar para reanudar donde nos quedamos
                long bytesASaltar = totalBytes - pausaPunto; 
                iniciarHilo(bytesASaltar);
            } catch (IOException e) {
                System.out.println("Error al reanudar: " + e.getMessage());
            }
        }
    }
}
