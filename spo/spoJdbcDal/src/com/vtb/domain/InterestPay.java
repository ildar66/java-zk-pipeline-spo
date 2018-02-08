package com.vtb.domain;

import java.sql.Date;

import com.vtb.util.Formatter;

import ru.masterdm.compendium.domain.spo.CRMInterestPay;

/**
 * Interest payment schedule data (График погашения процентов по кредиту)
 * @author Michael Kuznetsov
 */

public class InterestPay  extends VtbObject{
	private static final long serialVersionUID = 2L;
	private Long id; 
	private Date firstPayDate; 
	private Date finalPayDate; 
	private Long numDay;
	private boolean finalPay = false; 	
	private String description;
	private String comment;
	private String pay_int;//Периодичность
	private String firstPayDateComment;//коментарий к дате первой оплаты
	
	public InterestPay() {
        super();
    }
	
	public InterestPay(Long id) {
        super();
        this.id = id;
    }
    
	public InterestPay(Long id, Date firstPayDate, Date finalPayDate, Long numDay, 
	                   boolean finalPay, String description,String pay_int, String cmnt, String firstPayDateComment) {
        super();
        this.id = id;
        this.firstPayDate = firstPayDate; 
        this.finalPayDate = finalPayDate; 
        this.numDay = numDay;
        this.finalPay = finalPay;   
        this.description = description;
        this.pay_int=pay_int;
        this.comment=cmnt;
        this.firstPayDateComment = firstPayDateComment;
        validate();
    }

	public String getFirstPayDateComment() {
		return firstPayDateComment;
	}

	public void setFirstPayDateComment(String firstPayDateComment) {
		this.firstPayDateComment = firstPayDateComment;
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
//        if ((periodOrder == null) || (periodOrder.getId() == null))  
//            addError("График погашения процентов по кредиту. Периодичность погашения должна быть задана");
//        if ((numDay == null) || (numDay.longValue() <1) || (numDay.longValue() >31)) 
//            addError("График погашения процентов по кредиту. Число уплаты процентов должно быть в интервале от 1 до 31 включительно");
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

	public boolean isFinalPay() {
        return finalPay;
    }

    public void setFinalPay(boolean finalPay) {
        this.finalPay = finalPay;
    }

    public Long getNumDay() {
        return numDay;
    }

    public String getNumDayAsString() {
        return (numDay == null) ? "": String.valueOf(numDay);
    }
    
    public void setNumDay(Long numDay) {
        this.numDay = numDay;
    }

	/**
	 * @return the pay_int
	 */
	public String getPay_int() {
		return pay_int==null?"":pay_int;
	}

	/**
	 * @param pay_int the pay_int to set
	 */
	public void setPay_int(String pay_int) {
		this.pay_int = pay_int;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

    @Override
    public int hashCode() {
        int result = firstPayDate != null ? firstPayDate.hashCode() : 0;
        result = 31 * result + (finalPayDate != null ? finalPayDate.hashCode() : 0);
        result = 31 * result + (numDay != null ? numDay.hashCode() : 0);
        result = 31 * result + (finalPay ? 1 : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (pay_int != null ? pay_int.hashCode() : 0);
        result = 31 * result + (firstPayDateComment != null ? firstPayDateComment.hashCode() : 0);
        return result;
    }
}
