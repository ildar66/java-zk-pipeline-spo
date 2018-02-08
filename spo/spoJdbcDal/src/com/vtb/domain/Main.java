package com.vtb.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.OtherGoal;
import ru.md.domain.TargetGroupLimit;

import com.vtb.util.Formatter;

import ru.masterdm.compendium.domain.Currency;

public class Main extends VtbObject {
    private static final long serialVersionUID = 1L;
    private BigDecimal sum;           // сумма лимита
    private BigDecimal exchangeRate;  // курс валюты в поле CURRENCY к рублю
    private Currency currency;        // валюта лимита

    private Date validfrom;  //дата начала действия лимита, сделки
    private Date validto;    //дата окончания действия лимита, сделки
    private Date usedatefrom;//дата начала использования лимита, сделки
    private Date usedate;    //дата окончания использования лимита, сделки
    private Integer period;  // срок действия лимита (сделки) в днях
    private String periodDimension; //размерность срока
    private String period_validTo;  // срок действия лимита (сделки) в днях для отображения на UI
    private Integer useperiod; // срок использования лимита (сделки) в днях
    private String useperiodtype;

    private String extraSumInfo;   // Дополнительная информация по сумме (поле имеется только для сублимита)
    private boolean redistribResidues = false;
    private boolean renewable = false;
    private boolean mayBeRenewable = false;  // информация из вышестоящих лимитов \ сублимитов.
                                               // Запрещает или разрешает возможность устанавливать
                                               // флаг 'Возобновляемый' для нижележащих лимитов \ сублимиты
    private boolean projectFin = false;
    private String changedConditions;  // Измененные и дополненные условия
    private String country;  // Страновая принадлежность
    private String targetTypeComment;             // Контроль целевого использования в секции 
    private String projectName;  // Поле «Название проекта / Контрагент»
    private String issuingBank; //Выдающий Банк
    
    private ArrayList<OtherGoal> otherGoals = new ArrayList<OtherGoal>();  
    private ArrayList<Forbidden> forbiddens = new ArrayList<Forbidden>();
    private List<TargetGroupLimit> targetGroupLimits = new ArrayList<TargetGroupLimit>();  

    /*********** Дополнительная информация для сделки *******************************************/
    private String contract;     // Поле «Контракт» - текстовое поле, для Сделок по гарантиям, заполняется вручную.
    private String warrantyItem; // Поле «Предмет гарантии – текстовое поле, для Сделок по гарантиям, заполняется вручную.
    private String beneficiary;  // Поле «Бенефициар» - текстовое поле, для Сделок по гарантиям, заполняется вручную.
    private String beneficiaryOGRN;  // Поле «ОГРН» - текстовое поле, для Сделок по гарантиям, заполняется вручную.
    private Date proposedDateSigningAgreement; // Поле «Планируемая дата подписания Кредитного соглашения» - дата, вводимая пользователем.
    private String periodComment; // Комментарий по сроку сделки

    private BigDecimal limitIssueSum;         // сумма лимита выдачи
    private BigDecimal debtLimitSum;          // сумма лимита задолженности
    private String sumWithCurrency;           // сумма сделки
    private String limitIssueSumWithCurrency; // сумма лимита выдачи вместе с показателем валютой
    private String debtLimitSumWithCurrency;  // сумма лимита выдачи вместе с показателем валютой

    private String product_group_names = ""; //название групп видов сделки через точку с запятой для отчета лимита
    private String product_name = ""; //название вида сделки
    private boolean irregular = false;     // Настандартная сделка
    private boolean isLimitIssue = false;     // флаг, вводить ли лимит выдачи (LV in CRM)
    private boolean isDebtLimit = false;      // флаг, вводить ли лимит задолженности (LZ in CRM)
    private boolean isCreditLineType = false; // флаг, относится ли вид сделки к гарантиям кредитным линиям
                                              //  = isLimitIssue || isDebtLimit
    private boolean isGuaranteeType = false;  // флаг, относится ли вид сделки к гарантиям (их тоже может быть несколько разных). Непонятно, как брать
    
    
    private boolean isMainLoaded = false;
    
    private ArrayList<LimitTree> limitTreeList = new ArrayList<LimitTree>();  
          // все сублимиты и сделки, на которые ссылается Task.
          // взять только детей, не всю иерархию лимитов\сублимитов
          // используется для отображения секции Сублимиты.
          // и для построения списка сублимитов при построении отчетов.

    private String acredetivSourcePayment = "";  // "Источник формирования покрытия для осуществления платежа по аккредитиву"

    private String genCondLimit = "";  // Общие условия для лимита в секции Условия. ТОЛЬКО ДЛЯ ОТЧЕТОВ!!!

