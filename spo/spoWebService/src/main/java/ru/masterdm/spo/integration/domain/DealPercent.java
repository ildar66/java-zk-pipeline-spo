package ru.masterdm.spo.integration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Хронология изменения общих данных процентной ставки по сделке
 *
 * @author akirilchev@masterdm.ru
 */
public class DealPercent implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	private Long idHistory;
	private Long idCreditDeal;
	
	private Long positiveRateCount;
	private List<Percent> percents;
	private List<Rate> actualOnDateRates;
	
	private Boolean isPaymentRateNotActual = false;
	private Rate paymentRateByIdPayment;

	/**
	 * Возвращает {@link Long первичный ключ} хронологии изменения общих данных сделки
	 *
	 * @return {@link Long первичный ключ} хронологии изменения общих данных сделки
	 */
	public Long getIdHistory() {
		return idHistory;
	}

	/**
	 * Устанавливает {@link Long первичный ключ} хронологии изменения общих данных сделки
	 *
	 * @param idHistory {@link Long первичный ключ} хронологии изменения общих данных сделки
	 */
	public void setIdHistory(Long idHistory) {
		this.idHistory = idHistory;
	}

	/**
	 * Возвращает {@link Long id} сделки
	 *
	 * @return {@link Long id} сделки
	 */
	public Long getIdCreditDeal() {
		return idCreditDeal;
	}

	/**
	 * Устанавливает {@link Long id} сделки
	 *
	 * @param idCreditDeal {@link Long id} сделки
	 */
	public void setIdCreditDeal(Long idCreditDeal) {
		this.idCreditDeal = idCreditDeal;
	}
	
	/**
	 * Возвращает {@link Boolean признак}, что выбранное в модуле Выдача значение стало неактуальным. Так как после этого в том же процентном периоде были еще изменения ставки
	 *
	 * @return {@link Boolean признак}, что выбранное в модуле Выдача значение стало неактуальным. Так как после этого в том же процентном периоде были еще изменения ставки
	 */
	public Boolean getIsPaymentRateNotActual() {
		return isPaymentRateNotActual;
	}

	/**
	 * Устанавливает {@link Boolean признак}, что выбранное в модуле Выдача значение стало неактуальным. Так как после этого в том же процентном периоде были еще изменения ставки
	 *
	 * @param isPaymentRateNotActual {@link Boolean признак}, что выбранное в модуле Выдача значение стало неактуальным. Так как после этого в том же процентном периоде были еще изменения ставки
	 */
	public void setIsPaymentRateNotActual(Boolean isPaymentRateNotActual) {
		this.isPaymentRateNotActual = isPaymentRateNotActual;
	}

	/**
	 * Устанавливает {@link List список} {@link Percent объектов хронологии} процентной ставки сделки 
	 *
	 * @param percents {@link List список} {@link Percent объектов хронологии} процентной ставки сделки
	 */
	public void setPercents(List<Percent> percents) {
		this.percents = percents;
	}

	/**
	 * Возвращает {@link List список} {@link Percent объектов хронологии} процентной ставки сделки
	 *
	 * @return {@link List список} {@link Percent объектов хронологии} процентной ставки сделки
	 */
	public List<Percent> getPercents() {
		return percents;
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
	 * Возвращает {@link List список} актуальных на текущую дату {@link Rate ставок}
	 *
	 * @return {@link List список} актуальных на текущую дату {@link Rate ставок}
	 */
	public List<Rate> getActualOnDateRates() {
		return actualOnDateRates;
	}

	/**
	 * Устанавливает {@link List список} актуальных на текущую дату {@link Rate ставок}
	 *
	 * @param actualOnDateRates {@link List список} актуальных на текущую дату {@link Rate ставок}
	 */
	public void setActualOnDateRates(List<Rate> actualOnDateRates) {
		this.actualOnDateRates = actualOnDateRates;
	}

	/**
	 * Возвращает {@link Rate ставку} выдачи, используемыую в конкретной выдаче заданной по {@link Long id} 
	 *
	 * @return {@link Rate ставку} выдачи, используемыую в конкретной выдаче заданной по {@link Long id}
	 */
	public Rate getPaymentRateByIdPayment() {
		return paymentRateByIdPayment;
	}

	/**
	 * Устанавливает {@link Rate ставку} выдачи, используемыую в конкретной выдаче заданной по {@link Long id}
	 *
	 * @param paymentRateByIdPayment {@link Rate ставку} выдачи, используемыую в конкретной выдаче заданной по {@link Long id}
	 */
	public void setPaymentRateByIdPayment(Rate paymentRateByIdPayment) {
		this.paymentRateByIdPayment = paymentRateByIdPayment;
	}

}

