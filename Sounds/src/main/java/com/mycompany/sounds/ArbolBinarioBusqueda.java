
package com.mycompany.sounds;

/**
 *
 * @author barri
 */
public class ArbolBinarioBusqueda {
private NodoArbol raiz;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }

    // --- INSERCIÓN ---
    public void insertar(Cancion cancion) {
        raiz = insertarRecursivo(raiz, cancion);
    }

    private NodoArbol insertarRecursivo(NodoArbol nodoActual, Cancion cancion) {
        // Si llegamos a una hoja libre, creamos el nuevo nodo
        if (nodoActual == null) {
            return new NodoArbol(cancion);
        }

        // Comparamos los nombres para decidir el camino (izquierda o derecha)
        // compareToIgnoreCase devuelve < 0 si va antes en el alfabeto, > 0 si va después
        int comparacion = cancion.getNombre().compareToIgnoreCase(nodoActual.getCancion().getNombre());

        if (comparacion < 0) {
            nodoActual.setIzquierdo(insertarRecursivo(nodoActual.getIzquierdo(), cancion));
        } else if (comparacion > 0) {
            nodoActual.setDerecho(insertarRecursivo(nodoActual.getDerecho(), cancion));
        }
        
        return nodoActual;
    }

    // --- RECORRIDOS ---
    
    // InOrden: Izquierda -> Raíz -> Derecha (Útil para imprimir en orden alfabético)
    public void mostrarInOrden() {
        System.out.println("--- Recorrido InOrden ---");
        inOrdenRecursivo(raiz);
        System.out.println("-------------------------");
    }

    private void inOrdenRecursivo(NodoArbol nodo) {
        if (nodo != null) {
            inOrdenRecursivo(nodo.getIzquierdo());
            System.out.println(nodo.getCancion().getNombre());
            inOrdenRecursivo(nodo.getDerecho());
        }
    }

    // PreOrden: Raíz -> Izquierda -> Derecha
    public void mostrarPreOrden() {
        System.out.println("--- Recorrido PreOrden ---");
        preOrdenRecursivo(raiz);
        System.out.println("--------------------------");
    }

    private void preOrdenRecursivo(NodoArbol nodo) {
        if (nodo != null) {
            System.out.println(nodo.getCancion().getNombre());
            preOrdenRecursivo(nodo.getIzquierdo());
            preOrdenRecursivo(nodo.getDerecho());
        }
    }

    // PostOrden: Izquierda -> Derecha -> Raíz
    public void mostrarPostOrden() {
        System.out.println("--- Recorrido PostOrden ---");
        postOrdenRecursivo(raiz);
        System.out.println("---------------------------");
    }

    private void postOrdenRecursivo(NodoArbol nodo) {
        if (nodo != null) {
            postOrdenRecursivo(nodo.getIzquierdo());
            postOrdenRecursivo(nodo.getDerecho());
            System.out.println(nodo.getCancion().getNombre());
        }
    }
}