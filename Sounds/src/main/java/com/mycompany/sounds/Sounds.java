/**
 *
 * @author barri
 */
package com.mycompany.sounds;
import java.util.List;

public class Sounds {
public static void main(String[] args) {
        System.out.println("Iniciando motor de integración de Sounds...\n");
        LectorArchivos lector = new LectorArchivos();
        ListaSimple bibliotecaLista = new ListaSimple();
        ArbolBinarioBusqueda bibliotecaArbol = new ArbolBinarioBusqueda();
         String rutaMusica = "D:\\musica prueba"; 
                System.out.println("Leyendo archivos desde: " + rutaMusica);
        lector.leerCarpetaRecursivamente(rutaMusica);
        List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
                for (Cancion c : cancionesLeidas) {
            bibliotecaLista.insertar(c);    
            bibliotecaArbol.insertar(c);  
        }
        
        if (bibliotecaLista.getTamaño() > 0) {
      
            GeneradorEstadisticas.mostrarEstadisticas(bibliotecaLista);
            GeneradorEstadisticas.buscarDuplicados(bibliotecaLista);
                        String archivoExportacion = "playlist_encriptada.txt";
            System.out.println("\n--- INICIANDO EXPORTACIÓN ---");
                     GestorEncriptacion.exportarPlaylistEncriptada(bibliotecaArbol.getRaiz(), archivoExportacion, "InOrden");
                        GestorEncriptacion.recuperarPlaylist(archivoExportacion);
            System.out.println("\n--- INICIANDO REPRODUCCIÓN DE AUDIO ---");
            // Tomamos la primera canción de la lista
            Cancion primera = bibliotecaLista.getCabeza().getCancion();
            System.out.println("▶ Reproduciendo pista: " + primera.getNombre() + " - " + primera.getArtista());
                        Reproductor reproductor = new Reproductor();
            reproductor.reproducir(primera.getRuta());
                              try {
                Thread.sleep(15000); 
                reproductor.detener();
                System.out.println("Prueba de audio finalizada exitosamente. El sistema es estable.");
            } catch (InterruptedException e) {
            }
                    } else {
            System.out.println("No se encontraron canciones MP3 en la ruta especificada.");
        }
    }
    }
