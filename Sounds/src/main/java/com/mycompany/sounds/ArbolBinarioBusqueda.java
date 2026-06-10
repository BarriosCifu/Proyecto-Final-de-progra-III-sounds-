package com.mycompany.sounds;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author barri
 */
public class ArbolBinarioBusqueda {
    private NodoArbol raiz;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }

    public void insertar(Cancion cancion) {
        raiz = insertarRecursivo(raiz, cancion);
    }

    private NodoArbol insertarRecursivo(NodoArbol nodoActual, Cancion cancion) {
        if (nodoActual == null) {
            return new NodoArbol(cancion);
        }
        int comparacion = cancion.getNombre().compareToIgnoreCase(nodoActual.getCancion().getNombre());
        if (comparacion < 0) {
            nodoActual.setIzquierdo(insertarRecursivo(nodoActual.getIzquierdo(), cancion));
        } else if (comparacion > 0) {
            nodoActual.setDerecho(insertarRecursivo(nodoActual.getDerecho(), cancion));
        }
        return nodoActual;
    }

    // InOrden: Izquierda -> Raíz -> Derecha 
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
        if (nodo == null) {
            return null; 
        }
        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());
        if (comparacion == 0) {
            return nodo.getCancion(); // ¡La encontramos!
        } else if (comparacion < 0) {
            return buscarRecursivo(nodo.getIzquierdo(), nombre);
        } else {
            return buscarRecursivo(nodo.getDerecho(), nombre);
        }
    }

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
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            } else if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            NodoArbol sucesor = encontrarMinimo(nodo.getDerecho());
            nodo.setCancion(sucesor.getCancion());
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

    public boolean modificar(String nombreActual, Cancion nuevosDatos) {
        Cancion cancionExistente = buscar(nombreActual);
        
        if (cancionExistente != null) {
            if (!nombreActual.equalsIgnoreCase(nuevosDatos.getNombre())) {
                eliminar(nombreActual);
                insertar(nuevosDatos);
            } else {
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

    public NodoArbol getRaiz(){
        return raiz;
    }

    // =========================================================
    // --- NUEVO: MÉTODOS REQUERIDOS PARA EL BENCHMARKING ---
    // =========================================================

    public List<Cancion> obtenerListaInOrden() {
        List<Cancion> listaExtraida = new ArrayList<>();
        recorridoInOrdenParaLista(this.raiz, listaExtraida);
        return listaExtraida;
    }

    private void recorridoInOrdenParaLista(NodoArbol actual, List<Cancion> listaExtraida) {
        if (actual != null) {
            recorridoInOrdenParaLista(actual.getIzquierdo(), listaExtraida);
            listaExtraida.add(actual.getCancion()); 
            recorridoInOrdenParaLista(actual.getDerecho(), listaExtraida);
        }
    }

    public List<Cancion> buscarPorFiltro(String textoBusqueda) {
        List<Cancion> resultados = new ArrayList<>();
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            return obtenerListaInOrden(); 
        }
        buscarFiltroRecursivo(raiz, textoBusqueda.toLowerCase().trim(), resultados);
        return resultados;
    }

    private void buscarFiltroRecursivo(NodoArbol nodo, String filtro, List<Cancion> resultados) {
        if (nodo != null) {
            buscarFiltroRecursivo(nodo.getIzquierdo(), filtro, resultados);
            
            String nombreCancion = nodo.getCancion().getNombre().toLowerCase();
            String nombreArtista = nodo.getCancion().getArtista().toLowerCase();
            
            if (nombreCancion.contains(filtro) || nombreArtista.contains(filtro)) {
                resultados.add(nodo.getCancion());
            }
            
            buscarFiltroRecursivo(nodo.getDerecho(), filtro, resultados);
        }
    }
}
