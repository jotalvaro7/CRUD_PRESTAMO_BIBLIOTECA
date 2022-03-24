package com.ceiba.biblioteca.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
        ResponseModel responseModel = new ResponseModel();
        responseModel.id = usuario.id;
        responseModel.fechaMaximaDevolucion = usuario.fechaMaximaDevolucion;
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

        Calendar fechaInicial = Calendar.getInstance();

        int reposicionDias = 0;
        int cantidadDias = 0;
        int i = 0;

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

        while(i<=cantidadDias){

            if(fechaInicial.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || fechaInicial.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                reposicionDias++;
            }
            i++;
            fechaInicial.add(Calendar.DATE, 1);
        }

        int totalDiasPrestamo = cantidadDias + reposicionDias;
        
        Calendar fechaEntrega = Calendar.getInstance();
        fechaEntrega.add(Calendar.DATE, totalDiasPrestamo);

        //validar si el dia que cayo de entrega es un Sabado o Domingo que no se contaba sumarle los dias faltantes a un dia habil.
        if(fechaEntrega.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            fechaEntrega.add(Calendar.DATE, 1);
        }else if(fechaEntrega.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            fechaEntrega.add(Calendar.DATE, 2);
        }

        Date date = fechaEntrega.getTime();
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateEntrega = simpleDateFormat.format(date);

        usuario.fechaMaximaDevolucion = dateEntrega;
        return usuario;
    }
    
    
}
