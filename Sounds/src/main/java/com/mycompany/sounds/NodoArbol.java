package com.mycompany.sounds;
public class NodoArbol {
  private Cancion cancion;
    private NodoArbol izquierdo;
    private NodoArbol derecho;
    private int altura; // Necesario para el balanceo del árbol AVL

    public NodoArbol(Cancion cancion) {
        this.cancion = cancion;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1; // Un nodo nuevo siempre se inserta como hoja con altura 1
    }

    // Getters y Setters
    public Cancion getCancion() { return cancion; }
    public void setCancion(Cancion cancion) { this.cancion = cancion; }

    public NodoArbol getIzquierdo() { return izquierdo; }
    public void setIzquierdo(NodoArbol izquierdo) { this.izquierdo = izquierdo; }

    public NodoArbol getDerecho() { return derecho; }
    public void setDerecho(NodoArbol derecho) { this.derecho = derecho; }

    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }  
}
