package com.vtb.domain;

import javax.persistence.Transient;

import com.vtb.util.Formatter;

/**
 * Premium, transferred from PremiumJPA.
 * Вознаграждения для процентной ставки.
 */

public class Premium extends VtbObject {
    private static final long serialVersionUID = 1L;

    private String premiumType; // Вознаграждения. Тип (из справочника) 
    private String premiumTypeValue;//Вознаграждения. Тип типа (из справочника): Формула, Валюта, иное...
    private Double premiumValue;//Вознаграждения величина
    private String premiumCurr;//Вознаграждения валюта
    private String premiumText;//Вознаграждения формула
    private String premiumForPrint; // Вознаграждения: скомибинированное значение для печати.
    
    public Premium() {
    	super();
    }
    
    /**
     * Конструктор
     * @param premiumType
     * @param premiumTypeValue
     * @param premiumvalue
     * @param premiumcurr
     * @param premiumtext
     */
	public Premium(String premiumType, String premiumTypeValue, Double premiumvalue, String premiumcurr, String premiumtext) {
		super();
		this.premiumType = premiumType;
		this.premiumTypeValue = premiumTypeValue;
		this.premiumValue = premiumvalue;
		this.premiumCurr = premiumcurr;
		this.premiumText = premiumtext;
		this.premiumForPrint = generatePremiumForPrint();
	}
	public String getPremiumType() {
		return premiumType;
	}
	public void setPremiumType(String premiumType) {
		this.premiumType = premiumType;
	}
	public Double getPremiumvalue() {
		return premiumValue;
	}
	public void setPremiumvalue(Double premiumvalue) {
		this.premiumValue = premiumvalue;
	}
	public String getPremiumcurr() {
		return premiumCurr;
	}
	public void setPremiumcurr(String premiumcurr) {
		this.premiumCurr = premiumcurr;
	}
	public String getPremiumtext() {
		return premiumText;
	}
	public void setPremiumtext(String premiumtext) {
		this.premiumText = premiumtext;
	}
	public String getPremiumTypeValue() {
		return premiumTypeValue;
	}
	public void setPremiumTypeValue(String premiumTypeValue) {
		this.premiumTypeValue = premiumTypeValue;
	}
	public String getPremiumForPrint() {
		return premiumForPrint;
	}
	public void setPremiumForPrint(String premiumForPrint) {
		this.premiumForPrint = premiumForPrint;
	}
	
    /**
     * Соберем в текстовое значение поля вознаграждения в зависимости от типа вознаграждения (из справочника)
     * @return сгенерированное текстовое значение для вознаграждения 
     */
	@Transient
    private String generatePremiumForPrint() {
    	String type = premiumTypeValue;
    	if (type == null) return null;
    	if (type.toLowerCase().indexOf("валюта") > -1) {
    		return Formatter.toMoneyFormat(premiumValue) + " " + premiumCurr;	
    	}
    	if (type.toLowerCase().indexOf("формула") > -1) {
    		return premiumText;	
    	}
    	return premiumTypeValue;  // это для иных значений (которые -- не формула, и не валюта с процентами)
    }
}
