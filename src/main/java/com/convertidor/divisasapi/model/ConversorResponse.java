package com.convertidor.divisasapi.model;

import java.time.LocalDate;


public class ConversorResponse {

    private String divisaEntrada;
    private String divisaSalida;
    private double montoEntrada;
    private double montoSalida;
    private double tasaConversion;
    private LocalDate fechaConversion;

    public ConversorResponse() {
        this.fechaConversion = LocalDate.now();
    }

    public ConversorResponse(String divisaEntrada, String divisaSalida,
                              double montoEntrada, double montoSalida,
                              double tasaConversion) {
        this.divisaEntrada = divisaEntrada;
        this.divisaSalida = divisaSalida;
        this.montoEntrada = montoEntrada;
        this.montoSalida = montoSalida;
        this.tasaConversion = tasaConversion;
        this.fechaConversion = LocalDate.now();
    }

    public String getDivisaEntrada() {
        return divisaEntrada;
    }

    public void setDivisaEntrada(String divisaEntrada) {
        this.divisaEntrada = divisaEntrada;
    }

    public String getDivisaSalida() {
        return divisaSalida;
    }

    public void setDivisaSalida(String divisaSalida) {
        this.divisaSalida = divisaSalida;
    }

    public double getMontoEntrada() {
        return montoEntrada;
    }

    public void setMontoEntrada(double montoEntrada) {
        this.montoEntrada = montoEntrada;
    }

    public double getMontoSalida() {
        return montoSalida;
    }

    public void setMontoSalida(double montoSalida) {
        this.montoSalida = montoSalida;
    }

    public double getTasaConversion() {
        return tasaConversion;
    }

    public void setTasaConversion(double tasaConversion) {
        this.tasaConversion = tasaConversion;
    }

    public LocalDate getFechaConversion() {
        return fechaConversion;
    }

    public void setFechaConversion(LocalDate fechaConversion) {
        this.fechaConversion = fechaConversion;
    }

    @Override
    public String toString() {
        return "ConversorResponse{" +
                "divisaEntrada='" + divisaEntrada + '\'' +
                ", divisaSalida='" + divisaSalida + '\'' +
                ", montoEntrada=" + montoEntrada +
                ", montoSalida=" + montoSalida +
                ", tasaConversion=" + tasaConversion +
                ", fechaConversion=" + fechaConversion +
                '}';
    }
}
