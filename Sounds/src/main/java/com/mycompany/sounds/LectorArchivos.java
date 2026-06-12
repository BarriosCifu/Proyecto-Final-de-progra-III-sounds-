package com.mycompany.sounds;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LectorArchivos {
    private List<Cancion> cancionesCargadas;

    public LectorArchivos() {
        this.cancionesCargadas = new ArrayList<>();
    }
    // metodo recursivo para las carpetas y subcarpetas
    public void leerCarpetaRecursivamente(String rutaDirectorio) {
        File directorio = new File(rutaDirectorio);

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        leerCarpetaRecursivamente(archivo.getAbsolutePath());
                    } else if (archivo.getName().toLowerCase().endsWith(".mp3")) {
                        Cancion nuevaCancion = extraerMetadatos(archivo);
                        cancionesCargadas.add(nuevaCancion);
                    }
                }
            }
        } else {
            System.out.println("La ruta no existe o no es un directorio válido.");
        }
    }
    private Cancion extraerMetadatos(File archivo) {
        Cancion cancion = new Cancion();
            cancion.setNombre(archivo.getName().replace(".mp3", "")); 
        cancion.setRuta(archivo.getAbsolutePath());
        cancion.setTamano(archivo.length());
        cancion.setArtista("Artista Desconocido");
        cancion.setAlbum("Álbum Desconocido");
        cancion.setGenero("Desconocido");
        cancion.setAnio(0);
        try {
               Mp3File mp3File = new Mp3File(archivo.getAbsolutePath());
            cancion.setDuracion(mp3File.getLengthInSeconds());
            if (mp3File.hasId3v2Tag()) {
                ID3v2 tagv2 = mp3File.getId3v2Tag();
                                if (tagv2.getTitle() != null && !tagv2.getTitle().trim().isEmpty()) cancion.setNombre(tagv2.getTitle().trim());
                if (tagv2.getArtist() != null && !tagv2.getArtist().trim().isEmpty()) cancion.setArtista(tagv2.getArtist().trim());
                if (tagv2.getAlbum() != null && !tagv2.getAlbum().trim().isEmpty()) cancion.setAlbum(tagv2.getAlbum().trim());
                if (tagv2.getGenreDescription() != null && !tagv2.getGenreDescription().trim().isEmpty()) cancion.setGenero(tagv2.getGenreDescription().trim());
                                try {
                    if (tagv2.getYear() != null && !tagv2.getYear().trim().isEmpty()) {
                        cancion.setAnio(Integer.parseInt(tagv2.getYear().trim()));
                    }
                } catch (NumberFormatException e) {
                    cancion.setAnio(0);
                }
                        byte[] bytesImagen = tagv2.getAlbumImage();
                if (bytesImagen != null) {
                    cancion.setImagenCaratula(bytesImagen);
                }
                                 
                      } else if (mp3File.hasId3v1Tag()) {
                ID3v1 tagv1 = mp3File.getId3v1Tag();
                if (tagv1.getTitle() != null && !tagv1.getTitle().trim().isEmpty()) cancion.setNombre(tagv1.getTitle().trim());
                if (tagv1.getArtist() != null && !tagv1.getArtist().trim().isEmpty()) cancion.setArtista(tagv1.getArtist().trim());
                if (tagv1.getAlbum() != null && !tagv1.getAlbum().trim().isEmpty()) cancion.setAlbum(tagv1.getAlbum().trim());
                try {
                    if (tagv1.getYear() != null && !tagv1.getYear().trim().isEmpty()) {
                        cancion.setAnio(Integer.parseInt(tagv1.getYear().trim()));
                    }
                } catch (NumberFormatException e) {
                    cancion.setAnio(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo metadatos de " + archivo.getName() + ": " + e.getMessage());
        }
        return cancion;
    }
    public List<Cancion> getCancionesCargadas() {
        return cancionesCargadas;
    }
}
