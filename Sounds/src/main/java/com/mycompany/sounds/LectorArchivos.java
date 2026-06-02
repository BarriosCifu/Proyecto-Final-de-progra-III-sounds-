package com.mycompany.sounds;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class LectorArchivos {

    // Lista temporal para almacenar las canciones antes de pasarlas a tus estructuras
    private List<Cancion> cancionesCargadas;

    public LectorArchivos() {
        this.cancionesCargadas = new ArrayList<>();
    }

    /**
     * Método recursivo para leer carpetas y subcarpetas.
     */
    public void leerCarpetaRecursivamente(String rutaDirectorio) {
        File directorio = new File(rutaDirectorio);

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        // Si es carpeta, se llama a sí mismo (recursividad)
                        leerCarpetaRecursivamente(archivo.getAbsolutePath());
                    } else if (archivo.getName().toLowerCase().endsWith(".mp3")) {
                        // Si es MP3, procesamos la canción
                        Cancion nuevaCancion = extraerMetadatos(archivo);
                        cancionesCargadas.add(nuevaCancion);
                    }
                }
            }
        } else {
            System.out.println("La ruta no existe o no es un directorio válido.");
        }
    }

    /**
     * Extrae información básica y lee los últimos 128 bytes (ID3v1) para los metadatos.
     */
    private Cancion extraerMetadatos(File archivo) {
        Cancion cancion = new Cancion();
        
        // Datos a nivel de sistema de archivos
        cancion.setNombre(archivo.getName().replace(".mp3", "")); // Nombre por defecto
        cancion.setRuta(archivo.getAbsolutePath());
        cancion.setTamano(archivo.length());
        
        // Intentar leer metadatos ID3v1 (últimos 128 bytes)
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            long longitud = raf.length();
            if (longitud > 128) {
                raf.seek(longitud - 128);
                byte[] etiquetaID3 = new byte[128];
                raf.read(etiquetaID3);

                String tag = new String(etiquetaID3, 0, 3);
                if (tag.equals("TAG")) {
                    // Si tiene la etiqueta, extraemos los datos limpiando espacios en blanco
                    String titulo = new String(etiquetaID3, 3, 30).trim();
                    String artista = new String(etiquetaID3, 33, 30).trim();
                    String album = new String(etiquetaID3, 63, 30).trim();
                    String anioStr = new String(etiquetaID3, 93, 4).trim();

                    if (!titulo.isEmpty()) cancion.setNombre(titulo);
                    if (!artista.isEmpty()) cancion.setArtista(artista);
                    if (!album.isEmpty()) cancion.setAlbum(album);
                    try {
                        if (!anioStr.isEmpty()) cancion.setAnio(Integer.parseInt(anioStr));
                    } catch (NumberFormatException e) {
                        cancion.setAnio(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo metadatos de: " + archivo.getName());
        }

        return cancion;
    }

    public List<Cancion> getCancionesCargadas() {
        return cancionesCargadas;
    }
}