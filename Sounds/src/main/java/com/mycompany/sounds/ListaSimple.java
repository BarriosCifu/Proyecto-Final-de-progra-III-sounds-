
package com.mycompany.sounds;
public class ListaSimple {
    private NodoLista cabeza;
    private int tamaño;

    public ListaSimple() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    // Insertar una canción al final de la lista
    public void insertar(Cancion cancion) {
        NodoLista nuevoNodo = new NodoLista(cancion);

        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoLista actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamaño++;
    }

    // Buscar una canción por nombre (útil antes de tener los árboles)
    public Cancion buscarPorNombre(String nombre) {
        NodoLista actual = cabeza;
        while (actual != null) {
            if (actual.getCancion().getNombre().equalsIgnoreCase(nombre)) {
                return actual.getCancion();
            }
            actual = actual.getSiguiente();
        }
        return null; // No se encontró
    }

    // Mostrar todas las canciones en consola (para pruebas)
    public void mostrarBiblioteca() {
        NodoLista actual = cabeza;
        System.out.println("--- Biblioteca Musical (" + tamaño + " canciones) ---");
        while (actual != null) {
            System.out.println(actual.getCancion().getNombre() + " - " + actual.getCancion().getArtista());
            actual = actual.getSiguiente();
        }
        System.out.println("-----------------------------------");
    }

    // Eliminar una canción por nombre (CRUD básico)
    public boolean eliminar(String nombre) {
        if (cabeza == null) return false;

        if (cabeza.getCancion().getNombre().equalsIgnoreCase(nombre)) {
            cabeza = cabeza.getSiguiente();
            tamaño--;
            return true;
        }

        NodoLista actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getCancion().getNombre().equalsIgnoreCase(nombre)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamaño--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int getTamaño() {
        return tamaño;
    }
    public NodoLista getCabeza(){
        return cabeza;
    }
}
