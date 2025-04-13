package com.convertidor.divisasapi.service.impl;

import com.convertidor.divisasapi.exception.TasasCambioException;
import com.convertidor.divisasapi.model.ConversorResponse;
import com.convertidor.divisasapi.model.TasaCambio;
import com.convertidor.divisasapi.model.TasaCambioResponse;
import com.convertidor.divisasapi.service.ConversorTasaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ConversorTasaServiceImpl implements ConversorTasaService {

    private static final Logger logger = LoggerFactory.getLogger(ConversorTasaServiceImpl.class);
    private static final String BASE_CURRENCY = "EUR"; // La API tiene la BASE EUR

    private final RestTemplate restTemplate;

    @Value("${exchange.api.url}")
    private String exchangeApiUrl;

    @Autowired
    public ConversorTasaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches all exchange rates from the cache or external API.
     */
    @Override
    @Cacheable(value = "tasaCambio") //--> se había cambiado variable de cacheo durante las pruebas
    public List<TasaCambio> getAllTasasCambio() {
        logger.info("Consultado todas las tasas de la API exchangeratesapi.io");

        try {
            ResponseEntity<TasaCambioResponse> response =
                    restTemplate.getForEntity(exchangeApiUrl, TasaCambioResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Fallo en la consulta de tasas. Status: {}", response.getStatusCode());
                throw new TasasCambioException("Failed to fetch exchange rates from external API");
            }

            TasaCambioResponse rateResponse = response.getBody();

            // Varifica si el request fue exitoso
            if (!rateResponse.isSuccess()) {
                String errorMessage = rateResponse.getError() != null ?
                        rateResponse.getError().getInfo() : "Unknown error";
                logger.error("La API retorno un valor de: {}", errorMessage);
                throw new TasasCambioException("Exchange rate API error: " + errorMessage);
            }

            // Se añade la divisa base a la lista de tasas
            List<TasaCambio> rates = new ArrayList<>();
            rates.add(new TasaCambio("EUR", 1.0)); // Como la base es EUR --> 1.0

            // Se añaden las otras tasas
            for (Map.Entry<String, Double> entry : rateResponse.getRates().entrySet()) {
                rates.add(new TasaCambio(entry.getKey(), entry.getValue()));
            }

            logger.info("Consulta exitosa {} tasas de cambio encontradas", rates.size());
            return rates;

        } catch (RestClientException e) {
            logger.error("Ocurrio un error mientras se consultaban las tasas de la API externa", e);
            throw new TasasCambioException("Failed to connect to exchange rate API: " + e.getMessage());
        }
    }

    /**
     * Obtener una tasa de cambio de una divisa en específico
     */
    @Override
    public TasaCambio getTasaCambio(String divisa) {
        logger.info("Obteniendo tasa de cambio de divisa: {}", divisa);

        // Se valida formato ISO 4217
        if (!isValidCodigoDivisa(divisa)) {
            logger.error("Formato inválido de codigo de divisa: {}", divisa);
            throw new TasasCambioException("Invalid currency code format. Must be in ISO 4217 format (3 letters): " + divisa);
        }

        // Se ponen todas las divisas en uppercase
        final String upperCaseDivisa = divisa.toUpperCase();

        List<TasaCambio> listaTasas = getAllTasasCambio();

        return listaTasas.stream()
                .filter(tasa -> tasa.getDivisa().equalsIgnoreCase(upperCaseDivisa))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Tasa de cambio no encontrada para divisa: {}", upperCaseDivisa);
                    return new TasasCambioException("Exchange rate not found for currency: " + upperCaseDivisa);
                });
    }

    /**
     * Valida si cumple con el formato ISO 4217 (3 letras).
     */
    private boolean isValidCodigoDivisa(String codigo) {
        return codigo != null && codigo.matches("[A-Za-z]{3}");
    }

    /**
     * Convierte de un monto de divisa a otro
     */
    @Override
    public ConversorResponse convertDivisa(String divisaEntrada, String divisaSalida, double monto) {
        logger.info("Convirtiendo {} {} a {}", monto, divisaEntrada, divisaSalida);

        // Validación de monto
        if (monto <= 0) {
            logger.error("Monto invalido: {}", monto);
            throw new TasasCambioException("Amount must be greater than zero");
        }

        // Obtensión de las tasas de cambio
        TasaCambio tasaEntrada = getTasaCambio(divisaEntrada);
        TasaCambio tasaSalida = getTasaCambio(divisaSalida);

        // Calculo de la tasa de cambio
        double tasaCambio = tasaSalida.getTasa() / tasaEntrada.getTasa();

        // Calculo del monto final convertido
        double montoConvertido = monto * tasaCambio;

        // Monto convertido resondeado a 2 decimales
        BigDecimal redondeado = BigDecimal.valueOf(montoConvertido).setScale(2, RoundingMode.HALF_UP);

        logger.info("Resultado de la conversion: {} {} = {} {}",
                monto, divisaEntrada, redondeado.doubleValue(), divisaSalida);

        // Respuesta de la conversion
        ConversorResponse response = new ConversorResponse(
                divisaEntrada,
                divisaSalida,
                monto,
                redondeado.doubleValue(),
                tasaCambio
        );

        // Obtener fecha desde la API
        TasaCambioResponse apiResponse = restTemplate.getForObject(exchangeApiUrl, TasaCambioResponse.class);
        if (apiResponse != null && apiResponse.getDate() != null) {
            try {
                LocalDate date = LocalDate.parse(apiResponse.getDate(), DateTimeFormatter.ISO_DATE);
                response.setFechaConversion(date);
            } catch (Exception e) {
                logger.warn("No se obtuvo la fecha de la API: {}", apiResponse.getDate());
            }
        }

        return response;
    }

    /**
     * Refresca valores de tasas.
     */
    @Override
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void refreshTasas() {
        logger.info("Refresh de tasas en cache");
    }
}
