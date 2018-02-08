package ru.md.domain;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.commons.lang3.StringUtils;

import ru.md.domain.dict.ComissionDimension;
import ru.md.domain.dict.ComissionPeriod;
import ru.md.domain.dict.CommonDictionary;

/**
 * Комиссия.
 * @author Sergey Valiev
 */
public class Comission {

    private Long id;
    private CommonDictionary<String> type;
    private CommonDictionary<String> period;
    private String currency;
    private BigDecimal value;
    private BigDecimal currencyRate;
    private MdTask mdTask;
    
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
     * Возвращает тип.
     * @return тип
     */
    public CommonDictionary<String> getType() {
        return type;
    }
    
    /**
     * Устанавливает тип.
     * @param type тип
     */
    public void setType(CommonDictionary<String> type) {
        this.type = type;
    }
    
    /**
     * Возвращает период.
     * @return период
     */
    public CommonDictionary<String> getPeriod() {
        return period;
    }
    
    /**
     * Устанавливает период.
     * @param period период
     */
    public void setPeriod(CommonDictionary<String> period) {
        this.period = period;
    }
    
    /**
     * Возвращает валюта.
     * @return период
     */
    public String getCurrency() {
        return currency;
    }
    
    /**
     * Устанавливает период.
     * @param currency период
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    /**
     * Возвращает значение.
     * @return значение
     */
    public BigDecimal getValue() {
        return value;
    }
    
    /**
     * Устанавливает значение.
     * @param value значение
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    /**
     * Возвращает курс.
     * @return курс
     */
    public BigDecimal getCurrencyRate() {
        return currencyRate;
    }
    
    /**
     * Устанавливает курс.
     * @param currencyRate курс
     */
    public void setCurrencyRate(BigDecimal currencyRate) {
        this.currencyRate = currencyRate;
    }

    /**
     * Возвращает заявка.
     * @return заявка
     */
    public MdTask getMdTask() {
        return mdTask;
    }

    /**
     * Устанавливает заявка.
     * @param mdTask заявка
     */
    public void setMdTask(MdTask mdTask) {
        this.mdTask = mdTask;
    }
    
    /**
     * Возвращает комиссия в % годовых.
     * @return комиссия в % годовых
     */
    public BigDecimal getAnnualValue() {
        if (StringUtils.isEmpty(currency)
                || value == null)
            return null;
        ComissionDimension comissionDimension = ComissionDimension.find(currency);
        if (ComissionDimension.ANNUAL_PERCENT.equals(comissionDimension))
            return value;
        
        ComissionPeriod comissionPeriod;
        if (period == null
                || (comissionPeriod = ComissionPeriod.find(period.getName())) == null)
            return null;
        
        if (ComissionDimension.PERCENT.equals(comissionDimension)) {
            if (comissionPeriod.getCoeff() != null)
                return value.multiply(BigDecimal.valueOf(comissionPeriod.getCoeff()));
            else {
                BigDecimal periodInYears;
                if (mdTask == null
                        || (periodInYears = mdTask.getPeriodInYears()) == null
                        || periodInYears.equals(BigDecimal.ZERO))
                    return null;
                return value.divide(periodInYears, MathContext.DECIMAL128);
            }
        } else {
            BigDecimal sum;
            BigDecimal currentCurrencyRate;
            if (currencyRate == null
                    || mdTask == null
                    || (sum = mdTask.getMdTaskSumCalculated()) == null
                    || (currentCurrencyRate = mdTask.getCurrentCurrencyRate()) == null)
                return null;
            
            if (comissionPeriod.getCoeff() == null) {
                BigDecimal periodInYears = mdTask.getPeriodInYears();
                if (periodInYears == null || periodInYears.equals(BigDecimal.ZERO))
                    return null;
                
                return value.multiply(currencyRate)
                            .multiply(BigDecimal.TEN.pow(2))
                            .divide(sum.multiply(currentCurrencyRate)
                                       .multiply(periodInYears), MathContext.DECIMAL128);
            } else 
                return value.multiply(currencyRate)
                            .multiply(BigDecimal.TEN.pow(2))
                            .multiply(BigDecimal.valueOf(comissionPeriod.getCoeff()))
                            .divide(sum.multiply(currentCurrencyRate), MathContext.DECIMAL128);
            
        }
    }
}
