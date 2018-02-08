package com.vtb.domain;

import ru.masterdm.spo.utils.Formatter;

/**
 * VtbObject "условия досрочного погашения"
 * @author Andrey Pavlenko
 *
 */
public class EarlyPayment extends VtbObject  {
    private static final long serialVersionUID = 1L;
    private String permission;//Разрешение досроч.погаш.  (ссылка на справочник)
    private String permissionValue;//Разрешение досроч.погаш.  (значение из справочника)
    private String commission = "N";//Взимание комиссии досроч.погаш.
    private String commissionPrint = "";//Взимание комиссии досроч.погаш.
    private String periodType = "allDays";//Тип периода, календарные или рабочие дни.
    private Long daysBeforeNotifyBank = 5L;//За сколько дней Заемщик должен уведомить Банк о досрочном погашении
    
    

    private String condition;//Условие досрочного погашения
    
    public EarlyPayment() {
        super();
    }
    
    public EarlyPayment(String permission, String permissionValue, String commission, String condition, String periodType, Long daysBeforeNotifyBank) {
        super();
        this.permission = permission;
        this.permissionValue = permissionValue;
        this.condition = condition;
        this.periodType = periodType;
        this.daysBeforeNotifyBank = daysBeforeNotifyBank;
        setCommission(commission);
    }

    public String getPermission() {
        return permission;
    }
    public void setPermission(String permission) {
        this.permission = permission;
    }
    public String getCommission() {
        return commission;
    }
    public void setCommission(String commission) {
        this.commission = commission;
        if("n".equalsIgnoreCase(commission)) commissionPrint = "комиссия не взимается";
        else if("y".equalsIgnoreCase(commission)) commissionPrint = "комиссия взимается";
        else commissionPrint = "";
        //ПКР
//        commissionPrint = getPermissionValue() + " " + commissionPrint + " " + getCondition() + "(periodType=" + periodType + ", daysBeforeNotifyBank=" + daysBeforeNotifyBank.toString() + ")";
        commissionPrint = createCommisiionForReport(getPermissionValue(), commissionPrint, getCondition(), periodType, daysBeforeNotifyBank);
    }
    
    private String createCommisiionForReport(String permission, String print, String condition, String typePeriod, Long period) {
    	String result = "";
    	if (permission == null)
    		return result;
    	result = permission;
    	if (print != null && !print.isEmpty())
    		result += (", " + print);
    	if (condition != null && !condition.isEmpty())
    		result += (" " + condition);
    	if (period == null || typePeriod == null)
    		return result;
    	if (typePeriod.equalsIgnoreCase("alldays"))
            typePeriod = " календарных дней";
    	else if (typePeriod.equalsIgnoreCase("workdays"))
            typePeriod = " рабочих дней";
    	else
    		return result;
    	result += ("\n" + "Заемщик уведомляет Банк о досрочном погашении за " + period.toString() + typePeriod);
    	return result;
    }
    
    public String getCondition() {
        return Formatter.cut(condition, 4000);
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getPermissionValue() {
        return permission==null?"":permissionValue;
    }
    public void setPermissionValue(String permissionValue) {
        this.permissionValue = permissionValue;
    }

	public String getPeriodType() {
		return periodType==null?"":periodType;
	}
	public String getPeriodTypeDisplay() {
        if(periodType==null) return "";
        if (periodType.equalsIgnoreCase("alldays"))
            return " рабочих дней";
        if (periodType.equalsIgnoreCase("workdays"))
            return " календарных дней";
        return "";
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public Long getDaysBeforeNotifyBank() {
		return daysBeforeNotifyBank;
	}
	public String getDaysBeforeNotifyBankDisplay() {
		return Formatter.format(daysBeforeNotifyBank) + " " + getPeriodTypeDisplay();
	}

	public void setDaysBeforeNotifyBank(Long daysBeforeNotifyBank) {
		this.daysBeforeNotifyBank = daysBeforeNotifyBank;
	}
}
