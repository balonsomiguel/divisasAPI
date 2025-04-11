package com.convertidor.divisasapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Response model for the exchangeratesapi.io API.
 * Structure matches the response from http://api.exchangeratesapi.io/v1/latest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TasaCambioResponse {

    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;

    @JsonProperty("error")
    private ExchangeRateError error;

    public TasaCambioResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public ExchangeRateError getError() {
        return error;
    }

    public void setError(ExchangeRateError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "TasaCambioResponse{" +
                "success=" + success +
                ", timestamp=" + timestamp +
                ", base='" + base + '\'' +
                ", date='" + date + '\'' +
                ", rates=" + rates +
                ", error=" + error +
                '}';
    }

    /**
     * Inner class to handle error responses from the API
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRateError {
        private String code;
        private String type;
        private String info;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "TasaCambioError{" +
                    "code='" + code + '\'' +
                    ", type='" + type + '\'' +
                    ", info='" + info + '\'' +
                    '}';
        }
    }
}
