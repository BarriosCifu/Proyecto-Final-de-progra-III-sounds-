/**
 *
 * @author barri
 */
package com.mycompany.sounds;
import java.util.List;

public class Sounds {
public static void main(String[] args) {
        System.out.println("Iniciando motor de integración de Sounds...\n");

        // 1. Instanciar todas las herramientas
        LectorArchivos lector = new LectorArchivos();
        ListaSimple bibliotecaLista = new ListaSimple();
        ArbolBinarioBusqueda bibliotecaArbol = new ArbolBinarioBusqueda();
        
        // 2. Definir la ruta de tu música (¡Cambia esto por tu ruta real!)
        String rutaMusica = "D:\\musica prueba"; 
        
        System.out.println("Leyendo archivos desde: " + rutaMusica);
        lector.leerCarpetaRecursivamente(rutaMusica);
        List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
        
        // 3. Poblar las estructuras
        for (Cancion c : cancionesLeidas) {
            bibliotecaLista.insertar(c);    // Para estadísticas
            bibliotecaArbol.insertar(c);    // Para encriptación en InOrden
        }
        
        if (bibliotecaLista.getTamaño() > 0) {
            // --- PRUEBA DE ESTADÍSTICAS ---
            GeneradorEstadisticas.mostrarEstadisticas(bibliotecaLista);
            GeneradorEstadisticas.buscarDuplicados(bibliotecaLista);
            
            // --- PRUEBA DE ENCRIPTACIÓN ---
            String archivoExportacion = "playlist_encriptada.txt";
            System.out.println("\n--- INICIANDO EXPORTACIÓN ---");
            
            // Exportar usando el recorrido InOrden
            GestorEncriptacion.exportarPlaylistEncriptada(bibliotecaArbol.getRaiz(), archivoExportacion, "InOrden");
            
            // Probar la desencriptación inmediatamente después
            GestorEncriptacion.recuperarPlaylist(archivoExportacion);

            // --- PRUEBA DE REPRODUCCIÓN ---
            System.out.println("\n--- INICIANDO REPRODUCCIÓN DE AUDIO ---");
            // Tomamos la primera canción de la lista
            Cancion primera = bibliotecaLista.getCabeza().getCancion();
            System.out.println("▶ Reproduciendo pista: " + primera.getNombre() + " - " + primera.getArtista());
            
            Reproductor reproductor = new Reproductor();
            reproductor.reproducir(primera.getRuta());
            
            // Dejamos que suene 15 segundos y luego detenemos el hilo
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
