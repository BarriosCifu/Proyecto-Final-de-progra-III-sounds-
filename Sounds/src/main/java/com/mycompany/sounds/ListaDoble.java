package com.mycompany.sounds;
public class ListaDoble {
    private NodoLista cabeza;
    private NodoLista fin;
    private NodoLista actual; 

    public ListaDoble() {
        this.cabeza = null;
        this.fin = null;
        this.actual = null;
    }
    public void insertar(Cancion cancion) {
        NodoLista nuevo = new NodoLista(cancion);

        if (cabeza == null) {
            cabeza = nuevo;
            fin = nuevo;
            actual = cabeza; 
        } else {
            fin.setSiguiente(nuevo);
            nuevo.setAnterior(fin);
            fin = nuevo;
        }
    }
    public Cancion irSiguiente() {
        if (actual != null && actual.getSiguiente() != null) {
            actual = actual.getSiguiente();
            return actual.getCancion();
        }
        System.out.println("Ya estás en la última canción de la lista.");
        return null; 
    }
    public Cancion irAnterior() {
        if (actual != null && actual.getAnterior() != null) {
            actual = actual.getAnterior();
            return actual.getCancion();
        }
        System.out.println("Ya estás en la primera canción, no hay anterior.");
        return null;
    }
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
