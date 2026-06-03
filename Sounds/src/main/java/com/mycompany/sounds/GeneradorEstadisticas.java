
package com.mycompany.sounds;

/**
 *
 * @author barri
 */
public class GeneradorEstadisticas {
    public static void mostrarEstadisticas(ListaSimple biblioteca) {
        if (biblioteca.getTamaño() == 0) {
            System.out.println("La biblioteca está vacía. No hay estadísticas para mostrar.");
            return;
        }

        System.out.println("\n--- ESTADÍSTICAS DE LA BIBLIOTECA ---");
        System.out.println("Total de canciones: " + biblioteca.getTamaño());
        System.out.println("Tamaño total en disco: " + calcularTamanoTotal(biblioteca) + " MB");
        System.out.println("Promedio de duración: " + calcularPromedioDuracion(biblioteca) + " segundos");
        
        // Aquí puedes agregar más lógica para el "Artista más escuchado" o "Género más frecuente"
        // (Esto requeriría recorrer la lista y usar arreglos o contadores)
        
        System.out.println("-------------------------------------");
    }

    private static double calcularTamanoTotal(ListaSimple biblioteca) {
        long tamanoTotalBytes = 0;
        // Para recorrer la ListaSimple de inicio a fin
        // (Asumiendo que agregaste un método getCabeza() en ListaSimple)
        NodoLista actual = biblioteca.getCabeza(); 
        
        while (actual != null) {
            tamanoTotalBytes += actual.getCancion().getTamano();
            actual = actual.getSiguiente();
        }
        
        // Convertir de Bytes a Megabytes
        return Math.round((tamanoTotalBytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }

    private static long calcularPromedioDuracion(ListaSimple biblioteca) {
        long duracionTotal = 0;
        NodoLista actual = biblioteca.getCabeza();
        
        while (actual != null) {
            duracionTotal += actual.getCancion().getDuracion();
            actual = actual.getSiguiente();
        }
        
        return duracionTotal / biblioteca.getTamaño();
    }
}
