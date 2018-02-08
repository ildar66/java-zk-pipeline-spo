package ru.md.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.LoggerFactory;


import ru.masterdm.spo.utils.Formatter;
import ru.md.domain.dict.CommonDictionary;
import ru.md.domain.dict.PeriodDimension;
import ru.md.domain.dict.ProcessType;

/**
 * Заявка.
 * @author Andrey Pavlenko
 */
public class MdTask {

	private Long idMdtask;
	private Long idInstance;
	private String tasktype;
	private String processname;
	private String priority;
	private String initdep;
	private Date creationDate;
	private Date lastUpdateDate;
	private Long idPupProcess;
	private Long idTypeProcess;
	private Long isImported;
	private String isImportedBm;
	private Long idStatus;
	private Long parentid;
	private BigDecimal mdtaskSum;
	private String currency;
	private Long mdtaskNumber;
	private String ekname;
	private String ekgroup;
	private String productName;
	private String productFamily;
	private String nameLimitType;
	private Long period;
	private String periodDimension;
	private String payInt;
	private Date validto;
	private String mainOrgChangeble;
	private String country;
	private String faces3;//распространяется на 3 лица
	private Date proposedDtSigning;
	private Org mainOrganization;
	private List<Org> otherOrganizations;
	private List<String> productGroups;
	private BigDecimal limitIssueSum;
	private BigDecimal debtLimitSum;
	private boolean limitSum;
	private boolean debtSum;
	private Boolean fixedRate;
	private boolean interestRateFixed;
	private boolean interestRateDerivative;
	private boolean supplyexist;
	private boolean userInProjectTeam;
	private CommonDictionary<String> baseRate;
	private List<InterestRate> interestRates;
	private Pipeline pipeline;
	private BigDecimal fixingRateSpread;
	private BigDecimal earlyRepaymentSpread;
	private BigDecimal currentCurrencyRate;
	private BigDecimal usdCurrencyRate;
	private Long drawdownDateInMonth;
	private Long usePeriod;
	private Date useDate;
	private boolean additionalContract;
	private boolean productMonitoring;
	private Product productType;
	private Boolean earlyRepaymentBan;
	private Long earlyRepaymentBanPeriod;
	private BigDecimal arrangementFee;
	private List<Comission> comissions;
	private BigDecimal supportComission;
	private Long version;
	private String projectName;
	private String projectClass;
	private String projectIndustry;
	private String projectRegion;
	private String projectRating1;
	private String projectRating2;
	private String projectRating3;
	private String projectRating4;

	private Long ccCacheStatusid;
	private String statusreturn;

	private String mapStatus;
	private String crossSellName;
	private Long crossSellId;

	private Date meetingProposedDate;
	private Integer initdepartment;
	private Long questionGroup;

	public Long getIdMdtask() {
		return idMdtask;
	}

	public void setIdMdtask(Long idMdtask) {
		this.idMdtask = idMdtask;
	}

	public String getTasktype() {
		return tasktype;
	}

	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	public Long getIdPupProcess() {
		return idPupProcess;
	}

	public void setIdPupProcess(Long idPupProcess) {
		this.idPupProcess = idPupProcess;
	}

	public boolean isProduct() {
		return getTasktype() != null && getTasktype().equals("p");
	}

	public boolean isCrossSell() {
		return getTasktype() != null && getTasktype().equals("c");
	}

	public boolean isLimit() {
		return getTasktype() != null && getTasktype().equals("l");
	}

	/** Тип кредитной заявки */
	public String getType() {
		if (getTasktype() == null) return "Сублимит";
		if (getTasktype().equals("l")) return "Лимит";
		if (getTasktype().equals("p")) return "Сделка";
		if (getTasktype().equals("c")) return "Кросс-селл";
		return "Сублимит";
	}

	public boolean isSublimit() {
		return getTasktype() == null || getTasktype().equals("s");
	}

	public Long getIdTypeProcess() {
		return idTypeProcess;
	}

