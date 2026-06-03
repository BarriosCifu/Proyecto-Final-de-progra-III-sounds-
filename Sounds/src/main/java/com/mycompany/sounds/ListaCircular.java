/**
 *
 * @author barri
 */
package com.mycompany.sounds;

public class ListaCircular {
    private NodoLista cabeza;
    private NodoLista actual;

    public ListaCircular() {
        this.cabeza = null;
        this.actual = null;
    }

    public void insertar(Cancion cancion) {
        NodoLista nuevo = new NodoLista(cancion);
        
        if (cabeza == null) {
            cabeza = nuevo;
            // Se apunta a sí mismo para hacer el círculo
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cabeza);
            actual = cabeza;
        } else {
            // El anterior a la cabeza siempre es el último nodo en una circular
            NodoLista ultimo = cabeza.getAnterior(); 
            
            nuevo.setSiguiente(cabeza);
            nuevo.setAnterior(ultimo);
            
            cabeza.setAnterior(nuevo);
            ultimo.setSiguiente(nuevo);
        }
    }

    // Al avanzar, si llegamos al final, automáticamente pasará al primero
    public Cancion irSiguiente() {
        if (actual != null) {
            actual = actual.getSiguiente();
            return actual.getCancion();
        }
        return null;
    }

    // Al retroceder desde el primero, automáticamente pasará al último
    public Cancion irAnterior() {
        if (actual != null) {
            actual = actual.getAnterior();
            return actual.getCancion();
        }
        return null;
    }
}
