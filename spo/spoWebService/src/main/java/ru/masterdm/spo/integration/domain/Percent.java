package ru.masterdm.spo.integration.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Хронология изменения процентной ставки сделки 
 * 
 * @author akirilchev@masterdm.ru
 */
public class Percent implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	private Long periodNumber;

	private Boolean interestRateDerivative;
    private Boolean interestRateFixed;
    
    private Date startDate; 
    private Date endDate; 
    
    private Long positiveRateCount;
    private List<Rate> rates;

	/**
	 * Возвращает {@link Date дату} окончания периода
	 *
	 * @return {@link Date дата} окончания периода
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Устанавливает {@link Date дату} окончания периода
	 *
	 * @param endDate {@link Date дата} окончания периода
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Возвращает {@link Date дату} начала периода
	 *
	 * @return {@link Date дата} начала периода
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Устанавливает {@link Date дату} начала периода
	 *
	 * @param startDate {@link Date дата} начала периода
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Возвращает {@link Long номер} периода. Если период всего один, то поле не заполнено, <code><b>null</b></code> 
	 *
	 * @return {@link Long номер} периода. Если период всего один, то поле не заполнено, <code><b>null</b></code>
	 */
	public Long getPeriodNumber() {
		return periodNumber;
	}

	/**
	 * Устанавливает {@link Long номер} периода. Если период всего один, то поле не заполнено, <code><b>null</b></code>
	 *
	 * @param periodNumber {@link Long номер} периода. Если период всего один, то поле не заполнено, <code><b>null</b></code>
	 */
	public void setPeriodNumber(Long periodNumber) {
		this.periodNumber = periodNumber;
	}
	
	/**
	 * Возвращает {@link Boolean признак} выставленного плавающего типа ставки
	 *
	 * @return {@link Boolean признак} выставленного плавающего типа ставки
	 */
	public Boolean getInterestRateDerivative() {
		return interestRateDerivative;
	}

	/**
	 * Устанавливает {@link Boolean признак} выставленного плавающего типа ставки
	 *
	 * @param interestRateDerivative {@link Boolean признак} выставленного плавающего типа ставки
	 */
	public void setInterestRateDerivative(Boolean interestRateDerivative) {
		this.interestRateDerivative = interestRateDerivative;
	}

	/**
	 * Возвращает {@link Boolean признак} выставленного фиксированного типа ставки
	 *
	 * @return {@link Boolean признак} выставленного фиксированного типа ставки
	 */
	public Boolean getInterestRateFixed() {
		return interestRateFixed;
	}

	/**
	 * Устанавливает {@link Boolean признак} выставленного фиксированного типа ставки
	 *
	 * @param interestRateFixed {@link Boolean признак} выставленного фиксированного типа ставки
	 */
	public void setInterestRateFixed(Boolean interestRateFixed) {
		this.interestRateFixed = interestRateFixed;
	}

	/**
	 * Возвращает {@link Long количество} объектов хронологии ставки. Если <code><b>0</b></code> объектов, то возвращается <code><b>1</b></code>.
	 *
	 * @return {@link Long количество} объектов хронологии ставки. Если <code><b>0</b></code> объектов, то возвращается <code><b>1</b></code>.
	 */
	public Long getPositiveRateCount() {
		return positiveRateCount;
	}

	/**
	 * Устанавливает {@link Long количество} объектов хронологии ставки. Если <code><b>0</b></code> объектов, то возвращается <code><b>1</b></code>.
	 *
	 * @param positiveRateCount {@link Long количество} объектов хронологии ставки. Если <code><b>0</b></code> объектов, то возвращается <code><b>1</b></code>.
	 */
	public void setPositiveRateCount(Long positiveRateCount) {
		this.positiveRateCount = positiveRateCount;
	}

	/**
	 * Возвращает {@link List список} {@link Rate объектов хронологии} фиксированной и индикативной ставки
	 *
	 * @return {@link List список} {@link Rate объектов хронологии} фиксированной и индикативной ставки
	 */
	public List<Rate> getRates() {
		return rates;
	}

	/**
	 * Устанавливает {@link List список} {@link Rate объектов хронологии} фиксированной и индикативной ставки
	 *
	 * @param rates {@link List список} {@link Rate объектов хронологии} фиксированной и индикативной ставки
	 */
	public void setRates(List<Rate> rates) {
		this.rates = rates;
	}
	
}