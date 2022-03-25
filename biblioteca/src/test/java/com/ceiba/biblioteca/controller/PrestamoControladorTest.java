package com.ceiba.biblioteca.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.ceiba.biblioteca.entitys.Usuario;
import com.ceiba.biblioteca.models.ResponseModel;
import com.ceiba.biblioteca.services.IServiceUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(MockitoJUnitRunner.class)
public class PrestamoControladorTest {


    public static final int USUARIO_AFILIADO = 1;
    public static final int USUARIO_EMPLEADO = 2;
    public static final int USUARIO_INVITADO = 3;
    public static final int USUARIO_DESCONOCIDO = 5;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();


    @Mock
    IServiceUsuario iServiceUsuario;

    @InjectMocks
    PrestamoControlador prestamoControlador;
    

    @Before
    public void setUp() {
        prestamoControlador = new PrestamoControlador(iServiceUsuario);
        mockMvc = MockMvcBuilders.standaloneSetup(prestamoControlador).build();
    }

  

    @Test
    public void prestamoLibroUsuarioAfiliadoDeberiaAlmacenarCorrectamenteYCalcularFechaDeDevolucion() throws Exception {

        Usuario usuario = new Usuario();
        usuario.setIsbn("ASDA7884");
        usuario.setIdentificacionUsuario("974148");
        usuario.setTipoUsuario(USUARIO_AFILIADO);

        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(1L);
        responseModel.setFechaMaximaDevolucion("07/04/2022");
    
        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(responseModel);


        MvcResult resultadoLibroPrestado = mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("ASDA7884", "974148", USUARIO_AFILIADO))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion").exists())
            .andReturn();
            
        ResultadoPrestarTest resultadoPrestamo = objectMapper.readValue(resultadoLibroPrestado.getResponse().getContentAsString(), ResultadoPrestarTest.class);

        LocalDate fechaPrestamo = LocalDate.now();
        fechaPrestamo = addDaysSkippingWeekends(fechaPrestamo, 10);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Usuario usuarioFromConsult = new Usuario();
        usuarioFromConsult.setId(1L);
        usuarioFromConsult.setIsbn("ASDA7884");
        usuarioFromConsult.setIdentificacionUsuario("974148");
        usuarioFromConsult.setTipoUsuario(USUARIO_AFILIADO);
        usuarioFromConsult.setFechaMaximaDevolucion("07/04/2022");

        Mockito.when(iServiceUsuario.findById(any())).thenReturn(usuarioFromConsult);

        mockMvc.perform(get("/prestamo/" + resultadoPrestamo.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion", is(fechaPrestamo.format(formato))))
            .andExpect(jsonPath("$.isbn", is("ASDA7884")))
            .andExpect(jsonPath("$.identificacionUsuario", is("974148")))
            .andExpect(jsonPath("$.tipoUsuario", is(USUARIO_AFILIADO)));  
            
    }


    @Test
    public void prestamoLibroUsuarioEmpleadoDeberiaAlmacenarCorrectamenteYCalcularFechaDeDevolucion() throws Exception {
        
    }



    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static LocalDate addDaysSkippingWeekends(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }
        return result;
    }
}
