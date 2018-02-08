package com.vtb.domain;

import java.util.ArrayList;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.PaymentFrequency;
import ru.masterdm.compendium.domain.spo.StandardPriceCondition;

import com.vtb.util.Formatter;

public class TaskProcent  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;// это айдишник-уникальный ключ. Не меняется. Используется как первичный ключ в БД
	private boolean rateTypeFixed=true;//Тип ставки: фиксированная(true) / плавающая (false)

	// ПРОГНОЗНЫЕ значения из системы CRM (для СДЕЛКИ, не для ЛИМИТА).
	private BaseRate base;        // ЛИМИТ: тип базовой ставки. СДЕЛКА: Тип плавающей части (LIBOR *) для ПРОГНОЗНЫХ значений из CRM
	private Double value;         // ЛИМИТ: значение базовой ставки. СДЕЛКА: Плавающая часть для ПРОГНОЗНЫХ значений из CRM
	
	// РАСЧЕТНЫЕ значения из системы рейтингов (для СДЕЛКИ, не для ЛИМИТА).
	private java.util.Date computeDate;   // дата расчета рейтинга
	private Double APC;	                 // АРС
	private Double basePremium;          // Базовая премия за кредитный риск 
	private Double trRiskC1;             // Коэффициент транзакционного риска С1
	private Double trRiskC2;             // Коэффициент транзакционного риска С2
	private Double margin;               // Экономическая маржа 
	private Double computedRate;         // Расчетная ставка
	private BaseRate ratingComputedBaseRateType; // Расчетный Тип плавающей части (LIBOR *)
	private String ratingComputedBaseRateTypeAsString; // Расчетный Тип плавающей части (LIBOR *) как строка, а не ссылка.
	private Double ratingComputedRate;           // Расчетный прогноз значения ставки
	
	// ФАКТИЧЕСКИЕ значения (и для СДЕЛКИ, и, возможно, для ЛИМИТА).)
	private Double riskpremium;             // Премия за риск фактическая.
	private Currency currency;              // Валюта (для СДЕЛКИ не используется, там всегда %)
	private Double procent;                 // Ставка фактическая.
	private String description;            
	
	private String riskDescription; // Сделка: описание риска
	
	boolean isRatingAvailable;       // Если по каким-то причинам система расчета рейтинга недоступна, игнорируем ее 
	                                 // и не пересчитываем значения.
	
	boolean isManuallyEdited;        // были ли значения riskpremium и (или) procent введены вручную пользователем
	                                 // (или постоянно пересчитывается из системы рейтингов)
	
	
	private String pay_int;   // порядок уплаты процентов кредита для Лимита.
	
	
	ArrayList<StandardPriceCondition> standardPriceConditionList; // стандартные стоимостные условия для Лимита
	
	private String additionalDescription; // дополнительное описание (в секции Обеспечение) 
	
	private String riskPremiumDescr; // премия за кредитный риск для отображения в отчетах (по новым требованиям ВТБ)    
	private String riskPremium; // Наименование премий для отображения в отчетах
	private String riskPremiumValue; // Исчисление премий по умолчанию для отображения в отчетах
	private Double turnover;         // размер надбавкки
	private String turnoverPremiumDescr; // Описание размера надбавки
	private Double riskPremiumChange; // Величина изменения премий за кредитный риск
	
	private String capitalPay = "";       // плата за экономический капитал 
	private String priceIndCondition = "";       // Индивидуальные условия
	private String KTR = "";              // некий КТР;
	
	public TaskProcent() {
        super();     
    }
	
	public TaskProcent(Long id) {
        super();
        this.id = id;
        standardPriceConditionList = new ArrayList<StandardPriceCondition>();
    }
    
    /**
     * Common constructor 
     */
	public TaskProcent(Long id, String description, BaseRate base,
            Double value, Double procent, Double riskpremium, Currency currency, Boolean rateTypeFixed) {
        super();
        this.id = id;
        if (rateTypeFixed != null) this.rateTypeFixed = rateTypeFixed;
        else this.rateTypeFixed  = false;
        this.base = base;
        this.value = value;
        
        // фактические значения
        this.procent = procent;
        this.riskpremium = riskpremium;
        this.currency = currency;
        this.description = description;
        standardPriceConditionList = new ArrayList<StandardPriceCondition>();
        validate();
    }
    
	/**
     * Constructor for Limit (with extra parameters) 
     */
	public TaskProcent(Long id, String description, BaseRate base,
            Double value, Double procent, Double riskpremium, Currency currency, Boolean rateTypeFixed, 
            String riskPremium, String riskPremiumValue, Double turnover, String turnoverPremiumDescr, Double riskPremiumChange, 
            String capitalPay, String KTR, String priceIndCondition, String pay_int) {
        this(id, description, base, value, procent, riskpremium, currency, rateTypeFixed);
        this.riskPremium = riskPremium;
        this.riskPremiumValue = riskPremiumValue; 
        this.turnover = turnover;
        this.turnoverPremiumDescr = turnoverPremiumDescr;
        this.riskPremiumChange = riskPremiumChange;
        this.capitalPay = capitalPay;
        this.KTR = KTR;
        this.priceIndCondition=priceIndCondition;
        this.pay_int=pay_int;
        this.riskPremiumDescr = generateRiskPremiumDescr();
    }
	
	/**
	 * Соберем премию для печати из значений
	 * @return
	 */
	private String generateRiskPremiumDescr() {
		String res = riskPremiumValue;
		if ((res == null) || (res.equals(""))) res = "Тип премии за кредитный риск не определен"; 
		else res = "";
		res += "\n";
		String changed = riskPremium==null?"":riskPremium.replaceAll("% годовых", "").replaceAll("%годовых", "");
		if ((!ru.masterdm.spo.utils.Formatter.str(riskPremiumValue).trim().equalsIgnoreCase("увеличенная"))
			&&	(!ru.masterdm.spo.utils.Formatter.str(riskPremiumValue).trim().equalsIgnoreCase("уменьшенная"))) 
			res += changed;
		else 
			res += changed + " " + Formatter.format(riskPremiumChange) + " % годовых";
		res += "\n" + ru.masterdm.spo.utils.Formatter.str(priceIndCondition)+"\n";
		if(riskpremium!=null && riskpremium>0)
			res += Formatter.format(riskpremium) + " % годовых";
		return res;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        /* TODO : refactor! Maybe, two different types: for LimIT and fo Deal
        if (rateTypeFixed == false) {
            if (base != null) addError("Процентная ставка. Для фиксированной ставки не должен быть задан Тип ставки");
            if (value != null) addError("Процентная ставка. Для фиксированной ставки не должен быть задан значение ставки");
        } else {
            if ((base == null) || (base.getCode() == null)) addError("Процентная ставка. Для плавающей ставки должен быть задан Тип ставки");
            if (value == null) addError("Процентная ставка. Для плавающей ставки должно быть задано значение ставки");
        }
        */
//        if ((procent == null))  
//            addError("Процентная ставка. Фактическая ставка должна быть задана");
        // для сделки currency устанавливается в null
        //if ((currency == null) || (currency.getCode() == null)) addError("Процентная ставка. Валюта не определена");   
    }
    
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BaseRate getBase() {
		return base;
	}
	public void setBase(BaseRate base) {
		this.base = base;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public Double getProcent() {
		return procent;
	}
	
	public String getFormattedProcent() {
	    return Formatter.format(procent);
    }
	
	public void setProcent(Double procent) {
		this.procent = procent;
	}
	
	public Double getRiskpremium() {
		return riskpremium;
	}
	
	public String getFormattedRiskpremium() {
        return Formatter.format(riskpremium);
	}
	
	public void setRiskpremium(Double riskpremium) {
		this.riskpremium = riskpremium;
	}
    public Currency getCurrency() {
        return currency;
    }
    public Currency getCurrency2() {
        return currency;
    }
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long nextId) {
		this.id=nextId;
	}
	
	 /**
     * @return ип ставки: фиксированная
     */
    public boolean isRateTypeFixed() {
        return rateTypeFixed;
    }

    /**
     * @param rateTypeFixed ип ставки: фиксированная
     */
    public void setRateTypeFixed(boolean rateTypeFixed) {
        this.rateTypeFixed = rateTypeFixed;
    }

    public String getRiskDescription() {
        return riskDescription;
    }

    public void setRiskDescription(String riskDescription) {
        this.riskDescription = riskDescription;
    }

    public java.util.Date getComputeDate() {
        return computeDate;
    }

    public String getComputeDateFormatted() {
        return computeDate==null?"":Formatter.format(computeDate);
     }

    
    public void setComputeDate(java.util.Date computeDate) {
        this.computeDate = computeDate;
    }

    public Double getAPC() {
        return APC;
    }

    public String getAPCFormatted() {
        return Formatter.format(APC);
    }

    public void setAPC(Double apc) {
        APC = apc;
    }

    public Double getBasePremium() {
        return basePremium;
    }

    public String getBasePremiumFormatted() {
        return Formatter.format(basePremium);
    }

    public void setBasePremium(Double basePremium) {
        this.basePremium = basePremium;
    }

    public Double getTrRiskC1() {
        return trRiskC1;
    }

    public String getTrRiskC1Formatted() {
        return Formatter.format(trRiskC1);
    }

    public void setTrRiskC1(Double trRiskC1) {
        this.trRiskC1 = trRiskC1;
    }

    public Double getTrRiskC2() {
        return trRiskC2;
    }

    public String getTrRiskC2Formatted() {
        return Formatter.format(trRiskC2);
    }
    
    public void setTrRiskC2(Double trRiskC2) {
        this.trRiskC2 = trRiskC2;
    }

    public Double getMargin() {
        return margin;
    }

    public String getMarginFormatted() {
        return Formatter.format(margin);
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public Double getComputedRate() {
        return computedRate;
    }

    public String getComputedRateFormatted() {
        return Formatter.format(computedRate);
    }
    
    public void setComputedRate(Double computedRate) {
        this.computedRate = computedRate;
    }

    public void setRatingAvailable(boolean isRatingAvailable) {
        this.isRatingAvailable = isRatingAvailable;
    }

    public boolean isRatingAvailable() {
        return isRatingAvailable;
    }

    public BaseRate getRatingComputedBaseRateType() {
        return ratingComputedBaseRateType;
    }

    public void setRatingComputedBaseRateType(BaseRate ratingComputedBaseRateType) {
        this.ratingComputedBaseRateType = ratingComputedBaseRateType;
    }

    public Double getRatingComputedRate() {
        return ratingComputedRate;
    }

    public void setRatingComputedRate(Double ratingComputedRate) {
        this.ratingComputedRate = ratingComputedRate;
    }

    public boolean isManuallyEdited() {
        return isManuallyEdited;
    }

    public void setManuallyEdited(boolean isManuallyEdited) {
        this.isManuallyEdited = isManuallyEdited;
    }
    
    /**
     * Compute fact data for TaskProcent (only when Rating System is available)
     * @param task
     */
    public void computeFactForTaskProcent(Task task) {
        if ((task == null) || (task.getTaskProcent() == null)) return;
        TaskProcent tp = task.getTaskProcent();

        // пересчитываем только, если доступна система рейтинга и только если поля фактических значений не были отредактированы вручную.
        if (tp.isRatingAvailable() && (!tp.isManuallyEdited())) {            
            double basePremium = (tp.getBasePremium() == null) ? 0.0 : tp.getBasePremium();
            double trRiskC1 = (tp.getTrRiskC1() == null) ? 0.0 : tp.getTrRiskC1();
            double trRiskC2 = (tp.getTrRiskC2() == null) ? 0.0 : tp.getTrRiskC2();
            double factRiskPremium = basePremium * trRiskC1 * trRiskC2; 
            /* if was not set in the previous step,  recalculate it. */
            if (tp.getRiskpremium() == null)  tp.setRiskpremium(factRiskPremium);
            else 
                if ((tp.getRiskpremium() != null) && (tp.getRiskpremium().doubleValue() == 0.0D)) tp.setRiskpremium(factRiskPremium);
                //else tp.setRiskpremium(null);
            
            if (tp.isRateTypeFixed()) {
                double ARC = (tp.getAPC() == null) ? 0.0 : tp.getAPC();
                double margin = (tp.getMargin() == null) ? 0.0 : tp.getMargin();
                double procent = ARC + factRiskPremium * trRiskC1 * trRiskC2 + margin; 
                /* values were not set in the previous step (editing of the claim) and were not saved in the database, recalculate it. */
                if (tp.getProcent() == null)  tp.setProcent(procent);
                else 
                    if ((tp.getProcent() != null) && (tp.getProcent().doubleValue() == 0.0D)) tp.setProcent(procent);
                    //else tp.setProcent(null);
                
            } else {
                double estimatedRate = (tp.getValue() == null) ? 0.0 : tp.getValue();
                double ARC = (tp.getAPC() == null) ? 0.0 : tp.getAPC();
                double margin = (tp.getMargin() == null) ? 0.0 : tp.getMargin();
                double procent = estimatedRate + ARC + factRiskPremium * trRiskC1 * trRiskC2 + margin; 
                /* if was not set in the previous step,  recalculate it. */
                if (tp.getProcent() == null)  tp.setProcent(procent);
                else 
                    if ((tp.getProcent() != null) && (tp.getProcent().doubleValue() == 0.0D)) tp.setProcent(procent);
                    //else tp.setProcent(null);
            }
        } 
    }

    public ArrayList<StandardPriceCondition> getStandardPriceConditionList() {
        return standardPriceConditionList;
    }

    public void setStandardPriceConditionList(
            ArrayList<StandardPriceCondition> standardPriceConditionList) {
        this.standardPriceConditionList = standardPriceConditionList;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public String getRatingComputedBaseRateTypeAsString() {
        return ratingComputedBaseRateTypeAsString;
    }

    public void setRatingComputedBaseRateTypeAsString(String ratingComputedBaseRateTypeAsString) {
        this.ratingComputedBaseRateTypeAsString = ratingComputedBaseRateTypeAsString;
    }
    
    /**
     * Наименование премий
     *
     * @return riskPremium
     */
    public String getRiskPremium() {
		return riskPremium;
	}

    /**
     * Наименование премий
     * 
     * @param riskPremium
     */
	public void setRiskPremium(String riskPremium) {
		this.riskPremium = riskPremium;
	}

	/**
	 * Исчисление премий по умолчанию
	 *
	 * @return riskPremiumValue
	 */
	public String getRiskPremiumValue() {
		return riskPremiumValue;
	}

	/**
	 * Исчисление премий по умолчанию
	 *
	 * @param riskPremiumValue
	 */
	public void setRiskPremiumValue(String riskPremiumValue) {
		this.riskPremiumValue = riskPremiumValue;
	}

	public Double getTurnover() {
		return turnover;
	}

	public void setTurnover(Double turnover) {
		this.turnover = turnover;
	}

	/**
	 * Описание размера надбавки
	 * @return
	 */
	public String getTurnoverPremiumDescr() {
		return turnoverPremiumDescr;
	}

	/**
	 * Описание размера надбавки
	 * @param turnoverPremiumDescr
	 */
	public void setTurnoverPremiumDescr(String turnoverPremiumDescr) {
		this.turnoverPremiumDescr = turnoverPremiumDescr;
	}

	/**
	 * Величина изменения премий за кредитный риск
	 * @return riskPremiumChange
	 */
	public Double getRiskPremiumChange() {
		return riskPremiumChange;
	}

	/**
	 * Величина изменения премий за кредитный риск
	 * @param riskPremiumChange
	 */
	public void setRiskPremiumChange(Double riskPremiumChange) {
		this.riskPremiumChange = riskPremiumChange;
	}

	public String getCapitalPay() {
		return capitalPay;
	}

	public void setCapitalPay(String capitalPay) {
		this.capitalPay = capitalPay;
	}

	/**
	 * @return the kTR
	 */
	public String getKTR() {
		return KTR;
	}

	/**
	 * @param kTR the kTR to set
	 */
	public void setKTR(String kTR) {
		KTR = kTR;
	}

	/**
	 * @return the riskPremiumDescr
	 */
	public String getRiskPremiumDescr() {
		return riskPremiumDescr;
	}

	public String getPriceIndCondition() {
		return priceIndCondition;
	}

	public void setPriceIndCondition(String priceIndCondition) {
		this.priceIndCondition = priceIndCondition;
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
	
}