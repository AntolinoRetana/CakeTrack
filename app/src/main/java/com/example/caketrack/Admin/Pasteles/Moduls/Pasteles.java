package com.example.caketrack.Admin.Pasteles.Moduls;

public class Pasteles {
    private String id;
    private String nombrePastel;
    private String descripcion;
    private double precio;
    private String tamano;
    private boolean disponible;
    private int cantidadDisponible;

    public Pasteles(String id, String nombrePastel, String descripcion, double precio, String tamano, boolean disponible, int cantidadDisponible) {
        this.id = id;
        this.nombrePastel = nombrePastel;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tamano = tamano;
        this.disponible = disponible;
        this.cantidadDisponible = cantidadDisponible;
    }

    public Pasteles() {
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombrePastel() {
        return nombrePastel;
    }

    public void setNombrePastel(String nombrePastel) {
        this.nombrePastel = nombrePastel;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }
}
