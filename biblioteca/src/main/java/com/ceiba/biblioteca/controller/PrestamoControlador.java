package com.ceiba.biblioteca.controller;


import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

import com.ceiba.biblioteca.entitys.Usuario;
import com.ceiba.biblioteca.services.IServiceUsuario;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("prestamo")
public class PrestamoControlador {

    private IServiceUsuario iServiceUsuario;

    public PrestamoControlador(IServiceUsuario iServiceUsuario) {
        this.iServiceUsuario = iServiceUsuario;
    }

    String message = "mensaje";

    @PostMapping()
    public ResponseEntity<?> save(@Valid @RequestBody Usuario usuario){

        Map<String, Object> response = new HashMap<>();

        try{
           return iServiceUsuario.save(usuario);
        }catch(DataAccessException e){
            response.put(message, "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){
        Usuario usuario = null;

        Map<String, Object> response = new HashMap<>();
        try {
            usuario = iServiceUsuario.findById(id);
        } catch (DataAccessException e) {
            response.put(message, "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(usuario == null){
            response.put(message, "El cliente ID: ".concat(id.toString()).concat(" no existe en la base de datos"));
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(usuario, HttpStatus.OK);

    }


}

