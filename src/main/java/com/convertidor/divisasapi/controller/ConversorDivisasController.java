package com.convertidor.divisasapi.controller;

import com.convertidor.divisasapi.exception.TasasCambioException;
import com.convertidor.divisasapi.model.ConversorResponse;
import com.convertidor.divisasapi.service.ConversorTasaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST API de conversión de divisas.
 */
@RestController
@RequestMapping("/api/convert")
public class ConversorDivisasController {

    private static final Logger logger = LoggerFactory.getLogger(ConversorDivisasController.class);

    private final ConversorTasaService exchangeRateService;

    @Autowired
    public ConversorDivisasController(ConversorTasaService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Convierte de un valor de divisa a otro.
     *
     * @param monto Monto a convertir
     * @param divisaEntrada Divisa original de entrada
     * @param divisaSalida Divisa original de salida
     * @return Resultado de la conversión
     */
    @GetMapping
    public ResponseEntity<ConversorResponse> convertCurrency(
            @RequestParam double monto,
            @RequestParam String divisaEntrada,
            @RequestParam String divisaSalida) {

        logger.info("Petición para convertir {} {} to {}", monto, divisaEntrada, divisaSalida);

        // Valida entradas
        if (monto <= 0) {
            logger.error("Monto inválido de conversión: {}", monto);
            throw new TasasCambioException("Amount must be greater than zero");
        }

        if (divisaEntrada == null || divisaEntrada.trim().isEmpty()) {
            logger.error("Divisa de entrada no ingresada o nula");
            throw new TasasCambioException("Source currency is required");
        }

        if (divisaSalida == null || divisaSalida.trim().isEmpty()) {
            logger.error("Divisa de salida no ingresada o nula");
            throw new TasasCambioException("Target currency is required");
        }

        // Ejecuta la conversión
        ConversorResponse response = exchangeRateService.convertDivisa(
                divisaEntrada.trim(),
                divisaSalida.trim(),
                monto
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Se ingresa opcionalmente una funcion de refresh para ser implementada.
     *
     * @return Mensaje de refresco exitoso
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshRates() {
        logger.info("Se recibe petición de refresco de tasas");
        exchangeRateService.refreshTasas();
        return ResponseEntity.ok("Exchange rates refreshed successfully");
    }
}
