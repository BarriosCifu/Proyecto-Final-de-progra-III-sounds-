
package com.mycompany.sounds;
import java.util.HashMap;
import java.util.HashSet;
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
    public static void buscarDuplicados(ListaSimple biblioteca) {
        System.out.println("\n--- REPORTE DE DUPLICADOS ---");
        NodoLista actual = biblioteca.getCabeza();
        int contadorDuplicados = 0;
        long tamanoDuplicadosBytes = 0;

        // HashMap para registrar canciones ya vistas. Clave: "nombre_tamaño"
        HashMap<String, Cancion> cancionesVistas = new HashMap<>();
        // HashSet para evitar imprimir el mismo nombre varias veces si el archivo está triplicado
        HashSet<String> duplicadosReportados = new HashSet<>();

        while (actual != null) {
            Cancion c = actual.getCancion();
            // Creamos una clave única usando el nombre y el tamaño en bytes
            String claveUnica = c.getNombre().toLowerCase() + "_" + c.getTamano();

            if (cancionesVistas.containsKey(claveUnica)) {
                // Si ya vimos esta clave, es un duplicado
                if (!duplicadosReportados.contains(claveUnica)) {
                    System.out.println("- Duplicado detectado: " + c.getNombre() + 
                                       " | Tamaño: " + (c.getTamano() / 1024 / 1024) + " MB");
                    duplicadosReportados.add(claveUnica);
                }
                contadorDuplicados++;
                tamanoDuplicadosBytes += c.getTamano();
            } else {
                // Si no la hemos visto, la guardamos en el mapa
                cancionesVistas.put(claveUnica, c);
            }
            
            actual = actual.getSiguiente();
        }

        System.out.println("\nTotal de archivos duplicados: " + contadorDuplicados);
        double mbDuplicados = Math.round((tamanoDuplicadosBytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
        System.out.println("Tamaño total desperdiciado: " + mbDuplicados + " MB");
        System.out.println("-------------------------------");
    }
}
