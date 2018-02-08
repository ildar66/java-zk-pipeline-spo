package com.vtb.domain;

import java.sql.Date;

import ru.masterdm.compendium.domain.Currency;

import com.vtb.util.Formatter;

/**
 * Payment Schedule ('График платежей')
 * @author Michael Kuznetsov
 */

public class PaymentSchedule  extends VtbObject{
	private static final long serialVersionUID = 2L;
	private Long id;
	private Long period;
	private Double FONDRATE;//Ставка фондирования по периоду платежа
	private Double amount;
	private Date fromDate; 
	private Date toDate;
	private Currency currency;
	private String currencyText;
	private boolean manualFondrate=true;
	private Long tranceId;
	private String desc;
	private String comBase;

   public PaymentSchedule() {
        super();
    }

	public PaymentSchedule(Long id) {
        super();
        this.id = id;
    }
    
	public PaymentSchedule(Long id, Double amount, Double FONDRATE, 
			               Date fromDate,Date toDate, String cur, Long paymentPeriod, 
			               boolean manualFondrate, Long tranceId, String pmn_desc, String comBase) {
        super();
        this.id = id;
        this.amount = amount;
        this.fromDate = fromDate; 
        this.toDate = toDate;
        this.currency = new Currency(cur);
        this.currencyText = (cur == null)?"":cur;
        this.FONDRATE=FONDRATE;
        this.manualFondrate=manualFondrate;
        period=paymentPeriod;
        this.tranceId=tranceId;
        desc = pmn_desc;
        this.comBase = comBase;
        if(tranceId!=null && tranceId.longValue()==0)
        	this.tranceId=null;
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
//        if ((amount == null) || (amount.doubleValue() == 0.0))  
//            addError("График платежей. Сумма платежа не должна быть равной нулю");
//        if (fromDate == null) addError("График платежей. Перидо оплаты (с даты) должен быть задан");   
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

    public Date getFromDate() {
        return fromDate;
    }

    public String getFromDateFormatted() {
        return Formatter.sqlDateFormat(fromDate);
     }
    
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public String getToDateFormatted() {
        return Formatter.sqlDateFormat(toDate);
     }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getFONDRATE() {
        return FONDRATE;
    }

    public void setFONDRATE(Double fONDRATE) {
        FONDRATE = fONDRATE;
    }

    public Long getPeriod() {
    	return period;
    }
	public String getPeriodStr() {
		if(period==null)
			return "";
		return period.toString();
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public boolean isManualFondrate() {
		return manualFondrate;
	}

	public void setManualFondrate(boolean manualFondrate) {
		this.manualFondrate = manualFondrate;
	}

	public Long getTranceId() {
		return tranceId;
	}

	public void setTranceId(Long tranceId) {
		this.tranceId = tranceId;
	}

	public String getDesc() {
		return desc==null?" ":desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getComBase() {
		if (comBase == null) {
			return "";
		}
		return comBase.trim();
	}

	public void setComBase(String comBase) {
		this.comBase = comBase;
	}

	public String getCurrencyText() {
		return currencyText;
	}

	public void setCurrencyText(String currencyText) {
		this.currencyText = currencyText;
	}

    @Override
    public int hashCode() {
        int result = period != null ? period.hashCode() : 0;
        result = 31 * result + (FONDRATE != null ? FONDRATE.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (fromDate != null ? fromDate.hashCode() : 0);
        result = 31 * result + (toDate != null ? toDate.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (comBase != null ? comBase.hashCode() : 0);
        return result;
    }
}