    public boolean isDocumentary = false;  // Является ли сублимит или сделка документарным. ТОЛЬКО ДЛЯ ОТЧЕТОВ!!!
                                           // для проверки при работе используйте TaskJPA!!!

    @Override
    public void validate() {
        correctQuantityData();
    }

	/**
	 * Процедура корректирует возможные неверные значения quantity***, если что-то было выставлено неверно
	 */
	public void correctQuantityData() {
		if (!isDebtLimit && !isLimitIssue) {
			// поле суммы не может быть пустым!!!
			if (sum == null) {
				BigDecimal maxDebtLimitSum = debtLimitSum != null ? debtLimitSum : BigDecimal.ZERO;
				BigDecimal maxLimitIssueSum = limitIssueSum != null ? limitIssueSum : BigDecimal.ZERO;
				sum = maxDebtLimitSum.max(maxLimitIssueSum);
			}
		}

		if (isDebtLimit && isLimitIssue)
			sum = limitIssueSum;
		else if (isDebtLimit && debtLimitSum != null && !debtLimitSum.equals(BigDecimal.ZERO))
			sum = debtLimitSum;
		else if (isLimitIssue && limitIssueSum != null && !limitIssueSum.equals(BigDecimal.ZERO))
			sum = limitIssueSum;

		setSumWithCurrency();
	}

    /**
     * Тип процесса
     */
    private String descriptionProcess;
    private Long idProcessType;

    public String getDescriptionProcess() {
        return descriptionProcess;
    }

    public BigDecimal getSum() {
        return sum;
    }
    public String getFormattedSum() {
        return Formatter.format(sum);
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
        setSumWithCurrency();
    }

    public Currency getCurrency2() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getValidto() {
    	/*try {
    		if(period!=null && proposedDateSigningAgreement!=null && validto==null){
    			CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
    			return new java.sql.Date(compenduim.findDeadlineDate(false, proposedDateSigningAgreement, period).getTime());
    		}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}*/
        return validto;
    }

    public void setValidto(Date validto) {
        this.validto = validto;
        //this.period_validTo = getPeriodValidTo(period, validto);
    }

    public Date getUsedate() {
        return usedate;
    }

    public void setUsedate(Date usedate) {
        this.usedate = usedate;
    }

    public Long getPeriodInDay() {
    	if(periodDimension==null || periodDimension.isEmpty())
    		return 0L;
    	try {
    		return periodDimension.equals("дн.")?getPeriod():(periodDimension.equals("мес.")?getPeriod().longValue()*30:getPeriod()*365);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
    	return 0L;
    }
    public Integer getPeriod() {
    	/*try {
    		if(period==null && proposedDateSigningAgreement!=null && validto!=null){
    			CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
    			return compenduim.getInterval(false, proposedDateSigningAgreement, validto);
    		}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}*/
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
        //this.period_validTo = getPeriodValidTo(period, validto);
    }

    public Integer getUseperiod() {
        return useperiod;
    }

    public void setUseperiod(Integer useperiod) {
        this.useperiod = useperiod;
    }

    public String getUseperiodtype() {
        return useperiodtype;
    }

    public void setUseperiodtype(String useperiodtype) {
        this.useperiodtype = useperiodtype;
    }

    public void setDescriptionProcess(String descriptionProcess) {
        this.descriptionProcess = descriptionProcess;
    }

	/**
	 * @return курс валюты в поле CURRENCY к рублю
	 */
	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}
	/**
	 * @return курс валюты в поле CURRENCY к рублю
	 */
	public String getFormatedExchangeRate() {
		return Formatter.format(exchangeRate);
	}

	/**
	 * @param exchangeRate курс валюты в поле CURRENCY к рублю
	 */
	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	/**
	 * Возвращает сумму заявки в рублях
	 * @return
	 */
	public BigDecimal getRurSum(){
		if(currency.getCode()==null||sum==null)return null;
		return currency.getCode().equalsIgnoreCase("RUR")?
				sum:sum.multiply(getExchangeRate()==null?BigDecimal.valueOf(0):getExchangeRate());
	}

    public String getExtraSumInfo() {
        return extraSumInfo;
    }

    public void setExtraSumInfo(String extraSumInfo) {
        this.extraSumInfo = extraSumInfo;
    }

    public boolean isRedistribResidues() {
        return redistribResidues;
    }

