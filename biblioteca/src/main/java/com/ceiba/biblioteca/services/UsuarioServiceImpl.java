package com.ceiba.biblioteca.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.ceiba.biblioteca.dao.IUsuarioDao;
import com.ceiba.biblioteca.entitys.Usuario;
import com.ceiba.biblioteca.models.ResponseModel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements IServiceUsuario {


    public static final int USUARIO_AFILIADO = 1;
    public static final int USUARIO_EMPLEADO = 2;
    public static final int USUARIO_INVITADO = 3;

    private IUsuarioDao usuarioDao;

    public UsuarioServiceImpl(IUsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public ResponseEntity<?> save(Usuario usuario){

        Map<String, Object> response = new HashMap<>();
        //buscar si el usuario existe
        Usuario usuarioExistente = findByIdentificacionUsuario(usuario.getIdentificacionUsuario());

        if(usuarioExistente != null && usuarioExistente.getTipoUsuario() == USUARIO_INVITADO){
                response.put("mensaje", "El usuario con identificación " + usuario.getIdentificacionUsuario() +  " ya tiene un libro prestado por lo cual no se le puede realizar otro préstamo");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(usuario.getTipoUsuario() != USUARIO_AFILIADO && usuario.getTipoUsuario() != USUARIO_EMPLEADO && usuario.getTipoUsuario() != USUARIO_INVITADO){
            response.put("mensaje", "Tipo de usuario no permitido en la biblioteca");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        
        String fechaPrestamo = settingDate(usuario);
        usuario.setFechaMaximaDevolucion( fechaPrestamo );
        usuarioDao.save(usuario);
        ResponseModel responseModel = responseLibroPrestado(usuario);
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
    

    @Override
    public Usuario findByIdentificacionUsuario(String identificacionUsuario){
        return usuarioDao.findByIdentificacionUsuario(identificacionUsuario);
    }

    @Override
    public Usuario findById(Long id){
        return usuarioDao.findById(id).orElse(null);
    }
    

    @Override
    public String settingDate(Usuario usuario){  

        int cantidadDias = 0;
        switch (usuario.getTipoUsuario()) {
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
        return fechaPrestamo.format(format);
       
    }

    private ResponseModel responseLibroPrestado(Usuario usuario){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(usuario.getId());
        responseModel.setFechaMaximaDevolucion(usuario.getFechaMaximaDevolucion());
        return responseModel;
    }
    
    
}
