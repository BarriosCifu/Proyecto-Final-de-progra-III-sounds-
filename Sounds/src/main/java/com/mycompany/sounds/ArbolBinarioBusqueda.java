
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
        public Cancion buscar(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }

    private Cancion buscarRecursivo(NodoArbol nodo, String nombre) {
        // Si llegamos al final y no está, o si el árbol está vacío
        if (nodo == null) {
            return null; 
        }

        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());

        if (comparacion == 0) {
            return nodo.getCancion(); // ¡La encontramos!
        } else if (comparacion < 0) {
            return buscarRecursivo(nodo.getIzquierdo(), nombre); // Buscamos en la izquierda
        } else {
            return buscarRecursivo(nodo.getDerecho(), nombre); // Buscamos en la derecha
        }
        
    }
    // --- ELIMINACIÓN ---
    public void eliminar(String nombre) {
        raiz = eliminarRecursivo(raiz, nombre);
    }

    private NodoArbol eliminarRecursivo(NodoArbol nodo, String nombre) {
        if (nodo == null) {
            return null;
        }

        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());

        if (comparacion < 0) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), nombre));
        } else if (comparacion > 0) {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), nombre));
        } else {
            // Nodo encontrado. Caso 1 y 2: Un hijo o ningún hijo
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            } else if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }

            // Caso 3: Dos hijos. Buscamos el sucesor en inorden (el menor de los mayores)
            NodoArbol sucesor = encontrarMinimo(nodo.getDerecho());
            
            // Reemplazamos los datos del nodo actual con los del sucesor
            nodo.setCancion(sucesor.getCancion());
            
            // Eliminamos el sucesor de su posición original
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), sucesor.getCancion().getNombre()));
        }
        return nodo;
    }

    private NodoArbol encontrarMinimo(NodoArbol nodo) {
        NodoArbol actual = nodo;
        while (actual.getIzquierdo() != null) {
            actual = actual.getIzquierdo();
        }
        return actual;
    }
    // --- MODIFICACIÓN ---
    public boolean modificar(String nombreActual, Cancion nuevosDatos) {
        Cancion cancionExistente = buscar(nombreActual);
        
        if (cancionExistente != null) {
            // Si el nombre cambia, debemos eliminar del árbol y volver a insertar para mantener el orden
            if (!nombreActual.equalsIgnoreCase(nuevosDatos.getNombre())) {
                eliminar(nombreActual);
                insertar(nuevosDatos);
            } else {
                // Si el nombre es el mismo, solo actualizamos los demás atributos
                cancionExistente.setArtista(nuevosDatos.getArtista());
                cancionExistente.setAlbum(nuevosDatos.getAlbum());
                cancionExistente.setGenero(nuevosDatos.getGenero());
                cancionExistente.setAnio(nuevosDatos.getAnio());
            }
            return true;
        }
        System.out.println("No se encontró la canción para modificar.");
        return false;
    }
    }