    public void setRedistribResidues(boolean redistribResidues) {
        this.redistribResidues = redistribResidues;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    public boolean isProjectFin() {
        return projectFin;
    }

    public void setProjectFin(boolean projectFin) {
        this.projectFin = projectFin;
    }

    public ArrayList<OtherGoal> getOtherGoals() {
        return otherGoals;
    }

    public void setOtherGoals(ArrayList<OtherGoal> otherGoals) {
        this.otherGoals = otherGoals;
    }
    
    /**
	 * Возвращает {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений} сделки
	 *
	 * @return {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений} сделки
	 */
	public List<TargetGroupLimit> getTargetGroupLimits() {
		return targetGroupLimits;
	}

	/**
	 * Устанавливает {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений} сделки
	 *
	 * @param targetGroupLimits {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений} сделки
	 */
	public void setTargetGroupLimits(List<TargetGroupLimit> targetGroupLimits) {
		this.targetGroupLimits = targetGroupLimits;
	}

	public Date getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(Date validfrom) {
        this.validfrom = validfrom;
    }

    public Date getUsedatefrom() {
        return usedatefrom;
    }

    public void setUsedatefrom(Date usedatefrom) {
        this.usedatefrom = usedatefrom;
    }

    public boolean isMayBeRenewable() {
        return mayBeRenewable;
    }

    public void setMayBeRenewable(boolean mayBeRenewable) {
        this.mayBeRenewable = mayBeRenewable;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getWarrantyItem() {
        return warrantyItem;
    }

    public void setWarrantyItem(String warrantyItem) {
        this.warrantyItem = warrantyItem;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public Date getProposedDateSigningAgreement() {
        return proposedDateSigningAgreement;
    }

    public void setProposedDateSigningAgreement(Date proposedDateSigningAgreement) {
        this.proposedDateSigningAgreement = proposedDateSigningAgreement;
    }

    public String getPeriodComment() {
        return periodComment;
    }

    public void setPeriodComment(String periodComment) {
        this.periodComment = periodComment;
    }

    public BigDecimal getLimitIssueSum() {
        return limitIssueSum;
    }

    public void setLimitIssueSum(BigDecimal limitIssueSum) {
        this.limitIssueSum = limitIssueSum;
        setLimitIssueSumWithCurrency();
    }


    public BigDecimal getDebtLimitSum() {
        return debtLimitSum;
    }

    public void setDebtLimitSum(BigDecimal debtLimitSum) {
        this.debtLimitSum = debtLimitSum;
        setDebtLimitSumWithCurrency();
    }

    public boolean isLimitIssue() {
        return isLimitIssue;
    }

    public void setLimitIssue(boolean isLimitIssue) {
        this.isLimitIssue = isLimitIssue;
        // установим признак кредитной линии
        this.isCreditLineType = isLimitIssue || isDebtLimit;
        // установим кредит выдачи с валютой
        setLimitIssueSumWithCurrency();
    }

    public boolean isDebtLimit() {
        return isDebtLimit;
    }

    public void setDebtLimit(boolean isDebtLimit) {
        this.isDebtLimit = isDebtLimit;
        // установим признак кредитной линии
        this.isCreditLineType = isLimitIssue || isDebtLimit;
        // установим кредит задолженности с валютой
        setDebtLimitSumWithCurrency();
    }

    public boolean isCreditLineType() {
        return isCreditLineType;
    }

    public void setCreditLineType(boolean isCreditLineType) {
        this.isCreditLineType = isCreditLineType;
    }

    public boolean isGuaranteeType() {
        return isGuaranteeType;
    }

    public void setGuaranteeType(boolean isGuaranteeType) {
        this.isGuaranteeType = isGuaranteeType;
    }

    public ArrayList<LimitTree> getLimitTreeList() {
        return limitTreeList;
    }

    public void setLimitTreeList(ArrayList<LimitTree> limitTreeList) {
        this.limitTreeList = limitTreeList;
    }

    public String getPeriod_validTo() {
        return period_validTo;
    }

    public void setPeriod_validTo(String period_validTo) {
        this.period_validTo = period_validTo;
    }

    /**
     * Generates standard period / validTo data
     */
    public  String getPeriodValidTo(Integer period, Date validTo) {
        if ((period != null) && !(period.intValue() == 0)) return Formatter.toMoneyFormat(period) + " дн.";
        if (validTo != null) return Formatter.format(validTo);
        return "";
    }

    public String getSumWithCurrency() {
        return sumWithCurrency;
    }

    public void setSumWithCurrency() {
        /*if ((sum != null) && (currency != null))
            this.sumWithCurrency = Formatter.toMoneyFormat(sum.longValue()) + " (" + currency.getCode() + ")";
        else  this.sumWithCurrency = "";*/
        String code = currency==null?null:currency.getCode();
        this.sumWithCurrency = SBeanLocator.singleton().getDictService().moneyDisplay(sum,code);
    }

    public String getLimitIssueSumWithCurrency() {
        return limitIssueSumWithCurrency;
    }

    public void setLimitIssueSumWithCurrency() {
        if (isLimitIssue && (limitIssueSum != null) && (currency != null))
            this.limitIssueSumWithCurrency = Formatter.toMoneyFormat(limitIssueSum.longValue()) + " (" + currency.getCode() + ")";
        else  this.limitIssueSumWithCurrency = "";
    }

    public String getDebtLimitSumWithCurrency() {
        return debtLimitSumWithCurrency;
    }

    public void setDebtLimitSumWithCurrency() {
        if (isDebtLimit && (debtLimitSum != null) && (currency != null))
            this.debtLimitSumWithCurrency = Formatter.toMoneyFormat(debtLimitSum.longValue()) + " (" + currency.getCode() + ")";
        else  this.debtLimitSumWithCurrency = "";
    }

    /**
     * @return idProcessType
     */
    public Long getIdProcessType() {
        return idProcessType;
    }

    /**
     * @param idProcessType idProcessType
     */
    public void setIdProcessType(Long idProcessType) {
        this.idProcessType = idProcessType;
    }

	public String getAcredetivSourcePayment() {
		return acredetivSourcePayment;
	}

	public void setAcredetivSourcePayment(String acredetivSourcePayment) {
		this.acredetivSourcePayment = acredetivSourcePayment;
	}

	public String getGenCondLimit() {
		return genCondLimit;
	}

	public void setGenCondLimit(String genCondLimit) {
		this.genCondLimit = genCondLimit;
	}

	public String getChangedConditions() {
		return changedConditions;
	}

	public void setChangedConditions(String changedConditions) {
		this.changedConditions = changedConditions;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName project name to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return размерность срока
	 */
	public String getPeriodDimension() {
		if(periodDimension==null)
			return "";
		return periodDimension;
	}

	/**
	 * @param periodDimension размерность срока
	 */
	public void setPeriodDimension(String periodDimension) {
		this.periodDimension = periodDimension;
	}

	/**
	 * @return the beneficiaryOGRN
	 */
	public String getBeneficiaryOGRN() {
		return beneficiaryOGRN;
	}

	/**
	 * @param beneficiaryOGRN the beneficiaryOGRN to set
	 */
	public void setBeneficiaryOGRN(String beneficiaryOGRN) {
		this.beneficiaryOGRN = beneficiaryOGRN;
	}

	/**
	 * @return Настандартная сделка
	 */
	public boolean isIrregular() {
		return irregular;
	}

	/**
	 * @param irregular Настандартная сделка
	 */
	public void setIrregular(boolean irregular) {
		this.irregular = irregular;
	}

	/**
	 * @return the product_name
	 */
	public String getProduct_name() {
		return product_name==null?"":product_name;
	}

	/**
	 * @param product_name the product_name to set
	 */
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	/**
	 * @return название групп видов сделки через точку с запятой для отчета лимита
	 */
	public String getProduct_group_names() {
		return product_group_names;
	}

	/**
	 * @param product_group_names название групп видов сделки через точку с запятой для отчета лимита
	 */
	public void setProduct_group_names(String product_group_names) {
		this.product_group_names = product_group_names;
	}
	
	/**
	 * @return Контроль целевого использования
	 */
	public String getTargetTypeComment() {
		return targetTypeComment;
	}

	/**
	 * @param Контроль целевого использования
	 */
	public void setTargetTypeComment(String targetTypeComment) {
		this.targetTypeComment = targetTypeComment;
	}

	public ArrayList<Forbidden> getForbiddens() {
		return forbiddens;
	}

	public void setForbiddens(ArrayList<Forbidden> forbiddens) {
		this.forbiddens = forbiddens;
	}

	/**
	 * Возвращает {@link Boolean признак} загружалась ли основная секция
	 *
	 * @return {@link Boolean признак} загружалась ли основная секция
	 */
	public boolean isMainLoaded() {
		return isMainLoaded;
	}

	/**
	 * Устанавливает {@link Boolean признак} загружалась ли основная секция
	 *
	 * @param isMainLoaded {@link Boolean признак} загружалась ли основная секция
	 */
	public void setMainLoaded(boolean isMainLoaded) {
		this.isMainLoaded = isMainLoaded;
	}	
	

    /**
     * Возвращает выдающий банк.
     * @return выдающий банк
     */
    public String getIssuingBank() {
        return issuingBank;
    }

    /**
     * Устанавливает выдающий банк.
     * @param issuingBank выдающий банк
     */
    public void setIssuingBank(String issuingBank) {
        this.issuingBank = issuingBank;
    }
}
