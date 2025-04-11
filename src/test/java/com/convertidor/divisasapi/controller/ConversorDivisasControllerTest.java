package com.convertidor.divisasapi.controller;

import com.convertidor.divisasapi.exception.GlobalExceptionHandler;
import com.convertidor.divisasapi.model.ConversorResponse;
import com.convertidor.divisasapi.service.ConversorTasaService;
import com.convertidor.divisasapi.exception.TasasCambioException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConversorDivisasController.class)
@ContextConfiguration(classes = {ConversorDivisasControllerTest.TestConfig.class, ConversorDivisasController.class})
@Import(GlobalExceptionHandler.class)
public class ConversorDivisasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConversorTasaService exchangeRateService;

    // Configuración de contexto de prueba con bean mock
    public static class TestConfig {
        @Bean
        public ConversorTasaService exchangeRateService() {
            return mock(ConversorTasaService.class);
        }
    }

    @Test
    public void cuandoLaEntradaEsValida_haceLaConversion() throws Exception {
        // Configurar el mock del servicio
        ConversorResponse mockResponse = new ConversorResponse();
        mockResponse.setDivisaEntrada("USD");
        mockResponse.setDivisaSalida("EUR");
        mockResponse.setMontoEntrada(100.0);
        mockResponse.setMontoSalida(90.0);
        mockResponse.setTasaConversion(0.9);
        mockResponse.setFechaConversion(LocalDate.parse("2025-04-10"));

        when(exchangeRateService.convertDivisa("USD", "EUR", 100.0))
                .thenReturn(mockResponse);

        // Ejecutar solicitud HTTP y verificar respuesta
        mockMvc.perform(get("/api/convert")
                        .param("divisaSalida", "EUR")
                        .param("divisaEntrada", "USD")
                        .param("monto", "100.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.divisaEntrada", is("USD")))
                .andExpect(jsonPath("$.divisaSalida", is("EUR")))
                .andExpect(jsonPath("$.montoEntrada", is(100.0)))
                .andExpect(jsonPath("$.montoSalida", is(90.0)))
                .andExpect(jsonPath("$.tasaConversion", is(0.9)))
                .andExpect(jsonPath("$.fechaConversion", is("2025-04-10")));
    }

    @Test
    public void cuandoNoHayMonto_seRetornaUnBadRequest() throws Exception {
        // Ejecutar solicitud HTTP sin el parámetro 'amount' y verificar respuesta de error
        mockMvc.perform(get("/api/convert")
                        .param("divisaEntrada", "USD")
                        .param("divisaSalida", "EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void cuandoLaDivisaEsInvalida_retornaError() throws Exception {
        // Configurar el mock del servicio para lanzar una excepción
        when(exchangeRateService.convertDivisa(anyString(), anyString(), anyDouble()))
                .thenThrow(new TasasCambioException("Exchange rate not found for currency: XYZ"));

        // Ejecutar solicitud HTTP y verificar respuesta de error
        mockMvc.perform(get("/api/convert")
                        .param("divisaEntrada", "XYZ")
                        .param("divisaSalida", "EUR")
                        .param("monto", "100.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Exchange rate not found")));
    }

    @Test
    public void cuandoElMontoEsCero_RetornaError() throws Exception {
        // Configurar el mock del servicio para lanzar una excepción
        when(exchangeRateService.convertDivisa(anyString(), anyString(),eq(0.0)))
                .thenThrow(new TasasCambioException("Amount must be greater than zero"));

        // Ejecutar solicitud HTTP y verificar respuesta de error
        mockMvc.perform(get("/api/convert")
                        .param("divisaEntrada", "USD")
                        .param("divisaSalida", "EUR")
                        .param("monto", "0.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Amount must be greater than zero")));
    }
}