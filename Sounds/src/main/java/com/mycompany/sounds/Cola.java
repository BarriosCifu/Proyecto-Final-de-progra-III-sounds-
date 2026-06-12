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
    public Cancion desencolar() {
        if (frente == null) {
            return null; // No hay canciones en espera
        }
        Cancion extraida = frente.getCancion();
        frente = frente.getSiguiente();
        
        if (frente == null) {
            fin = null; // Si sacamos la unica cancion, la cola queda totalmente vacía
        }
             return extraida;
    }
}
