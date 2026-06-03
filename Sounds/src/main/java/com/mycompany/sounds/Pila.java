/**
 *
 * @author barri
 */
package com.mycompany.sounds;


public class Pila {
  private NodoLista cima;

    public Pila() {
        this.cima = null;
    }

    // Agregar una canción al historial (Push)
    public void push(Cancion cancion) {
        NodoLista nuevo = new NodoLista(cancion);
        if (cima != null) {
            nuevo.setSiguiente(cima);
        }
        cima = nuevo;
    }

    // Sacar la última canción del historial (Pop)
    public Cancion pop() {
        if (cima == null) {
            return null; // El historial está vacío
        }
        Cancion extraida = cima.getCancion();
        cima = cima.getSiguiente();
        return extraida;
    }

    // Ver cuál fue la última canción sin sacarla del historial (Peek)
    public Cancion peek() {
        if (cima != null) {
            return cima.getCancion();
        }
        return null;
    }  
}
