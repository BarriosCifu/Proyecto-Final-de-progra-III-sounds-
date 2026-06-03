
package com.mycompany.sounds;


public class ListaDoble {
    private NodoLista cabeza;
    private NodoLista fin;
    private NodoLista actual; // Puntero crucial para saber dónde estamos en la lista

    public ListaDoble() {
        this.cabeza = null;
        this.fin = null;
        this.actual = null;
    }

    // Insertar al final de la lista de reproducción
    public void insertar(Cancion cancion) {
        NodoLista nuevo = new NodoLista(cancion);

        if (cabeza == null) {
            cabeza = nuevo;
            fin = nuevo;
            actual = cabeza; // Por defecto, la primera canción insertada es la actual
        } else {
            fin.setSiguiente(nuevo);
            nuevo.setAnterior(fin);
            fin = nuevo;
        }
    }

    // Avanzar a la siguiente canción
    public Cancion irSiguiente() {
        if (actual != null && actual.getSiguiente() != null) {
            actual = actual.getSiguiente();
            return actual.getCancion();
        }
        System.out.println("Ya estás en la última canción de la lista.");
        return null; 
    }

    // Retroceder a la canción anterior
    public Cancion irAnterior() {
        if (actual != null && actual.getAnterior() != null) {
            actual = actual.getAnterior();
            return actual.getCancion();
        }
        System.out.println("Ya estás en la primera canción, no hay anterior.");
        return null;
    }

    // Obtener la canción que está seleccionada actualmente sin cambiar de posición
    public Cancion obtenerActual() {
        if (actual != null) {
            return actual.getCancion();
        }
        return null;
    }
    
    public void reiniciarPuntero() {
        actual = cabeza;
    }
}
