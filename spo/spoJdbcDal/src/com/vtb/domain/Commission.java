package com.vtb.domain;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;

import com.vtb.util.Formatter;

/**
 * Commission - class representing commission object for Limit (Sublimit) and opportunity
 * @author Michail Kuznetsov
 */
public class Commission  extends VtbObject{
	private static final long serialVersionUID = 2L;
	private Long id;
    private String description;
	private Currency currency;
	private CommissionType name;
	private Double value;
	private PatternPaidPercentType procent_order;
	
	private PatternPaidPercentType commissionLimitPayPattern;

   public Commission() {
        super();
    }

	   
	public Commission(Long id) {
        super();
        this.id = id;
    }
    
	public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
	public Commission(Long id, String description, Currency currency, CommissionType name, Double value,
	                  PatternPaidPercentType procent_order, PatternPaidPercentType commissionLimitPayPattern) {
        super();
        this.id = id;
        this.description = description;
        this.currency = currency;
        this.name = name;
        this.value = value;
        this.procent_order = procent_order;
        this.commissionLimitPayPattern = commissionLimitPayPattern;
        validate();
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
	   //if ((currency == null) || (currency.getCode() == null)) addError("Комиссия. Валюта не определена");
	   if ((name == null) || (name.getId() == null)) addError("Комиссия. Тип комиссии не определен");
	   //if ((procent_order == null) || (procent_order.getId() == null)) addError("Комиссия. Порядок уплаты процентов не определен");
	}
	
	public String getDescription() {
	    return (description != null)?description:"";
    }

	public void setDescription(String description) {
        this.description = description;
    }
	
	public Currency getCurrency() {
	    return currency;
	}
	public String getCurrencyCode() {
	    if(currency==null) return "";
		return currency.getCode();
	}
	
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public CommissionType getName() {
		return name;
	}
	
	public void setName(CommissionType name) {
		this.name = name;
	}
	
	public Double getValue() {
		return value;
	}
	
	public String getFormattedValue() {
	    return Formatter.format(value);
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
    public PatternPaidPercentType getProcent_order() {
        return procent_order;
    }

    public void setProcent_order(PatternPaidPercentType procent_order) {
        this.procent_order = procent_order;
    }
    
    /**
     * Generates prefix for select lists in the jsp pages  
     * @return
     */
    public static String generateComTypePrefix(int j) {
        return "comType" + j;
    }
    
    public PatternPaidPercentType getCommissionLimitPayPattern() {
        return commissionLimitPayPattern;
    }

    public void setCommissionLimitPayPattern(PatternPaidPercentType commissionLimitPayPattern) {
        this.commissionLimitPayPattern = commissionLimitPayPattern;
    }
}
