package com.example.caketrack.Admin.Pasteles.Moduls;

public class Pasteles {
    private String id;
    private String nombrePastel;
    private String descripcion;
    private double precio;
    private String tamano;
    private boolean disponible;
    private int cantidadDisponible;
    private String imageUrl; // Campo para URL de imagen

    // Constructor completo incluyendo imageUrl
    public Pasteles(String id, String nombrePastel, String descripcion, double precio, String tamano, boolean disponible, int cantidadDisponible, String imageUrl) {
        this.id = id;
        this.nombrePastel = nombrePastel;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tamano = tamano;
        this.disponible = disponible;
        this.cantidadDisponible = cantidadDisponible;
        this.imageUrl = imageUrl;
    }

    // Constructor sin imageUrl (para compatibilidad)
    public Pasteles(String id, String nombrePastel, String descripcion, double precio, String tamano, boolean disponible, int cantidadDisponible) {
        this.id = id;
        this.nombrePastel = nombrePastel;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tamano = tamano;
        this.disponible = disponible;
        this.cantidadDisponible = cantidadDisponible;
        this.imageUrl = ""; // Valor por defecto vacío
    }

    // Constructor vacío requerido por Firebase
    public Pasteles() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombrePastel() { return nombrePastel; }
    public void setNombrePastel(String nombrePastel) { this.nombrePastel = nombrePastel; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getTamano() { return tamano; }
    public void setTamano(String tamano) { this.tamano = tamano; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}