package ru.md.domain.percenthistory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * Хронология изменения процентной ставки сделки 
 * 
 * @author akirilchev@masterdm.ru
 */
public class FactPercentHistory implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private List<IndrateHistory> indrateHistories;
	
	private BigDecimal rate4; // Ставка размещения
	
	private Date startDate; // период с
	
	private Date endDate; // период по
	
	private String rate4Description;
	
	private String reason;
	
	private Long periodNumber;
	
	private Boolean interestRateDerivative;
    private Boolean interestRateFixed;
    private Date rate4StartDate;
    private Date rate4EndDate;

	/**
	 * Возвращает {@link Long первичный ключ}
	 *
	 * @return {@link Long первичный ключ}
	 */
	@Id
	public Long getId() {
		return id;
	}

	/**
	 * Устанавливает {@link Long первичный ключ}
	 *
	 * @param id {@link Long первичный ключ}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 *
	 * @return {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 */
	public List<IndrateHistory> getIndrateHistories() {
		return indrateHistories;
	}

	/**
	 * Устанавливает {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 *
	 * @param indrateHistories {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 */
	public void setIndrateHistories(List<IndrateHistory> indrateHistories) {
		this.indrateHistories = indrateHistories;
	}


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
	@Column(name = "Start_Date")
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
	 * Возвращает {@link BigDecimal ставку} размещения
	 *
	 * @return {@link BigDecimal ставку} размещения
	 */
	public BigDecimal getRate4() {
		return rate4;
	}

	/**
	 * Устанавливает {@link BigDecimal ставку} размещения
	 *
	 * @param rate4 {@link BigDecimal ставку} размещения
	 */
	public void setRate4(BigDecimal rate4) {
		this.rate4 = rate4;
	}

	/**
	 * Возвращает комментарий к ставке размещения
	 *
	 * @return комментарий к ставке размещения
	 */
	public String getRate4Description() {
		return rate4Description;
	}

	/**
	 * Устанавливает комментарий к ставке размещения
	 *
	 * @param rate4Description комментарий к ставке размещения
	 */
	public void setRate4Description(String rate4Description) {
		this.rate4Description = rate4Description;
	}

	/**
	 * Возвращает основание для изменения ставки размещения
	 *
	 * @return основание для изменения ставки размещения
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Устанавливает основание для изменения ставки размещения
	 *
	 * @param reason основание для изменения ставки размещения
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * Возвращает {@link Long номер} периода
	 *
	 * @return {@link Long номер} периода
	 */
	public Long getPeriodNumber() {
		return periodNumber;
	}

	/**
	 * Устанавливает {@link Long номер} периода
	 *
	 * @param periodNumber {@link Long номер} периода
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
	 * Возвращает {@link Long количество} объектов хронологии изменения индикативной ставки. Если <code><b>0</b></code>, то <code><b>1</b></code>.
	 *
	 * @return {@link Long количество} объектов хронологии изменения индикативной ставки. Если <code><b>0</b></code>, то <code><b>1</b></code>.
	 */
	public Long getIndrateHistoryCount() {
		Long count = new Long(getIndrateHistories() != null ? getIndrateHistories().size() : 0);
		if (count.equals(0L))
			count = 1L;
		return count;
	}

	/**
	 * Возвращает {@link Date дату} начала применения ставки размещения
	 *
	 * @return {@link Date дата} начала применения ставки размещения
	 */
	public Date getRate4StartDate() {
		return rate4StartDate;
	}

	/**
	 * Устанавливает {@link Date дату} начала применения ставки размещения
	 *
	 * @param rate4StartDate {@link Date дата} начала применения ставки размещения
	 */
	public void setRate4StartDate(Date rate4StartDate) {
		this.rate4StartDate = rate4StartDate;
	}

	/**
	 * Возвращает {@link Date дату} окончания применения ставки размещения
	 *
	 * @return {@link Date дата} окончания применения ставки размещения
	 */
	public Date getRate4EndDate() {
		return rate4EndDate;
	}

	/**
	 * Устанавливает {@link Date дату} окончания применения ставки размещения
	 *
	 * @param rate4EndDate {@link Date дата} окончания применения ставки размещения
	 */
	public void setRate4EndDate(Date rate4EndDate) {
		this.rate4EndDate = rate4EndDate;
	}
	
}