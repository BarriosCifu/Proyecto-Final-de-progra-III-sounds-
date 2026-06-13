package com.mycompany.sounds;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    
    private void recorridoInOrden(NodoArbol actual, List<Cancion> listaExtraida) {
        if (actual != null) {
            recorridoInOrden(actual.getIzquierdo(), listaExtraida);
            listaExtraida.add(actual.getCancion());
            recorridoInOrden(actual.getDerecho(), listaExtraida);
        }
    }
    
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
        public void eliminar(String nombre) {
        raiz = eliminarRecursivo(raiz, nombre);
    }
        private NodoArbol eliminarRecursivo(NodoArbol nodo, String nombre) {
        if (nodo == null) return nodo;
        
        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());
        if (comparacion < 0) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), nombre));
        } else if (comparacion > 0) {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), nombre));
        } else {
            if ((nodo.getIzquierdo() == null) || (nodo.getDerecho() == null)) {
                NodoArbol temp = (nodo.getIzquierdo() != null) ? nodo.getIzquierdo() : nodo.getDerecho();
                if (temp == null) {
                    temp = nodo;
                    nodo = null;
                } else {
                    nodo = temp;
                }
            } else {
                NodoArbol temp = encontrarMinimo(nodo.getDerecho());
                nodo.setCancion(temp.getCancion());
                nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), temp.getCancion().getNombre()));
            }
        }
        
        if (nodo == null) return nodo;
        
        nodo.setAltura(Math.max(obtenerAltura(nodo.getIzquierdo()), obtenerAltura(nodo.getDerecho())) + 1);
        int balance = obtenerFactorEquilibrio(nodo);
        
        if (balance > 1 && obtenerFactorEquilibrio(nodo.getIzquierdo()) >= 0) {
            return rotacionDerecha(nodo);
        }
        if (balance > 1 && obtenerFactorEquilibrio(nodo.getIzquierdo()) < 0) {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && obtenerFactorEquilibrio(nodo.getDerecho()) <= 0) {
            return rotacionIzquierda(nodo);
        }
        if (balance < -1 && obtenerFactorEquilibrio(nodo.getDerecho()) > 0) {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
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
        if (nodo == null) return null; 
        int comparacion = nombre.compareToIgnoreCase(nodo.getCancion().getNombre());
        if (comparacion == 0) return nodo.getCancion();
        if (comparacion < 0) return buscarRecursivo(nodo.getIzquierdo(), nombre);
        return buscarRecursivo(nodo.getDerecho(), nombre);
    }
    
    public List<Cancion> buscarPorFiltro(String textoBusqueda) {
        List<Cancion> resultados = new ArrayList<>();
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) return obtenerListaInOrden();
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
        dot.append("digraph ArbolAVL {\n");
        dot.append("    node [shape=record, style=filled, fillcolor=\"#1db954\", fontcolor=white, fontname=\"Helvetica\"];\n");
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
            double mb = nodo.getCancion().getTamano() / (1024.0 * 1024.0);
            String tamanoFormateado = String.format("%.2f MB", mb);

               dot.append("    \"").append(nodo.hashCode()).append("\" [label=\"").append(nombre).append("\\n").append(tamanoFormateado).append("\"];\n");

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
