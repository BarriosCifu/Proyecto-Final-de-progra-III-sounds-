package com.mycompany.sounds;

/**
 *
 * @author barri
 */
public class Cola {
    private NodoLista frente;
    private NodoLista fin;

    public Cola() {
        this.frente = null;
        this.fin = null;
    }

    // Agregar una canción a la cola de reproducción (Enqueue)
    public void encolar(Cancion cancion) {
        NodoLista nuevo = new NodoLista(cancion);
        
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
    }

    // Sacar la siguiente canción a reproducir (Dequeue)
    public Cancion desencolar() {
        if (frente == null) {
            return null; // No hay canciones en espera
        }
        Cancion extraida = frente.getCancion();
        frente = frente.getSiguiente();
        
        if (frente == null) {
            fin = null; // Si sacamos la única canción, la cola queda totalmente vacía
        }
        
        return extraida;
    }
}
