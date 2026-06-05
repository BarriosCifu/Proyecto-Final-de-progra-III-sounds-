/**
 *
 * @author barri
 */
package com.mycompany.sounds;

public class ListaCircular {
    private NodoLista cabeza;
    private NodoLista actual;

    public ListaCircular() {
        this.cabeza = null;
        this.actual = null;
    }
  public void insertar(Cancion cancion) {
     NodoLista nuevo = new NodoLista(cancion);
       if (cabeza == null) {
         cabeza = nuevo;
          cabeza.setSiguiente(cabeza);
          cabeza.setAnterior(cabeza);
          actual = cabeza;
        } else {
       NodoLista ultimo = cabeza.getAnterior(); 
                        nuevo.setSiguiente(cabeza);
            nuevo.setAnterior(ultimo);
                        cabeza.setAnterior(nuevo);
            ultimo.setSiguiente(nuevo);
        }
    }
 public Cancion irSiguiente() {
     if (actual != null) {
         actual = actual.getSiguiente();
          return actual.getCancion();
        }
        return null;
    }
    public Cancion irAnterior() {
     if (actual != null) {
         actual = actual.getAnterior();
         return actual.getCancion();
        }
    return null;
    }
}
