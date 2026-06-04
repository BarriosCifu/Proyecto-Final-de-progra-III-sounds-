package com.mycompany.sounds;
public class NodoLista {
    private Cancion cancion;
    private NodoLista siguiente;
    private NodoLista anterior;

    public NodoLista(Cancion cancion) {
        this.cancion = cancion;
        this.siguiente = null;
        this.anterior = null;
    }
    public Cancion getCancion() { return cancion; }
    public void setCancion(Cancion cancion) { this.cancion = cancion; }
    public NodoLista getSiguiente() { return siguiente; }
    public void setSiguiente(NodoLista siguiente) { this.siguiente = siguiente; }
    public NodoLista getAnterior() { return anterior; }
    public void setAnterior(NodoLista anterior) { this.anterior = anterior; }
}
