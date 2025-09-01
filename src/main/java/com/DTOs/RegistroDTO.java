package com.DTOs;

public class RegistroDTO {
    private String username;
    private String correo;
    private String password;
    
    public RegistroDTO() {}

    public RegistroDTO(String username, String correo, String password) {
        this.username = username;
        this.correo = correo;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
