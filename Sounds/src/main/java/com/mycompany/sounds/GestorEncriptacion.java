package com.mycompany.sounds;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
/**
 *
 * @author barri
 */
public class GestorEncriptacion {
   private static final int LLAVE = 5; 
   private static String cifrar(String texto) {
   StringBuilder resultado = new StringBuilder();
   for (char c : texto.toCharArray()) {
    resultado.append((char) (c + LLAVE));
  }
   return resultado.toString();
    }
    public static String descifrar(String textoCifrado) {
        StringBuilder resultado = new StringBuilder();
        for (char c : textoCifrado.toCharArray()) {
      resultado.append((char) (c - LLAVE));
        }
     return resultado.toString();
    }
    public static void exportarPlaylistEncriptada(NodoArbol raiz, String rutaArchivoDestino, String tipoRecorrido) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivoDestino))) {
              if (tipoRecorrido.equalsIgnoreCase("InOrden")) {
             escribirInOrden(raiz, writer);
           } else if (tipoRecorrido.equalsIgnoreCase("PreOrden")) {
                escribirPreOrden(raiz, writer);
          } else if (tipoRecorrido.equalsIgnoreCase("PostOrden")) {
            escribirPostOrden(raiz, writer);
         }
                System.out.println("Playlist exportada y encriptada exitosamente en: " + rutaArchivoDestino);
           } catch (Exception e) {
        System.out.println("Error al exportar playlist: " + e.getMessage());
        }
    }
    private static void escribirInOrden(NodoArbol nodo, PrintWriter writer) {
     if (nodo != null) {
          escribirInOrden(nodo.getIzquierdo(), writer);
           writer.println(cifrar(nodo.getCancion().getRuta()));
           escribirInOrden(nodo.getDerecho(), writer);
        }
    }
    private static void escribirPreOrden(NodoArbol nodo, PrintWriter writer) {
        if (nodo != null) {
          writer.println(cifrar(nodo.getCancion().getRuta()));
           escribirPreOrden(nodo.getIzquierdo(), writer);
           escribirPreOrden(nodo.getDerecho(), writer);
 }
    }
    private static void escribirPostOrden(NodoArbol nodo, PrintWriter writer) {
     if (nodo != null) {
          escribirPostOrden(nodo.getIzquierdo(), writer);
           escribirPostOrden(nodo.getDerecho(), writer);
           writer.println(cifrar(nodo.getCancion().getRuta()));
        }
    }
    public static void recuperarPlaylist(String rutaArchivoCifrado) {
      System.out.println("\n--- RECUPERANDO PLAYLIST ---");
     try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivoCifrado))) {
      String lineaCifrada;
        while ((lineaCifrada = reader.readLine()) != null) {
          String rutaDescifrada = descifrar(lineaCifrada);
          System.out.println("Ruta recuperada: " + rutaDescifrada);
            }
    } catch (Exception e) {
      System.out.println("Error al recuperar playlist: " + e.getMessage());
   }
 }  
}
