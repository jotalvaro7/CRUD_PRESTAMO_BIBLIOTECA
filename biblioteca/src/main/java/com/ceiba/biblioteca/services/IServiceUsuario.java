package com.ceiba.biblioteca.services;

import com.ceiba.biblioteca.entitys.Usuario;
import com.ceiba.biblioteca.models.ResponseModel;

public interface IServiceUsuario {
    
    public ResponseModel save(Usuario usuario);

    public Usuario findByIdentificacionUsuario(String identificacionUsuario);

    public Usuario findById(Long id);

}
