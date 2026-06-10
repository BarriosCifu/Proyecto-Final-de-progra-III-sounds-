package com.mycompany.sounds;

public class Cancion {
    private String nombre;     
    private String artist;    
    private String album;      
    private String genero;     
    private int anio;          
    private long duracion;     
    private long tamano;       
    private String ruta;       
    
    // NUEVO: Atributo para almacenar los bytes de la carátula
    private byte[] imagenCaratula;

    public Cancion(String nombre, String artista, String album, String genero, int anio, long duracion, long tamano, String ruta) {
        this.nombre = nombre;
        this.artist = artista;
        this.album = album;
        this.genero = genero;
        this.anio = anio;
        this.duracion = duracion;
        this.tamano = tamano;
        this.ruta = ruta;
        this.imagenCaratula = null;
    }

    public Cancion() {
    }
   
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getArtista() { return artist; }
    public void setArtista(String artista) { this.artist = artista; }
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
    
    // NUEVO: Métodos de acceso para la carátula
    public byte[] getImagenCaratula() { return imagenCaratula; }
    public void setImagenCaratula(byte[] imagenCaratula) { this.imagenCaratula = imagenCaratula; }

    @Override
    public String toString() {
        return "Cancion{" +
                "nombre='" + nombre + '\'' +
                ", artista='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", genero='" + genero + '\'' +
                ", anio=" + anio +
                ", duracion=" + duracion + "s" +
                ", tamano=" + (tamano / 1024 / 1024) + "MB" +
                ", tieneCaratula=" + (imagenCaratula != null) +
                '}';
    }
}
