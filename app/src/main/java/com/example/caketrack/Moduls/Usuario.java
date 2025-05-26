package com.example.caketrack.Moduls;

public class Usuario {
    private String name;
    private String alias;
    private String role;

    public Usuario(String alias, String name, String role) {
        this.alias = alias;
        this.name = name;
        this.role = role;
    }

    public Usuario() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