	public void setIdTypeProcess(Long idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	public boolean isImported() {
		return isImported != null && isImported > 0 || !Formatter.str(isImportedBm).isEmpty();
	}

	public boolean isImportedAccess() {
		return isImported != null && isImported > 0;
	}

	public boolean isImportedBM() {
		return !Formatter.str(isImportedBm).isEmpty();
	}

	public Long getIsImported() {
		return isImported;
	}

	public void setIsImported(Long isImported) {
		this.isImported = isImported;
	}

	public boolean isPaused() {
		if (idStatus == null)
			return false;
		return idStatus.longValue() == 2;
	}

	public Long getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Long idStatus) {
		this.idStatus = idStatus;
	}

	public Long getParentid() {
		return parentid;
	}

	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}

	public BigDecimal getMdtaskSum() {
		return mdtaskSum;
	}

	public String getSumWithCurrency() {
		return Formatter.format(mdtaskSum) + " " + currency;
	}

	public String getSumMillionWithCurrency() {
		if (mdtaskSum == null)
			return "";
		BigDecimal mln = mdtaskSum.divide(new BigDecimal(1000000.0), 2, RoundingMode.UP);
		String m = String.valueOf(mln.doubleValue()).replaceAll("\\.", ",").replaceAll(",00", "");
		if (m.endsWith(",0"))
			m = m.replaceAll(",0", "");
		return m + " млн. " + currency;
	}

	public void setMdtaskSum(BigDecimal mdtaskSum) {
		this.mdtaskSum = mdtaskSum;
	}

	public boolean isPipelineProcess() {
		return processname != null && processname.equalsIgnoreCase("Pipeline");
	}

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}

	/**
	 * Возвращает приоритет.
	 * @return приоритет
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * Устанавливает приоритет.
	 * @param priority приоритет
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * Возвращает дату создания.
	 * @return дата создания
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Устанавливает дату создания.
	 * @param creationDate дата создания
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Возвращает дата последнего обновления.
	 * @return дата последнего обновления
	 */
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * Устанавливает дата последнего обновления.
	 * @param lastUpdateDate дата последнего обновления
	 */
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Long getMdtaskNumber() {
		return mdtaskNumber;
	}

	public void setMdtaskNumber(Long mdtaskNumber) {
		this.mdtaskNumber = mdtaskNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getEkname() {
		return ekname;
	}

	public void setEkname(String ekname) {
		this.ekname = ekname;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPeriodFormated() {
		if (period == null) {
			if (validto != null && proposedDtSigning != null) {
				Long result = BigDecimal.valueOf(validto.getTime() - proposedDtSigning.getTime())
										.divide(BigDecimal.valueOf(DateUtils.MILLIS_PER_DAY), MathContext.DECIMAL32)
										.longValue();
				if (result < 0)
					result = 0L;
				return result + " дн.";
			}
			return "";
		}
		return period.toString() + " " + Formatter.str(getPeriodDimension());
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public String getPeriodDimension() {
		return periodDimension;
	}

	public void setPeriodDimension(String periodDimension) {
		this.periodDimension = periodDimension;
	}

	public String getValidtoDisplay() {
		if (isSublimit()) {
			return Formatter.str(getPeriod()) + " " + Formatter.str(getPeriodDimension());
		}
		if (validto == null)
			return "";
		return Formatter.format(validto);// + " (осталось " +
		//Days.daysBetween(new DateTime(), new DateTime(validto)).getDays() +	" дн.)";
	}

	public Date getValidto() {
		return validto;
	}

	public void setValidto(Date validto) {
		this.validto = validto;
	}

	public String getMainOrgChangeble() {
		return mainOrgChangeble;
	}

	public boolean isMainOrgChangebleB() {
		return mainOrgChangeble != null && mainOrgChangeble.equalsIgnoreCase("y");
	}

	public void setMainOrgChangeble(String mainOrgChangeble) {
		this.mainOrgChangeble = mainOrgChangeble;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean is3faces() {
		return faces3 != null && faces3.equalsIgnoreCase("y");
	}

	public String getFaces3() {
		return faces3;
	}

	public void setFaces3(String faces3) {
		this.faces3 = faces3;
	}

	/**
	 * Возвращает планируемая дата подписания Кредитного соглашения.
	 * @return планируемая дата подписания Кредитного соглашения
	 */
	public Date getProposedDtSigning() {
		return proposedDtSigning;
	}

	/**
	 * Устанавливает планируемая дата подписания Кредитного соглашения.
	 * @param proposedDtSigning планируемая дата подписания Кредитного соглашения
	 */
	public void setProposedDtSigning(Date proposedDtSigning) {
		this.proposedDtSigning = proposedDtSigning;
	}

	/**
	 * Возвращает основной заемщик.
	 * @return основной заемщик
	 */
	public Org getMainOrganization() {
		return mainOrganization;
	}

	/**
	 * Устанавливает основной заемщик.
	 * @param mainOrganization основной заемщик
	 */
	public void setMainOrganization(Org mainOrganization) {
		this.mainOrganization = mainOrganization;
	}

	/**
	 * Возвращает остальные организации.
	 * @return остальные организации
	 */
	public List<Org> getOtherOrganizations() {
		return otherOrganizations;
	}

	/**
	 * Устанавливает остальные организации.
	 * @param otherOrganizations остальные организации
	 */
	public void setOtherOrganizations(List<Org> otherOrganizations) {
		this.otherOrganizations = otherOrganizations;
	}

	/**
	 * Возвращает группа вида сделки.
	 * @return группа вида сделки
	 */
	public List<String> getProductGroups() {
		return productGroups;
	}

	/**
	 * Устанавливает группа вида сделки.
	 * @param productGroups группа вида сделки
	 */
	public void setProductGroups(List<String> productGroups) {
		this.productGroups = productGroups;
	}

	/**
	 * Возвращает сумма лимита выдачи.
	 * @return сумма лимита выдачи
	 */
	public BigDecimal getLimitIssueSum() {
		return limitIssueSum;
	}

	/**
	 * Устанавливает сумма лимита выдачи.
	 * @param limitIssueSum сумма лимита выдачи
	 */
	public void setLimitIssueSum(BigDecimal limitIssueSum) {
		this.limitIssueSum = limitIssueSum;
	}

	/**
	 * Возвращает сумма лимита задолженности.
	 * @return сумма лимита задолженности
	 */
	public BigDecimal getDebtLimitSum() {
		return debtLimitSum;
	}

	/**
	 * Устанавливает сумма лимита задолженности.
	 * @param debtLimitSum сумма лимита задолженности
	 */
	public void setDebtLimitSum(BigDecimal debtLimitSum) {
		this.debtLimitSum = debtLimitSum;
	}

	/**
	 * Возвращает флаг наличия лимита выдачи.
	 * @return флаг наличия лимита выдачи
	 */
	public boolean isLimitSum() {
		return limitSum;
	}

	/**
	 * Устанавливает флаг наличия лимита выдачи.
	 * @param limitSum флаг наличия лимита выдачи
	 */
	public void setLimitSum(boolean limitSum) {
		this.limitSum = limitSum;
	}

	/**
	 * Возвращает флаг наличия лимита задолженности.
	 * @return флаг наличия лимита задолженности
	 */
	public boolean isDebtSum() {
		return debtSum;
	}

	/**
	 * Устанавливает флаг наличия лимита задолженности.
	 * @param debtSum флаг наличия лимита задолженности
	 */
	public void setDebtSum(boolean debtSum) {
		this.debtSum = debtSum;
	}

	/**
	 * Возвращает тип ставки.
	 * @return <code>true</code> если ставка фиксированная, иначе плавающая
	 */
	public Boolean isFixedRate() {
		return fixedRate;
	}

	/**
	 * Возвращает тип ставки (для freemaker).
	 * @return <code>true</code> если ставка фиксированная, иначе плавающая
	 */
	public String getFixedRateDisplay() {
		String res = "";
		if (isInterestRateFixed())
			res += "фиксированная";
		if (isInterestRateDerivative()) {
			if (!res.isEmpty())
				res += ", ";
			res += "плавающая";
		}
		return res;
	}

	/**
	 * Возвращает тип ставки фиксированная.
	 * @return <code>true</code> если ставка фиксированная
	 */
	public boolean isInterestRateFixed() {
		return interestRateFixed;
	}

	/**
	 * Устанавливает тип ставки.
	 * @param fixedRate тип ставки
	 */
	public void setInterestRateFixed(boolean interestRateFixed) {
		this.interestRateFixed = interestRateFixed;
	}

	/**
	 * Возвращает тип ставки плавающая.
	 * @return <code>true</code> если ставка плавающая
	 */
	public boolean isInterestRateDerivative() {
		return interestRateDerivative;
	}

	/**
	 * Устанавливает тип ставки.
	 * @param fixedRate тип ставки
	 */
	public void setInterestRateDerivative(boolean interestRateDerivative) {
		this.interestRateDerivative = interestRateDerivative;
	}

	/**
	 * Устанавливает тип ставки.
	 * @param fixedRate тип ставки
	 */
	public void setFixedRate(Boolean fixedRate) {
		this.fixedRate = fixedRate;
	}

	/**
	 * Возвращает базовая (индикативная) ставка.
	 * @return базовая (индикативная) ставка
	 */
	public CommonDictionary<String> getBaseRate() {
		return baseRate;
	}

	/**
	 * Устанавливает базовая (индикативная) ставка.
	 * @param baseRate базовая (индикативная) ставка
	 */
	public void setBaseRate(CommonDictionary<String> baseRate) {
		this.baseRate = baseRate;
	}

	/**
	 * Возвращает список процентных ставок.
	 * @return список процентных ставок
	 */
	public List<InterestRate> getInterestRates() {
		return interestRates;
	}

	/**
	 * Устанавливает список процентных ставок.
	 * @param interestRates список процентных ставок
	 */
	public void setInterestRates(List<InterestRate> interestRates) {
		this.interestRates = interestRates;
	}

	/**
	 * Возвращает pipeline.
	 * @return pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * Устанавливает pipeline.
	 * @param pipeline pipeline
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	/**
	 * Возвращает компенсирующий спред за фиксацию ставки.
	 * @return компенсирующий спред за фиксацию ставки
	 */
	public BigDecimal getFixingRateSpread() {
		return fixingRateSpread;
	}

	/**
	 * Устанавливает компенсирующий спред за фиксацию ставки.
	 * @param fixingRateSpread компенсирующий спред за фиксацию ставки
	 */
	public void setFixingRateSpread(BigDecimal fixingRateSpread) {
		this.fixingRateSpread = fixingRateSpread;
	}

	/**
	 * Возвращает компенсирующий спред за досрочное погашение.
	 * @return компенсирующий спред за досрочное погашение
	 */
	public BigDecimal getEarlyRepaymentSpread() {
		return earlyRepaymentSpread;
	}

	/**
	 * Устанавливает компенсирующий спред за досрочное погашение.
	 * @param earlyRepaymentSpread компенсирующий спред за досрочное погашение
	 */
	public void setEarlyRepaymentSpread(BigDecimal earlyRepaymentSpread) {
		this.earlyRepaymentSpread = earlyRepaymentSpread;
	}

	/**
	 * Возвращает курс валюты сделки.
	 * @return курс валюты сделки
	 */
	public BigDecimal getCurrentCurrencyRate() {
		return currentCurrencyRate;
	}

	/**
	 * Устанавливает курс валюты сделки.
	 * @param currentCurrencyRate курс валюты сделки
	 */
	public void setCurrentCurrencyRate(BigDecimal currentCurrencyRate) {
		this.currentCurrencyRate = currentCurrencyRate;
	}

	/**
	 * Возвращает курс USD.
	 * @return курс USD
	 */
	public BigDecimal getUsdCurrencyRate() {
		return usdCurrencyRate;
	}

	/**
	 * Устанавливает курс USD.
	 * @param usdCurrencyRate курс USD
	 */
	public void setUsdCurrencyRate(BigDecimal usdCurrencyRate) {
		this.usdCurrencyRate = usdCurrencyRate;
	}

	/**
	 * Возвращает срок выборки (мес).
	 * @return срок выборки (мес)
	 */
	public Long getDrawdownDateInMonth() {
		return drawdownDateInMonth;
	}

	/**
	 * Устанавливает срок выборки (мес).
	 * @param drawdownDateInMonth срок выборки (мес)
	 */
	public void setDrawdownDateInMonth(Long drawdownDateInMonth) {
		this.drawdownDateInMonth = drawdownDateInMonth;
	}

	/**
	 * Возвращает период использования.
	 * @return период использования
	 */
	public Long getUsePeriod() {
		return usePeriod;
	}

	/**
	 * Устанавливает период использования.
	 * @param usePeriod период использования
	 */
	public void setUsePeriod(Long usePeriod) {
		this.usePeriod = usePeriod;
	}

	/**
	 * Возвращает дату использования.
	 * @return дату использования
	 */
	public Date getUseDate() {
		return useDate;
	}

	/**
	 * Устанавливает дату использования.
	 * @param useDate дату использования
	 */
	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}

	/**
	 * Возвращает вид продукта (сделки).
	 * @return вид продукта (сделки)
	 */
	public Product getProductType() {
		return productType;
	}

	/**
	 * Устанавливает вид продукта (сделки).
	 * @param productType вид продукта (сделки)
	 */
	public void setProductType(Product productType) {
		this.productType = productType;
	}

	/**
	 * Возвращает признак запрета досрочного погашения.
	 * @return признак запрета досрочного погашения
	 */
	public Boolean getEarlyRepaymentBan() {
		return earlyRepaymentBan;
	}

	/**
	 * Устанавливает признак запрета досрочного погашения.
	 * @param earlyRepaymentBan признак запрета досрочного погашения
	 */
	public void setEarlyRepaymentBan(Boolean earlyRepaymentBan) {
		this.earlyRepaymentBan = earlyRepaymentBan;
	}

	/**
	 * Возвращает период запрета досрочного погашения.
	 * @return период запрета досрочного погашения
	 */
	public Long getEarlyRepaymentBanPeriod() {
		return earlyRepaymentBanPeriod;
	}

	/**
	 * Устанавливает период запрета досрочного погашения.
	 * @param earlyRepaymentBanPeriod период запрета досрочного погашения
	 */
	public void setEarlyRepaymentBanPeriod(Long earlyRepaymentBanPeriod) {
		this.earlyRepaymentBanPeriod = earlyRepaymentBanPeriod;
	}

	/**
	 * Возвращает комиссия за выдачу.
	 * @return комиссия за выдачу
	 */
	public BigDecimal getArrangementFee() {
		return arrangementFee;
	}

	/**
	 * Устанавливает комиссия за выдачу.
	 * @param arrangementFee комиссия за выдачу
	 */
	public void setArrangementFee(BigDecimal arrangementFee) {
		this.arrangementFee = arrangementFee;
	}

	/**
	 * Возвращает список комиссий стоимостных условий.
	 * @return список комиссий стоимостных условий
	 */
	public List<Comission> getComissions() {
		return comissions;
	}

	/**
	 * Устанавливает список комиссий стоимостных условий.
	 * @param comissions список комиссий стоимостных условий
	 */
	public void setComissions(List<Comission> comissions) {
		this.comissions = comissions;
	}

	/**
	 * Возвращает комиссия за сопровождение.
	 * @return комиссия за сопровождение
	 */
	public BigDecimal getSupportComission() {
		return supportComission;
	}

	/**
	 * Устанавливает комиссия за сопровождение.
	 * @param supportComission комиссия за сопровождение
	 */
	public void setSupportComission(BigDecimal supportComission) {
		this.supportComission = supportComission;
	}

	/**
	 * Возвращает срок сделки в днях
	 * @return срок сделки в днях
	 */
	public Long getPeriodInDays() {
		BigDecimal result = getPeriod(period, PeriodDimension.DAYS);
		if (result == null)
			return null;
		else
			return result.round(new MathContext(1)).longValue();
	}

	/**
	 * Возвращает срок сделки в годях
	 * @return срок сделки в годях
	 */
	public BigDecimal getPeriodInYears() {
		PeriodDimension currentPeriodDimension = null;
		if (periodDimension == null || period == null)
			return null;
		currentPeriodDimension = PeriodDimension.find(this.periodDimension);
		if (currentPeriodDimension == null)
			return null;
		if (PeriodDimension.DAYS == currentPeriodDimension) {//особый случай учёта високосного года
			//ближайший год високосный
			return BigDecimal.valueOf(period - 1L).divide(BigDecimal.valueOf(365L), MathContext.DECIMAL32);
		} else
			return getPeriod(period, PeriodDimension.YEARS);
	}

	/**
	 * Возвращает маржа для сделки.
	 * @return маржа для сделки
	 */
	public BigDecimal getCreditDealMargin() {
		BigDecimal loanRate = BigDecimal.ZERO;
		BigDecimal managementFee = BigDecimal.ZERO;
		BigDecimal managementFeeCalculated = getManagementFee();
		BigDecimal fixingRateSpread = this.fixingRateSpread != null ? this.fixingRateSpread : BigDecimal.ZERO;
		BigDecimal earlyRepaymentSpread = this.earlyRepaymentSpread != null ? this.earlyRepaymentSpread : BigDecimal.ZERO;

		if (interestRates != null
				&& interestRates.size() > 0
				&& interestRates.get(0) != null
				&& interestRates.get(0).getLoanRate() != null)
			loanRate = interestRates.get(0).getLoanRate();

		if (managementFeeCalculated != null)
			managementFee = managementFeeCalculated;

		return loanRate.add(managementFee)
					   .subtract(fixingRateSpread)
					   .subtract(earlyRepaymentSpread);
	}

	/**
	 * Возвращает комиссия за организацию.
	 * @return комиссия за организацию
	 */
	public BigDecimal getManagementFee() {
		if (isProduct()) {
			return arrangementFee;
		} else {
			if (comissions != null
					&& comissions.size() > 0
					&& comissions.get(0) != null)
				return comissions.get(0).getAnnualValue();
			else
				return null;
		}
	}

	/**
	 * Возвращает Срок использования, мес.
	 * @return Срок использования, мес.
	 */
	public Long getDrawdownDateInMonthCalculated() {
		if (usePeriod != null) {
			BigDecimal result = BigDecimal.valueOf(usePeriod).divide(BigDecimal.valueOf(
					PeriodDimension.MONTH.getCoeff()), MathContext.DECIMAL32).setScale(0, RoundingMode.HALF_UP);
			return result.longValue();
		} else if (useDate != null && proposedDtSigning != null) {
			BigDecimal result = BigDecimal.valueOf(useDate.getTime() - proposedDtSigning.getTime())
										  .divide(BigDecimal.valueOf(DateUtils.MILLIS_PER_DAY), MathContext.DECIMAL32)
										  .divide(BigDecimal.valueOf(PeriodDimension.MONTH.getCoeff()), MathContext.DECIMAL32)
										  .setScale(0, RoundingMode.HALF_UP);
			return result.longValue() < 0 ? 0 : result.longValue();
		} else
			return null;
	}

	/**
	 * Возвращает срок погашения в месяцах.
	 * @return срок погашения в месяцах
	 */
	public Long getMaturityInMonth() {
		if (period != null) {
			BigDecimal result = getPeriod(period, PeriodDimension.MONTH);
			if (result == null)
				return null;
			else {
				BigDecimal scaled = result.setScale(0, RoundingMode.HALF_UP);
				return scaled.longValue();
				//return result.round(new MathContext(1)).longValue(); передумали, теперь не нужно округлять VTBSPO-922
			}
		} else if (validto != null && proposedDtSigning != null) {
			Long result = BigDecimal.valueOf(validto.getTime() - proposedDtSigning.getTime())
									.divide(BigDecimal.valueOf(DateUtils.MILLIS_PER_DAY), MathContext.DECIMAL32)
									.divide(BigDecimal.valueOf(PeriodDimension.MONTH.getCoeff()), new MathContext(1))
									.longValue();
			return result < 0 ? 0 : result;
		} else
			return null;
	}

	/**
	 * Возвращает расчетная сумма сделки в зависимости от признаков лимита выдачи и лимита задолженности.
	 * @return расчетная сумма сделки в зависимости от признаков лимита выдачи и лимита задолженности.
	 */
	public BigDecimal getMdTaskSumCalculated() {
		if (isProduct()) {
			if (limitSum)
				return limitIssueSum;
			else if (debtSum)
				return debtLimitSum;
			else
				return mdtaskSum;
		} else
			return mdtaskSum;
	}

	/**
	 * Возвращает сумма сделки в млн (доллар США).
	 * @return сумма сделки в млн (доллар США)
	 */
	public BigDecimal getMdTaskSumInUsd() {
		BigDecimal sum = null;
		if ((sum = getMdTaskSumCalculated()) == null
				|| currentCurrencyRate == null
				|| usdCurrencyRate == null)
			return null;

		return sum.multiply(currentCurrencyRate)
				  .divide(usdCurrencyRate, MathContext.DECIMAL128);
	}

	/**
	 * Перерасчет периода в необходимую размерность, учитывая текущую.
	 * @param period значение периода
	 * @param periodDimension необходимая размерность
	 * @return пересчитанный период
	 */
	private BigDecimal getPeriod(Long period, PeriodDimension periodDimension) {
		PeriodDimension currentPeriodDimension = null;
		if (period == null
				|| periodDimension == null
				|| (currentPeriodDimension = PeriodDimension.find(this.periodDimension)) == null)
			return null;

		if (periodDimension == currentPeriodDimension)
			return BigDecimal.valueOf(period);

		int currentOrdinal = currentPeriodDimension.ordinal();
		int targetOrdinal = periodDimension.ordinal();
		BigDecimal result = BigDecimal.valueOf(period);
		while (currentOrdinal != targetOrdinal) {
			if (currentOrdinal < targetOrdinal)
				result = result.divide(BigDecimal.valueOf(PeriodDimension.find(++currentOrdinal).getCoeff()),
									   MathContext.DECIMAL32);

			if (currentOrdinal > targetOrdinal)
				result = result.multiply(BigDecimal.valueOf(PeriodDimension.find(currentOrdinal--).getCoeff()));
		}
		return result;
	}

	public boolean isAdditionalContract() {
		return additionalContract;
	}

	public void setAdditionalContract(boolean additionalContract) {
		this.additionalContract = additionalContract;
	}

	public boolean isProductMonitoring() {
		return productMonitoring;
	}

	public void setProductMonitoring(boolean productMonitoring) {
		this.productMonitoring = productMonitoring;
	}

	/**
	 * Возвращает версия.
	 * @return версия
	 */
	public Long getVersion() {
		return version == null ? 1L : version;
	}

	/**
	 * Устанавливает версия.
	 * @param version версия
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

	public String getPayInt() {
		return payInt;
	}

	public void setPayInt(String payInt) {
		this.payInt = payInt;
	}

	/**
	 * Возвращает тип процесса.
	 * @return тип процесса
	 */
	public ProcessType getProcessType() {
		return ProcessType.find(processname);
	}

	/**
	 * Возвращает наименование проекта (для процесса Pipeline).
	 * @return наименование проекта
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Устанавливает наименование проекта (для процесса Pipeline).
	 * @param projectName наименование проекта
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectClass() {
		return projectClass;
	}

	public void setProjectClass(String projectClass) {
		this.projectClass = projectClass;
	}

	public String getProjectIndustry() {
		return projectIndustry;
	}

	public void setProjectIndustry(String projectIndustry) {
		this.projectIndustry = projectIndustry;
	}

	public String getProjectRegion() {
		return projectRegion;
	}

	public void setProjectRegion(String projectRegion) {
		this.projectRegion = projectRegion;
	}

	public String getProjectRating1() {
		return projectRating1;
	}

	public void setProjectRating1(String projectRating1) {
		this.projectRating1 = projectRating1;
	}

	public String getProjectRating2() {
		return projectRating2;
	}

	public void setProjectRating2(String projectRating2) {
		this.projectRating2 = projectRating2;
	}

	public String getProjectRating3() {
		return projectRating3;
	}

	public void setProjectRating3(String projectRating3) {
		this.projectRating3 = projectRating3;
	}

	public String getProjectRating4() {
		return projectRating4;
	}

	public void setProjectRating4(String projectRating4) {
		this.projectRating4 = projectRating4;
	}

	public Long getCcCacheStatusid() {
		return ccCacheStatusid;
	}

	public void setCcCacheStatusid(Long ccCacheStatusid) {
		this.ccCacheStatusid = ccCacheStatusid;
	}

	public String getStatusreturn() {
		return statusreturn;
	}

	public void setStatusreturn(String statusreturn) {
		this.statusreturn = statusreturn;
	}

	public String getNumberAndVersion() {
		return getMdtaskNumber() + " версия " + getVersion();
	}

	public Long getIdInstance() {
		return idInstance;
	}

	public void setIdInstance(Long idInstance) {
		this.idInstance = idInstance;
	}

	public String getCrossSellName() {
		return crossSellName;
	}

	public void setCrossSellName(String crossSellName) {
		this.crossSellName = crossSellName;
	}

	public Long getCrossSellId() {
		return crossSellId;
	}

	public void setCrossSellId(Long crossSellId) {
		this.crossSellId = crossSellId;
	}

	public boolean isSupplyexist() {
		return supplyexist;
	}

	public void setSupplyexist(boolean supplyexist) {
		this.supplyexist = supplyexist;
	}

	public Long getCreditDocumentary() {
		if (isCrossSell())//все кросс-селл всегда не кредитные и не документарные
			return 0L;
		if (isProduct()) {//Осн.параметры-Вид сделки входит в семейство Кредитование или
			//Вид сделки из семейства Документарные операции или Банковские гарантии
            if (getProductFamily() == null)
                return 0L;
            if (getProductFamily().equalsIgnoreCase("Кредитование"))
                return 1L;
            if (getProductFamily().equalsIgnoreCase("Документарные операции"))
                return 2L;
            if (getProductFamily().equalsIgnoreCase("Банковские гарантии"))
                return 2L;
		}
		if (isLimit()) {//атрибут Основные параметры-Вид Лимита/Сублимита=Кредитный или Документарный
            if (getNameLimitType() == null)
                return 0L;
            if (getNameLimitType().equalsIgnoreCase("Кредитный"))
                return 1L;
            if (getNameLimitType().equalsIgnoreCase("Документарный"))
                return 2L;
		}
		return 0L;
	}

    /**
     * Returns .
     * @return
     */
    public String getProductFamily() {
        return productFamily;
    }

    /**
     * Sets .
     * @param productFamily
     */
    public void setProductFamily(String productFamily) {
        this.productFamily = productFamily;
    }

    /**
     * Returns .
     * @return
     */
    public String getNameLimitType() {
        return nameLimitType;
    }

    /**
     * Sets .
     * @param nameLimitType
     */
    public void setNameLimitType(String nameLimitType) {
        this.nameLimitType = nameLimitType;
    }

	/**
	 * Returns .
	 * @return
	 */
	public String getInitdep() {
		return initdep;
	}

	/**
	 * Sets .
	 * @param initdep
	 */
	public void setInitdep(String initdep) {
		this.initdep = initdep;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getEkgroup() {
		return ekgroup;
	}

	/**
	 * Sets .
	 * @param ekgroup
	 */
	public void setEkgroup(String ekgroup) {
		this.ekgroup = ekgroup;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getMapStatus() {
		return mapStatus;
	}

	/**
	 * Sets .
	 * @param mapStatus
	 */
	public void setMapStatus(String mapStatus) {
		this.mapStatus = mapStatus;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getMeetingProposedDate() {
		return meetingProposedDate;
	}

	/**
	 * Sets .
	 * @param meetingProposedDate
	 */
	public void setMeetingProposedDate(Date meetingProposedDate) {
		this.meetingProposedDate = meetingProposedDate;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Integer getInitdepartment() {
		return initdepartment;
	}

	/**
	 * Sets .
	 * @param initdepartment
	 */
	public void setInitdepartment(Integer initdepartment) {
		this.initdepartment = initdepartment;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getQuestionGroup() {
		return questionGroup;
	}

	/**
	 * Sets .
	 * @param questionGroup
	 */
	public void setQuestionGroup(Long questionGroup) {
		this.questionGroup = questionGroup;
	}

	/**
	 * Returns .
	 * @return
	 */
	public boolean isUserInProjectTeam() {
		return userInProjectTeam;
	}

	/**
	 * Sets .
	 * @param userInProjectTeam
	 */
	public void setUserInProjectTeam(boolean userInProjectTeam) {
		this.userInProjectTeam = userInProjectTeam;
	}
}
