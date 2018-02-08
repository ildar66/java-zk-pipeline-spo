package ru.md.domain;

import java.math.BigDecimal;

/**
 * @author Andrey Pavlenko
 */
public class BranchStatisticRur {

    private BigDecimal sumRur;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSumRur() {
        return sumRur;
    }

    public void setSumRur(BigDecimal sumRur) {
        this.sumRur = sumRur;
    }

    public BranchStatisticRur(BigDecimal sumRur, String name) {
        this.sumRur = sumRur;
        this.name = name;
    }
}
