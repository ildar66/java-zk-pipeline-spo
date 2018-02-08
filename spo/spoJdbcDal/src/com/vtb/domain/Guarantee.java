package com.vtb.domain;

import java.math.BigDecimal;
import java.util.Date;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.Person;

import com.vtb.util.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

public class Guarantee extends AbstractSupply{
	private static final long serialVersionUID = 1L;

	private BigDecimal sum;// Сумма гарантии
	private Currency currency;//Валюта гарантии
	private String description; // Описание
	private Double transRisk;   // коэффициент транзакционного риска 
	private Date date;          //срок гарантии
    private String printedSum;  // Предел ответственности для отображения в отчете. нет setter, нет getter
    private String printedFullSum;//На всю сумму обязательств

	private boolean fullSum = false;//На всю сумму обязательств 
	public String getCode(){
		return getOrg()==null?getPerson().getId().toString():getOrg().getId();
	}

	/**
	 * @return the Сумма гарантии
	 */
	public BigDecimal getSum() {
		return sum==null?new BigDecimal(0):sum;
	}
	/**
	 * @param sum Сумма гарантии
	 */
	public void setSum(BigDecimal sum) {
		this.sum = sum;
		if (fullSum) {
            printedSum = null;
            printedFullSum = "Y";
        }
        else {
			String curCode = currency==null?"":currency.getCode();
            printedSum = SBeanLocator.singleton().getDictService().moneyDisplay(sum, curCode);
            printedFullSum = "N";
        }
	}
	/**
	 * @return the Валюта гарантии
	 */
	public Currency getCurrency() {
		return currency;
	}
	/**
	 * @param currency Валюта гарантии
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	/**
	 * @return Описание
	 */
	public String getDescription() {
		return description==null?"":description;
	}
	/**
	 * @param description Описание
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * @return коэффициент транзакционного риска
     */
	public Double getTransRisk() {
        return transRisk;
    }
	
	/**
     * @param transRisk коэффициент транзакционного риска
     */
	public void setTransRisk(Double transRisk) {
        this.transRisk = transRisk;
    }
	@Override
	public BigDecimal getRating_zalog(BigDecimal exchangeRate, BigDecimal mainSum) {
	    if (currency==null||currency.getCode()==null|| sum==null)return null;
		if (currency.getCode().equals("RUR"))return sum;
		if (exchangeRate==null)return null;
		return exchangeRate.multiply(sum);
	}
	@Override
	public String getSupplyType() {
		return "g";
	}
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public boolean isFullSum() {
        return fullSum;
    }
    public void setFullSum(boolean fullSum) {
        this.fullSum = fullSum;
        if (fullSum) {
            printedSum = null;
            printedFullSum = "Y";
        }
        else {
			String curCode = currency==null?"":currency.getCode();
			printedSum = SBeanLocator.singleton().getDictService().moneyDisplay(sum, curCode);
            printedFullSum = "N";
        }
    }
	@Override
	public String getSupplyTypeName() {
		return "Гарант";
	}
}
