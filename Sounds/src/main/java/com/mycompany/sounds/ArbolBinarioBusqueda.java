package com.mycompany.sounds;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    public Cancion buscar(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }
    private Cancion buscarRecursivo(NodoArbol nodo, String nombre) {
        if (nodo == null) {
            return null; 
        }
        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());
        if (comparacion == 0) {
            return nodo.getCancion(); 
        } else if (comparacion < 0) {
            return buscarRecursivo(nodo.getIzquierdo(), nombre);
        } else {
            return buscarRecursivo(nodo.getDerecho(), nombre);
        }
    }
    public NodoArbol getRaiz(){
        return raiz;
    }
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
    public String obtenerRecorridosCompletos() {
        StringBuilder sb = new StringBuilder();
        sb.append("PRE-ORDEN (Raíz, Izq, Der):\n");
        generarPreOrden(raiz, sb);
        sb.append("\n\nIN-ORDEN (Izq, Raíz, Der):\n");
        generarInOrden(raiz, sb);
        sb.append("\n\nPOST-ORDEN (Izq, Der, Raíz):\n");
        generarPostOrden(raiz, sb);
        return sb.toString();
    }

    private void generarPreOrden(NodoArbol nodo, StringBuilder sb) {
        if (nodo != null) {
            sb.append(nodo.getCancion().getNombre()).append(" | ");
            generarPreOrden(nodo.getIzquierdo(), sb);
            generarPreOrden(nodo.getDerecho(), sb);
        }
    }
    private void generarInOrden(NodoArbol nodo, StringBuilder sb) {
        if (nodo != null) {
            generarInOrden(nodo.getIzquierdo(), sb);
            sb.append(nodo.getCancion().getNombre()).append(" | ");
            generarInOrden(nodo.getDerecho(), sb);
        }
    }
    private void generarPostOrden(NodoArbol nodo, StringBuilder sb) {
        if (nodo != null) {
            generarPostOrden(nodo.getIzquierdo(), sb);
            generarPostOrden(nodo.getDerecho(), sb);
            sb.append(nodo.getCancion().getNombre()).append(" | ");
        }
    }
    public void generarGraphviz(String rutaArchivo) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ArbolABB {\n");
        dot.append("    node [shape=record, style=filled, fillcolor=\"#ff4d4d\", fontcolor=white, fontname=\"Helvetica\"];\n");
        dot.append("    edge [color=\"#b3b3b3\"];\n");
        dot.append("    bgcolor=\"#121212\";\n");
                if (raiz != null) {
            generarNodosGraphviz(raiz, dot);
        }
        dot.append("}\n");

        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.print(dot.toString());
        } catch (Exception e) {
            System.out.println("Error al exportar DOT: " + e.getMessage());
        }
    }
    private void generarNodosGraphviz(NodoArbol nodo, StringBuilder dot) {
        if (nodo != null) {
            String nombre = nodo.getCancion().getNombre().replace("\"", "\\\"");
            dot.append("    \"").append(nodo.hashCode()).append("\" [label=\"").append(nombre).append("\"];\n");

            if (nodo.getIzquierdo() != null) {
                dot.append("    \"").append(nodo.hashCode()).append("\" -> \"").append(nodo.getIzquierdo().hashCode()).append("\";\n");
                generarNodosGraphviz(nodo.getIzquierdo(), dot);
            }
            if (nodo.getDerecho() != null) {
                dot.append("    \"").append(nodo.hashCode()).append("\" -> \"").append(nodo.getDerecho().hashCode()).append("\";\n");
                generarNodosGraphviz(nodo.getDerecho(), dot);
            }
        }
    }
}
