package ru.md.domain.dashboard;

/**
 * Created by Andrey Pavlenko on 28.09.2016.
 */
public class Sum {
    private String currency;
    private Double value;

    public Sum(String currency, Double value) {
        this.currency = currency;
        this.value = value;
    }

    public Sum() {
    }

    /**
     * Returns .
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets .
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Returns .
     * @return
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets .
     * @param value
     */
    public void setValue(Double value) {
        this.value = value;
    }
}
