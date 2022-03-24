package com.ceiba.biblioteca.entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name= "usuarios")
public class Usuario {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotEmpty(message = "No puede estar vacio")
    @Size(min=1, max = 10, message = "El isbn debe estar entre 1 y 10 caracteres")
    @Column(nullable = false, unique = true)
    public String isbn;

    @NotEmpty(message = "No puede estar vacio")
    @Size(min=1, max = 10, message = "la identificacion del usuario debe estar entre 1 y 10 caracteres")
    @Column(nullable = false, unique = true)
    public String identificaciónUsuario;

    public int tipoUsuario;

    public String fechaMaximaDevolucion;




}
