package com.DTOs;

public class EliminarDTO {
    private String username;
    private String contrasenaIngresada;

    public EliminarDTO() {}

    public EliminarDTO(String username, String contrasenaIngresada) {
        this.username = username;
        this.contrasenaIngresada = contrasenaIngresada;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContrasenaIngresada() {
        return contrasenaIngresada;
    }

    public void setContrasenaIngresada(String contrasenaIngresada) {
        this.contrasenaIngresada = contrasenaIngresada;
    }
}
