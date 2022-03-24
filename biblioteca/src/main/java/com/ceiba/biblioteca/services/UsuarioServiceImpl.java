package com.ceiba.biblioteca.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.ceiba.biblioteca.dao.IUsuarioDao;
import com.ceiba.biblioteca.entitys.Usuario;
import com.ceiba.biblioteca.models.ResponseModel;


import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements IServiceUsuario {

    private IUsuarioDao usuarioDao;

    public UsuarioServiceImpl(IUsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public ResponseModel save(Usuario usuario){
        settingDate(usuario);
        usuarioDao.save(usuario);
        ResponseModel responseModel = responseJsonCongratulations(usuario);
        return responseModel;
    }
    

    @Override
    public Usuario findByIdentificacionUsuario(String identificacionUsuario){
        return usuarioDao.findByIdentificaciónUsuario(identificacionUsuario);
    }

    @Override
    public Usuario findById(Long id){
        return usuarioDao.findById(id).orElse(null);
    }
    


    private Usuario settingDate(Usuario usuario){        
        int cantidadDias = 0;
        switch (usuario.tipoUsuario) {
            case 1:
                cantidadDias = 10;
                break;
            case 2:
                cantidadDias = 8;
                break;
            case 3:
                cantidadDias = 7;
                break;
            default:
                break;
        }

        LocalDate fechaPrestamo = LocalDate.now();
        int reposicionDias = 0;
        while(reposicionDias < cantidadDias){

            fechaPrestamo = fechaPrestamo.plusDays(1);
            if (!(fechaPrestamo.getDayOfWeek() == DayOfWeek.SATURDAY || fechaPrestamo.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++reposicionDias;
            }
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        usuario.fechaMaximaDevolucion = fechaPrestamo.format(format);;
        return usuario;
    }

    private ResponseModel responseJsonCongratulations(Usuario usuario){
        ResponseModel responseModel = new ResponseModel();
        responseModel.id = usuario.id;
        responseModel.fechaMaximaDevolucion = usuario.fechaMaximaDevolucion;
        return responseModel;
    }
    
    
}
