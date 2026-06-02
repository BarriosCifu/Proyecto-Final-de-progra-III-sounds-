package com.mycompany.sounds;
public class Cancion {
    private String nombre;     // [cite: 85]
    private String artista;    // [cite: 86]
    private String album;      // [cite: 87]
    private String genero;     // [cite: 88]
    private int anio;          // [cite: 92]
    private long duracion;     // [cite: 89] (Recomendado en segundos)
    private long tamano;       // [cite: 90] (Recomendado en bytes)
    private String ruta;       // [cite: 91] (Ruta absoluta en el disco)

    // Constructor principal
    public Cancion(String nombre, String artista, String album, String genero, int anio, long duracion, long tamano, String ruta) {
        this.nombre = nombre;
        this.artista = artista;
        this.album = album;
        this.genero = genero;
        this.anio = anio;
        this.duracion = duracion;
        this.tamano = tamano;
        this.ruta = ruta;
    }
    // Constructor vacío por si necesitas inicializar y llenar después
    public Cancion() {
    }
    // --- Getters y Setters ---
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public long getDuracion() { return duracion; }
    public void setDuracion(long duracion) { this.duracion = duracion; }

    public long getTamano() { return tamano; }
    public void setTamano(long tamano) { this.tamano = tamano; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    // Método toString para facilitar las pruebas en consola
    @Override
    public String toString() {
        return "Cancion{" +
                "nombre='" + nombre + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", genero='" + genero + '\'' +
                ", anio=" + anio +
                ", duracion=" + duracion + "s" +
                ", tamano=" + (tamano / 1024 / 1024) + "MB" +
                '}';
    }
}
