package com.mycompany.sounds;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Reproductor {
    
    private MediaPlayer mediaPlayer;
    private String rutaActual;
    private boolean enPausa;
    private double volumenActual = 0.5; // El volumen arranca a la mitad (50%)

    public Reproductor() {
        this.enPausa = false;
    }

    public void reproducir(String rutaArchivo) {
        detener(); 
        this.rutaActual = rutaArchivo;
        this.enPausa = false;
        
        try {
            File archivo = new File(rutaArchivo);
            Media media = new Media(archivo.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            
            // Aplicamos automaticamente el volumen actual de la barra
            mediaPlayer.setVolume(volumenActual);
            
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error en la reproducción con JavaFX Media: " + e.getMessage());
        }
    }

    public void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); 
            mediaPlayer = null;
        }
    }

    public void pausar() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            enPausa = true;
        }
    }

    public void continuar() {
        if (mediaPlayer != null && enPausa) {
            mediaPlayer.play();
            enPausa = false;
        }
    }

    public void saltarA(double porcentaje) {
        if (mediaPlayer != null) {
            Duration total = mediaPlayer.getTotalDuration();
            if (total != Duration.UNKNOWN) {
          
                mediaPlayer.seek(total.multiply(porcentaje));
            }
        }
    }

    public double getProgreso() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != Duration.UNKNOWN) {
            return mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis();
        }
        return 0.0;
    }

    public int getDuracionEstimadaSegundos() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != Duration.UNKNOWN) {
            return (int) mediaPlayer.getTotalDuration().toSeconds();
        }
        return 0;
    }

    public boolean isReproduciendo() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    public void setVolumen(double nivelVolumen) {
        this.volumenActual = nivelVolumen; 
        if (mediaPlayer != null) {
            // El MediaPlayer de JavaFX acepta valores directamente de 0.0 a 1.0
            mediaPlayer.setVolume(nivelVolumen);
        }
    }
}
