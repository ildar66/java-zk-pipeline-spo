package com.vtb.domain;

import ru.masterdm.compendium.domain.Currency;

import com.vtb.util.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

import java.math.BigDecimal;

/**
 * Штрафная санкция
 * @author Michail Kuznetsov
 */

public class Fine  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;// это айдишник-уникальный ключ. Не меняется. Используется как первичный ключ в БД
	private String description;
	private Currency currency;
	private Double value;
	private String valueText;
	private String punitiveMeasure;
	private Long IDpunitiveMeasure;//какую запись выбрали из справочника перед тем, как редактировать
	private String idOrg; //ссылка на организацию для поручителя
	private Long idPerson; //ссылка на физлицо для поручителя
	private String column2;//Вторая колонка для ПКР
	private String column3;//3 колонка для ПКР
	private Long id_punitive_measure;
	private Long period;
	private String periontype;
	private boolean productRateEnlarge; //признак Увеличивает ставку по сделке

   public Fine() {
        super();
    }

	
   public Fine(Long id, String description, Currency currency, Double value, String punitiveMeasure, Long idPerson, String idOrg, String valueText,Long id_punitive_measure,
		   Long period, String periodtype, boolean productRateEnlarge) {
	   super();
	   this.id = id;
	   this.description = description;
	   this.currency = currency;
	   this.value = value;
	   this.punitiveMeasure = punitiveMeasure;
	   this.idOrg = idOrg;
	   this.idPerson = idPerson;
	   this.valueText = valueText;
	   this.id_punitive_measure=id_punitive_measure;
	   this.period=period;
	   if(period!=null && period.equals(0L))
		   this.period=null;
	   this.periontype=periodtype;
	   this.productRateEnlarge=productRateEnlarge;
	   validate();
   }
	public Fine(Long id, String description, Currency currency, Double value, String punitiveMeasure, Long idPerson, String idOrg) {
        super();
        this.id = id;
        this.description = description;
        this.currency = currency;
        this.value = value;
        this.punitiveMeasure = punitiveMeasure;
        this.idOrg = idOrg;
        this.idPerson = idPerson;
        validate();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if ((punitiveMeasure == null) || (punitiveMeasure.trim().equals(""))) addError("Штрафная санкция. Тип штрафной санкции не определен");
        //if ((currency == null) || (currency.getCode() == null)) addError("Штрафная санкция. Валюта не определена");
        //if ((value == null) || (value.doubleValue() == 0.0))  
        //    addError("Штрафная санкция. Сумма (% ставка) не должна быть равной нулю");
           
    }
    
	public String getDescription() {
		if(valueText!=null)
			return valueText;
	    if(description==null)
	        return "";
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Currency getCurrency() {
		return currency;
	}
	public String getCurrencyCode() {
		if(currency==null) return "";
        if(currency.getCode()==null) return "";
        return currency.getCode();
    }
	public void setCurrency(Currency currency) {
		this.currency = currency;
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
	public String getPunitiveMeasure() {
		return punitiveMeasure;
	}
	public void setPunitiveMeasure(String punitiveMeasure) {
		this.punitiveMeasure = punitiveMeasure;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Fine(Long id) {
		super();
		this.id = id;
	}


    public Long getIDpunitiveMeasure() {
        return IDpunitiveMeasure;
    }


    public void setIDpunitiveMeasure(Long iDpunitiveMeasure) {
        IDpunitiveMeasure = iDpunitiveMeasure;
    }


	public String getIdOrg() {
	    if(idOrg==null)
	        return null;
	    if(idOrg=="" || idOrg.equals("null"))
	        return null;
		return idOrg;
	}


	public void setIdOrg(String idOrg) {
		this.idOrg = idOrg;
	}


	public String getValueText() {
		return valueText;
	}


	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public Long getIdPerson() {
	    if(idPerson!=null && idPerson.equals(0L))
	        idPerson=null;
		return idPerson;
	}

	public void setIdPerson(Long idPerson) {
		this.idPerson = idPerson;
	}
	
	public String generateColumn3(){
		column3 = "";
		if(period==null)
			return "";
		column3 = "Период оплаты неустойки: "+Formatter.format(getPeriod())+" ";
		if(getPeriontype().equals("workdays")) column3+="рабочих дней";
		if(getPeriontype().equals("alldays")) column3+="календарных дней";
		return column3;
	}

	public String generateColumn2(){
		String description = getDescription();
	    if (description == null || description.isEmpty())
	    	description = "";
	    else 
	    	description += " ";
		
    	String formated = getFormattedValue();

		try{
			column2 = description + SBeanLocator.singleton().getDictService().moneyDisplay(new BigDecimal(value),getCurrencyCode());
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
			column2 = description;
		}

    	
    	if (formated != null && !formated.isEmpty()) {
    		if (getCurrencyCode() != null){
    			if (formated.endsWith(",00")) formated = formated.substring(0, formated.length() - 3);
    			if (getCurrencyCode().equals("1/365")) column2 = description + formated + "/365";
    			if (getCurrencyCode().equals("1/366")) column2 = description + formated + "/366";
    		}
    	}
	    return column2;
	}


	public Long getId_punitive_measure() {
		return id_punitive_measure;
	}


	public void setId_punitive_measure(Long id_punitive_measure) {
		this.id_punitive_measure = id_punitive_measure;
	}


	public Long getPeriod() {
		return period;
	}
	public String getFormattedPeriod() {
        return Formatter.format(period);
    }


	public void setPeriod(Long period) {
		this.period = period;
	}


	public String getPeriontype() {
		return periontype==null?"":periontype;
	}


	public void setPeriontype(String periontype) {
		this.periontype = periontype;
	}


	public boolean isProductRateEnlarge() {
		return productRateEnlarge;
	}


	public void setProductRateEnlarge(boolean productRateEnlarge) {
		this.productRateEnlarge = productRateEnlarge;
	}

}