package com.convertidor.divisasapi.model;

import java.util.Objects;

/**
 * Model class representing an exchange rate for a currency.
 */
public class TasaCambio {

    private String divisa;
    private double tasa;

    public TasaCambio() {
    }

    public TasaCambio(String divisa, double tasa) {
        this.divisa = divisa;
        this.tasa = tasa;
    }

    public String getDivisa() {
        return divisa;
    }

    public void setDivisa(String divisa) {
        this.divisa = divisa;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
            this.tasa = tasa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasaCambio that = (TasaCambio) o;
        return Double.compare(that.tasa, tasa) == 0 && Objects.equals(divisa, that.divisa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(divisa, tasa);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "currency='" + divisa + '\'' +
                ", rate=" + tasa +
                '}';
    }
}
