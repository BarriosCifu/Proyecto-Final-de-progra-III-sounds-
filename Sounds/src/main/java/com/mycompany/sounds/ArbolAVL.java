package com.mycompany.sounds;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author barri
 */
public class ArbolAVL {
    private NodoArbol raiz;

    public ArbolAVL() {
        this.raiz = null;
    }

    public List<Cancion> obtenerListaInOrden() {
        List<Cancion> listaExtraida = new ArrayList<>();
        recorridoInOrden(this.raiz, listaExtraida);
        return listaExtraida;
    }

    /**
     * Proceso recursivo que recorre el árbol (Izquierdo - Raíz - Derecho).
     */
    private void recorridoInOrden(NodoArbol actual, List<Cancion> listaExtraida) {
        if (actual != null) {
            recorridoInOrden(actual.getIzquierdo(), listaExtraida);
            listaExtraida.add(actual.getCancion()); // Agrega la canción al llegar a la raíz del subárbol
            recorridoInOrden(actual.getDerecho(), listaExtraida);
        }
    }

    // --- MÉTODOS ORIGINALES DE TU ÁRBOL AVL ---

    private int obtenerAltura(NodoArbol nodo) {
        if (nodo == null) return 0;
        return nodo.getAltura();
    }

    private int obtenerFactorEquilibrio(NodoArbol nodo) {
        if (nodo == null) return 0;
        return obtenerAltura(nodo.getIzquierdo()) - obtenerAltura(nodo.getDerecho());
    }

    private NodoArbol rotacionDerecha(NodoArbol y) {
        NodoArbol x = y.getIzquierdo();
        NodoArbol T2 = x.getDerecho();
        x.setDerecho(y);
        y.setIzquierdo(T2);
        y.setAltura(Math.max(obtenerAltura(y.getIzquierdo()), obtenerAltura(y.getDerecho())) + 1);
        x.setAltura(Math.max(obtenerAltura(x.getIzquierdo()), obtenerAltura(x.getDerecho())) + 1);
        return x;
    }

    private NodoArbol rotacionIzquierda(NodoArbol x) {
        NodoArbol y = x.getDerecho();
        NodoArbol T2 = y.getIzquierdo();
        y.setIzquierdo(x);
        x.setDerecho(T2);
        x.setAltura(Math.max(obtenerAltura(x.getIzquierdo()), obtenerAltura(x.getDerecho())) + 1);
        y.setAltura(Math.max(obtenerAltura(y.getIzquierdo()), obtenerAltura(y.getDerecho())) + 1);
        return y;
    }

    public void insertar(Cancion cancion) {
        raiz = insertarRecursivo(raiz, cancion);
    }

    private NodoArbol insertarRecursivo(NodoArbol nodo, Cancion cancion) {
        if (nodo == null) {
            return new NodoArbol(cancion);
        }
        int comparacion = cancion.getNombre().compareToIgnoreCase(nodo.getCancion().getNombre());
        if (comparacion < 0) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), cancion));
        } else if (comparacion > 0) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), cancion));
        } else {
            return nodo; 
        }
        
        nodo.setAltura(1 + Math.max(obtenerAltura(nodo.getIzquierdo()), obtenerAltura(nodo.getDerecho())));
       
        int balance = obtenerFactorEquilibrio(nodo);

        if (balance > 1 && cancion.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getCancion().getNombre()) < 0) {
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && cancion.getNombre().compareToIgnoreCase(nodo.getDerecho().getCancion().getNombre()) > 0) {
            return rotacionIzquierda(nodo);
        }
        if (balance > 1 && cancion.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getCancion().getNombre()) > 0) {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && cancion.getNombre().compareToIgnoreCase(nodo.getDerecho().getCancion().getNombre()) < 0) {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
        }
        return nodo;
    }   

    public Cancion buscar(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }

    private Cancion buscarRecursivo(NodoArbol nodo, String nombre) {
        if (nodo == null) return null; 
        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());
        if (comparacion == 0) return nodo.getCancion();
        if (comparacion < 0) return buscarRecursivo(nodo.getIzquierdo(), nombre);
        return buscarRecursivo(nodo.getDerecho(), nombre);
    }
}