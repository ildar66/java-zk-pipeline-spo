package com.vtb.domain;

import java.sql.Date;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.spo.CRMRepayment;

import com.vtb.util.Formatter;

/**
 * Principal payment schedule data (График погашения основного долга)
 * @author Michael Kuznetsov
 */

public class PrincipalPay  extends VtbObject{
	private static final long serialVersionUID = 2L;
	private Long id; 
	private CRMRepayment periodOrder = null;
	private Date firstPayDate; 
	private Date finalPayDate; 
	private Double amount;
	private boolean depended = false; 	
	private String description;
	private String comment;
	private String currency;
	private boolean firstPay = false;
	
    public PrincipalPay() {
        super();
        periodOrder = new CRMRepayment();
    }

	public PrincipalPay(Long id) {
        super();
        this.id = id;
        periodOrder = new CRMRepayment();
    }
    
	public PrincipalPay(Long id, CRMRepayment periodOrder, Date firstPayDate, Date finalPayDate, Double amount, 
	                   boolean depended, String description, boolean firstPay, String cmnt, String currency) {
        super();
        this.id = id;
        this.periodOrder = periodOrder;
        this.firstPayDate = firstPayDate; 
        this.finalPayDate = finalPayDate; 
        this.amount = amount;
        this.depended = depended;   
        this.description = description;
        this.firstPay = firstPay;
        this.comment=cmnt;
        this.currency=currency;
        validate();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	
	/**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if(true) return;//нет проверки
        if ((periodOrder == null) || (periodOrder.getId() == null))  
            addError("График погашения основного долга. Периодичность погашения основного долга должна быть задана");
        if ((amount == null) || (amount.doubleValue() == 0.0)) 
            addError("График погашения основного долга. Сумма платежа не может быть нулевой");
        if(firstPayDate == null) 
            addError("График погашения основного долга. Дата первой оплаты ОД должна быть задана");
        if(finalPayDate == null) 
            addError("График погашения основного долга. Дата окончательного погашения ОД должна быть задана");
    }
    
	
	public String getDescription() {
	    return (description != null)?description:"";
    }

	public void setDescription(String description) {
        this.description = description;
    }
	
	public Date getFirstPayDate() {
        return firstPayDate;
    }

	public String getFirstPayDateFormatted() {
       return Formatter.sqlDateFormat(firstPayDate);
    }

	public void setFirstPayDate(Date firstPayDate) {
        this.firstPayDate = firstPayDate;
    }

    public Date getFinalPayDate() {
        return finalPayDate;
    }

    public String getFinalPayDateFormatted() {
        return Formatter.sqlDateFormat(finalPayDate);
     }
    
    public void setFinalPayDate(Date finalPayDate) {
        this.finalPayDate = finalPayDate;
    }

	public boolean isDepended() {
        return depended;
    }

    public void setDepended(boolean depended) {
        this.depended = depended;
    }

    public CRMRepayment getPeriodOrder() {
        return periodOrder;
    }

    public void setPeriodOrder(CRMRepayment periodOrder) {
        this.periodOrder = periodOrder;
    }

    public boolean isFirstPay() {
        return firstPay;
    }

    public void setFirstPay(boolean firstPay) {
        this.firstPay = firstPay;
    }

    public Double getAmount() {
        return amount;
    }

    public String getAmountAsString() {
        return (amount == null) ? "": String.valueOf(amount);
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * Answers, whether the CRMRepayment value is 'Произвольно'  
     * @return
     */
    public boolean isArbitrary() {
        if (periodOrder != null)
            if (periodOrder.getName().trim().equalsIgnoreCase("произвольно")) return true;
        return false;
    }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
    
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        int result = firstPayDate != null ? firstPayDate.hashCode() : 0;
        result = 31 * result + (finalPayDate != null ? finalPayDate.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (depended ? 1 : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (firstPay ? 1 : 0);
        return result;
    }
}
