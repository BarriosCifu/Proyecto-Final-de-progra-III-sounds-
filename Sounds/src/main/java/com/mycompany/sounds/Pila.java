package com.mycompany.sounds;
public class Pila {
  private NodoLista cima;
    public Pila() {
        this.cima = null;
    }
    public void push(Cancion cancion) {
        NodoLista nuevo = new NodoLista(cancion);
        if (cima != null) {
            nuevo.setSiguiente(cima);
        }
        cima = nuevo;
    }
    public Cancion pop() {
        if (cima == null) {
            return null; 
        }
        Cancion extraida = cima.getCancion();
        cima = cima.getSiguiente();
        return extraida;
    }
    public Cancion peek() {
        if (cima != null) {
            return cima.getCancion();
        }
        return null;
    }  
}
