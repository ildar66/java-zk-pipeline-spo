package ru.md.domain;

import java.math.BigDecimal;

/**
 * @author Andrey Pavlenko
 */
public class BranchStatistic {

    private BigDecimal sum;
    private String name;
    private String currency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSum() {
        return sum;
    }

    /**
     * Sets .
     * @param sum
     */
    public void setSum(BigDecimal sum) {
        this.sum = sum;
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
}
