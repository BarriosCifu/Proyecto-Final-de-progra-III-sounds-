/**
 *
 * @author barri
 */
package com.mycompany.sounds;
import java.util.List;

public class Sounds {

    public static void main(String[] args) {
       System.out.println("Iniciando motor de Sounds...");

        // 1. Instanciar nuestras clases
        LectorArchivos lector = new LectorArchivos();
        ListaSimple bibliotecaPrincipal = new ListaSimple();
        
        // 2. Definir la ruta de tu música (¡Cambia esto por tu ruta real!)
        // Ejemplo en Windows: "C:\\Users\\TuUsuario\\Music"
        String rutaMusica = "D:\\musica prueba"; 
        
        System.out.println("Leyendo archivos desde: " + rutaMusica);
        lector.leerCarpetaRecursivamente(rutaMusica);
        
        // 3. Pasar las canciones leídas a nuestra Lista Simple
        List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
        for (Cancion c : cancionesLeidas) {
            bibliotecaPrincipal.insertar(c);
        }
        
        // 4. Mostrar el resultado en consola
        bibliotecaPrincipal.mostrarBiblioteca();
        
        // 5. Prueba opcional de audio (Si encontró canciones, reproduce la primera)
        if (bibliotecaPrincipal.getTamaño() > 0) {
            Cancion primera = bibliotecaPrincipal.buscarPorNombre(cancionesLeidas.get(0).getNombre());
            if (primera != null) {
                System.out.println("\n▶ Reproduciendo para prueba: " + primera.getNombre());
                Reproductor reproductor = new Reproductor();
                reproductor.reproducir(primera.getRuta());
                
                // Dejamos que suene 10 segundos y luego el programa termina (solo para la prueba)
                try {
                    Thread.sleep(10000); 
                    reproductor.detener();
                    System.out.println("Prueba de audio finalizada.");
                } catch (InterruptedException e) {
                }
            }
        } else {
            System.out.println("No se encontraron canciones MP3 en la ruta especificada.");
        }
    }
    }
