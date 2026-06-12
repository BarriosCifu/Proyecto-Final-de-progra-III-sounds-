
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
           System.out.println("-------------------------------------");
    }

    private static double calcularTamanoTotal(ListaSimple biblioteca) {
        long tamanoTotalBytes = 0;
          NodoLista actual = biblioteca.getCabeza(); 
        
        while (actual != null) {
            tamanoTotalBytes += actual.getCancion().getTamano();
            actual = actual.getSiguiente();
        }
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
        HashMap<String, Cancion> cancionesVistas = new HashMap<>();
          HashSet<String> duplicadosReportados = new HashSet<>();
        while (actual != null) {
            Cancion c = actual.getCancion();
                     String claveUnica = c.getNombre().toLowerCase() + "_" + c.getTamano();
            if (cancionesVistas.containsKey(claveUnica)) {
                 if (!duplicadosReportados.contains(claveUnica)) {
                    System.out.println("- Duplicado detectado: " + c.getNombre() + 
                                       " | Tamano: " + (c.getTamano() / 1024 / 1024) + " MB");
                    duplicadosReportados.add(claveUnica);
                }
                contadorDuplicados++;
                tamanoDuplicadosBytes += c.getTamano();
            } else {
                    cancionesVistas.put(claveUnica, c);
            }
                        actual = actual.getSiguiente();
        }

        System.out.println("\nTotal de archivos duplicados: " + contadorDuplicados);
        double mbDuplicados = Math.round((tamanoDuplicadosBytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
        System.out.println("Tamano total desperdiciado: " + mbDuplicados + " MB");
        System.out.println("-------------------------------");
    }
}
