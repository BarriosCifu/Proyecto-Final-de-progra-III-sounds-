package com.mycompany.sounds;

/**
 *
 * @author barri
 */
public class ArbolAVL {
    private NodoArbol raiz;

    public ArbolAVL() {
        this.raiz = null;
    }

    // --- UTILIDADES DE BALANCEO ---
    private int obtenerAltura(NodoArbol nodo) {
        if (nodo == null) return 0;
        return nodo.getAltura();
    }

    private int obtenerFactorEquilibrio(NodoArbol nodo) {
        if (nodo == null) return 0;
        return obtenerAltura(nodo.getIzquierdo()) - obtenerAltura(nodo.getDerecho());
    }

    // --- ROTACIONES --- [cite: 120]
    
    // Rotación Simple a la Derecha (RD) [cite: 122]
    private NodoArbol rotacionDerecha(NodoArbol y) {
        NodoArbol x = y.getIzquierdo();
        NodoArbol T2 = x.getDerecho();

        // Realizar rotación
        x.setDerecho(y);
        y.setIzquierdo(T2);

        // Actualizar alturas
        y.setAltura(Math.max(obtenerAltura(y.getIzquierdo()), obtenerAltura(y.getDerecho())) + 1);
        x.setAltura(Math.max(obtenerAltura(x.getIzquierdo()), obtenerAltura(x.getDerecho())) + 1);

        return x;
    }

    // Rotación Simple a la Izquierda (RI) [cite: 121]
    private NodoArbol rotacionIzquierda(NodoArbol x) {
        NodoArbol y = x.getDerecho();
        NodoArbol T2 = y.getIzquierdo();

        // Realizar rotación
        y.setIzquierdo(x);
        x.setDerecho(T2);

        // Actualizar alturas
        x.setAltura(Math.max(obtenerAltura(x.getIzquierdo()), obtenerAltura(x.getDerecho())) + 1);
        y.setAltura(Math.max(obtenerAltura(y.getIzquierdo()), obtenerAltura(y.getDerecho())) + 1);

        return y;
    }

    // --- INSERCIÓN CON BALANCEO AUTOMÁTICO --- [cite: 118, 119]
    public void insertar(Cancion cancion) {
        raiz = insertarRecursivo(raiz, cancion);
    }

    private NodoArbol insertarRecursivo(NodoArbol nodo, Cancion cancion) {
        // 1. Inserción normal de ABB
        if (nodo == null) {
            return new NodoArbol(cancion);
        }

        int comparacion = cancion.getNombre().compareToIgnoreCase(nodo.getCancion().getNombre());

        if (comparacion < 0) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), cancion));
        } else if (comparacion > 0) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), cancion));
        } else {
            return nodo; // No se permiten duplicados con el mismo nombre exacto
        }

        // 2. Actualizar la altura de este nodo ancestro
        nodo.setAltura(1 + Math.max(obtenerAltura(nodo.getIzquierdo()), obtenerAltura(nodo.getDerecho())));

        // 3. Obtener el factor de equilibrio para saber si se desbalanceó
        int balance = obtenerFactorEquilibrio(nodo);

        // Si se desbalanceó, hay 4 casos posibles de rotación:

        // Caso Izquierda-Izquierda (Rotación Simple Derecha - RD) [cite: 122]
        if (balance > 1 && cancion.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getCancion().getNombre()) < 0) {
            return rotacionDerecha(nodo);
        }

        // Caso Derecha-Derecha (Rotación Simple Izquierda - RI) [cite: 121]
        if (balance < -1 && cancion.getNombre().compareToIgnoreCase(nodo.getDerecho().getCancion().getNombre()) > 0) {
            return rotacionIzquierda(nodo);
        }

        // Caso Izquierda-Derecha (Rotación Doble: Izquierda en el hijo, Derecha en la raíz - RID) [cite: 123]
        if (balance > 1 && cancion.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getCancion().getNombre()) > 0) {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }

        // Caso Derecha-Izquierda (Rotación Doble: Derecha en el hijo, Izquierda en la raíz - RDI) [cite: 124]
        if (balance < -1 && cancion.getNombre().compareToIgnoreCase(nodo.getDerecho().getCancion().getNombre()) < 0) {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
        }

        return nodo; // Retorna el nodo sin cambios si ya estaba balanceado
    }
    
    // --- BÚSQUEDA ---
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
