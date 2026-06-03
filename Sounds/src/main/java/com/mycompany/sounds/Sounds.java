/**
 *
 * @author barri
 */
package com.mycompany.sounds;
import java.util.List;

public class Sounds {

    public static void main(String[] args) {
      System.out.println("Iniciando prueba del Árbol Binario de Búsqueda...");

        // 1. Instanciar el lector y el árbol
        LectorArchivos lector = new LectorArchivos();
        ArbolBinarioBusqueda arbolCanciones = new ArbolBinarioBusqueda();
        
        // 2. Coloca aquí tu ruta de prueba
        String rutaMusica = "D:\\musica prueba"; 
        
        System.out.println("Leyendo archivos desde: " + rutaMusica);
        lector.leerCarpetaRecursivamente(rutaMusica);
        
        // 3. Insertar las canciones leídas en el ABB usando los nodos
        List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
        for (Cancion c : cancionesLeidas) {
            arbolCanciones.insertar(c);
        }
        
        // 4. Mostrar los recorridos para verificar que los nodos se conectaron bien
        if (!cancionesLeidas.isEmpty()) {
            System.out.println("\nSe insertaron " + cancionesLeidas.size() + " canciones en el árbol.");
            
            // El InOrden debe mostrar las canciones ordenadas alfabéticamente
            arbolCanciones.mostrarInOrden();
            
            // Los otros recorridos muestran la estructura interna
            arbolCanciones.mostrarPreOrden();
            arbolCanciones.mostrarPostOrden();
            
        } else {
            System.out.println("No se encontraron canciones para insertar en el árbol.");
        }
    }
    }
