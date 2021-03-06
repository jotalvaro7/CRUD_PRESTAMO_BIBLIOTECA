package com.ceiba.biblioteca.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


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

        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(1L);
        responseModel.setFechaMaximaDevolucion("08/04/2022");
    
        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(responseModel, HttpStatus.OK));


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
        usuarioFromConsult.setFechaMaximaDevolucion("08/04/2022");

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
        
        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(1L);
        responseModel.setFechaMaximaDevolucion("06/04/2022");

        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(responseModel, HttpStatus.OK));

        MvcResult resultadoLibroPrestado = mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("AWQ489", "7481545", USUARIO_EMPLEADO))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion").exists())
            .andReturn();
        
        ResultadoPrestarTest resultadoPrestamo = objectMapper.readValue(resultadoLibroPrestado.getResponse().getContentAsString(), ResultadoPrestarTest.class);

        LocalDate fechaPrestamo = LocalDate.now();
        fechaPrestamo = addDaysSkippingWeekends(fechaPrestamo, 8);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Usuario usuarioFromConsult = new Usuario();
        usuarioFromConsult.setId(1L);
        usuarioFromConsult.setIsbn("AWQ489");
        usuarioFromConsult.setIdentificacionUsuario("7481545");
        usuarioFromConsult.setTipoUsuario(USUARIO_EMPLEADO);
        usuarioFromConsult.setFechaMaximaDevolucion("06/04/2022");


        Mockito.when(iServiceUsuario.findById(any())).thenReturn(usuarioFromConsult);

        mockMvc.perform(get("/prestamo/" + resultadoPrestamo.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion", is(fechaPrestamo.format(formato))))
            .andExpect(jsonPath("$.isbn", is("AWQ489")))
            .andExpect(jsonPath("$.identificacionUsuario", is("7481545")))
            .andExpect(jsonPath("$.tipoUsuario", is(USUARIO_EMPLEADO)));  
    }

    @Test
    public void prestamoLibroUsuarioInvitadoDeberiaAlmacenarCorrectamenteYCalcularFechaDeDevolucion() throws Exception {

        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(1L);
        responseModel.setFechaMaximaDevolucion("05/04/2022");

        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(responseModel, HttpStatus.OK));

        MvcResult resultadoLibroPrestado = mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("EQWQW8545", "74851254", USUARIO_INVITADO))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion").exists())
            .andReturn();
        
        ResultadoPrestarTest resultadoPrestamo = objectMapper.readValue(resultadoLibroPrestado.getResponse().getContentAsString(), ResultadoPrestarTest.class);

        LocalDate fechaPrestamo = LocalDate.now();
        fechaPrestamo = addDaysSkippingWeekends(fechaPrestamo, 7);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Usuario usuarioFromConsult = new Usuario();
        usuarioFromConsult.setId(1L);
        usuarioFromConsult.setIsbn("EQWQW8545");
        usuarioFromConsult.setIdentificacionUsuario("74851254");
        usuarioFromConsult.setTipoUsuario(USUARIO_INVITADO);
        usuarioFromConsult.setFechaMaximaDevolucion("05/04/2022");


        Mockito.when(iServiceUsuario.findById(any())).thenReturn(usuarioFromConsult);

        mockMvc.perform(get("/prestamo/" + resultadoPrestamo.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion", is(fechaPrestamo.format(formato))))
            .andExpect(jsonPath("$.isbn", is("EQWQW8545")))
            .andExpect(jsonPath("$.identificacionUsuario", is("74851254")))
            .andExpect(jsonPath("$.tipoUsuario", is(USUARIO_INVITADO)));  

    }

    @Test
    public void usuarioInvitadoTratandoDePrestarUnSegundoLibroDeberiaRetornarExcepcion() throws Exception {

        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(1L);
        responseModel.setFechaMaximaDevolucion("05/04/2022");

        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(responseModel, HttpStatus.OK));

        mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("EQWQW8545", "1111111111", USUARIO_INVITADO))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion").exists());
        
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "El usuario con identificaci??n 1111111111 ya tiene un libro prestado por lo cual no se le puede realizar otro pr??stamo");

        Mockito.when(iServiceUsuario.save(any())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("EQWQW8545", "1111111111", USUARIO_INVITADO))))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.mensaje", is("El usuario con identificaci??n 1111111111 ya tiene un libro prestado por lo cual no se le puede realizar otro pr??stamo")));

    }


    @Test
    public void usuarioNoInvitadoTratandoDePrestarUnSegundoLibroDeberiaPrestarloCorrectamente() throws Exception {

        ResponseModel responseModel = new ResponseModel();
        responseModel.setId(1L);
        responseModel.setFechaMaximaDevolucion("08/04/2022");

        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(responseModel, HttpStatus.OK));

        mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("EQWQW8545", "1111111111", USUARIO_AFILIADO))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion").exists());

        ResponseModel responseModelSecondRegister = new ResponseModel();
        responseModelSecondRegister.setId(2L);
        responseModelSecondRegister.setFechaMaximaDevolucion("08/04/2022");

        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(responseModelSecondRegister, HttpStatus.OK));

       mockMvc.perform(post("/prestamo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("EQWQW8545", "1111111111", USUARIO_AFILIADO))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fechaMaximaDevolucion").exists());

    }

    @Test
    public void prestamoConTipoDeUsuarioNoPermitidoDeberiaRetornarExcepcion() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Tipo de usuario no permitido en la biblioteca");

        Mockito.when(iServiceUsuario.save(any(Usuario.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));

        mockMvc.perform(
                 post("/prestamo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SolicitudPrestarLibroTest("EQWQW8545", "1111111111", USUARIO_DESCONOCIDO))))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.mensaje", is("Tipo de usuario no permitido en la biblioteca")));
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
