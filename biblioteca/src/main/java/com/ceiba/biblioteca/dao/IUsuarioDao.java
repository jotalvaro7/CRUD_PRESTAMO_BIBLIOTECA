package com.ceiba.biblioteca.dao;

import com.ceiba.biblioteca.entitys.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioDao extends JpaRepository<Usuario, Long> {

    public Usuario findByIdentificacionUsuario (String identifiacionUsuario);

}
