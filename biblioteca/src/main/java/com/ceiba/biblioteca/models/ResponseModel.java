package com.ceiba.biblioteca.models;

import lombok.Data;

@Data
public class ResponseModel {

    public Long id;
    public String fechaMaximaDevolucion;

    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFechaMaximaDevolucion() {
        return fechaMaximaDevolucion;
    }
    public void setFechaMaximaDevolucion(String fechaMaximaDevolucion) {
        this.fechaMaximaDevolucion = fechaMaximaDevolucion;
    }
    
    

}
