package com.ceiba.biblioteca.services;

import com.ceiba.biblioteca.entitys.Usuario;

import org.springframework.http.ResponseEntity;

public interface IServiceUsuario {
    
    public ResponseEntity save(Usuario usuario);

    public Usuario findByIdentificacionUsuario(String identificacionUsuario);

    public Usuario findById(Long id);

    public String settingDate(Usuario usuario);

}
