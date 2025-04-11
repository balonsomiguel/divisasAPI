package com.convertidor.divisasapi.service;

import com.convertidor.divisasapi.model.ConversorResponse;
import com.convertidor.divisasapi.model.TasaCambio;

import java.util.List;

/**
 * Interfaz del servicio de cambio de divisas.
 */
public interface ConversorTasaService {

    /**
     * Obtener todas las tasas de cambio por divisa.
     *
     * @return Lista de tasas de cambio por divisa
     */
    List<TasaCambio> getAllTasasCambio();

    /**
     * Obtener tasa de cambio una divisa específica
     *
     * @param divisa codigo de divisa
     * @return tasa de cambio
     */
    TasaCambio getTasaCambio(String divisa);

    /**
     * Conversión de un divisa a otra.
     *
     * @param divisaEntrada Divisa de entrada
     * @param divisaSalida   Divisa de salida
     * @param monto       monto a convertir
     * @return resultado de la conversion
     */
    ConversorResponse convertDivisa(String divisaEntrada, String divisaSalida, double monto);

    /**
     * Refresh de las tasas de cambio obtenidas desde la API.
     */
    void refreshTasas();
}
