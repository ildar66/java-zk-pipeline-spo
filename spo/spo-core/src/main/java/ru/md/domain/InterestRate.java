package ru.md.domain;

import java.math.BigDecimal;

/**
 * Процентная ставка
 * @author Sergey Valiev
 */
public class InterestRate {

    private Long id;
    private BigDecimal loanRate;
    private BigDecimal fundingRate;
    private boolean interestRateFixed;
    private boolean interestRateDerivative;
    
    /**
     * Возвращает идентификатор.
     * @return идентификатор
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Устанавливает идентификатор.
     * @param id идентификатор
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Возвращает ставка размещения.
     * @return ставка размещения
     */
    public BigDecimal getLoanRate() {
        return loanRate;
    }
    
    /**
     * Устанавливает ставка размещения.
     * @param loanRate ставка размещения
     */
    public void setLoanRate(BigDecimal loanRate) {
        this.loanRate = loanRate;
    }

    /**
     * Возвращает ставка фондирования.
     * @return ставка фондирования
     */
    public BigDecimal getFundingRate() {
        return fundingRate;
    }

    /**
     * Устанавливает ставка фондирования.
     * @param fundingRate ставка фондирования
     */
    public void setFundingRate(BigDecimal fundingRate) {
        this.fundingRate = fundingRate;
    }
    /**
     * Возвращает тип ставки фиксированная.
     * @return <code>true</code> если ставка фиксированная
     */
    public boolean isInterestRateFixed() {
        return interestRateFixed;
    }

    /**
     * Устанавливает тип ставки.
     * @param fixedRate тип ставки
     */
    public void setInterestRateFixed(boolean interestRateFixed) {
        this.interestRateFixed = interestRateFixed;
    }

    /**
     * Возвращает тип ставки плавающая.
     * @return <code>true</code> если ставка плавающая
     */
    public boolean isInterestRateDerivative() {
        return interestRateDerivative;
    }

    /**
     * Устанавливает тип ставки.
     * @param fixedRate тип ставки
     */
    public void setInterestRateDerivative(boolean interestRateDerivative) {
        this.interestRateDerivative = interestRateDerivative;
    }
}
