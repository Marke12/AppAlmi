package com.example.appalmi.modelos;

public class Foto {

    private int imagenResId; // Para imágenes locales (R.drawable...)
    private String urlImagen; // Para imágenes de internet
    private String texto;

    // Constructor para imágenes locales
    public Foto(int imagenResId, String texto) {
        this.imagenResId = imagenResId;
        this.texto = texto;
        this.urlImagen = null;
    }

    // Constructor para imágenes de internet (URLs)
    public Foto(String urlImagen, String texto) {
        this.urlImagen = urlImagen;
        this.texto = texto;
        this.imagenResId = 0;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public String getTexto() {
        return texto;
    }
}
