package ru.masterdm.spo.logic;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.monitoring.MonitoringService;
import ru.masterdm.spo.SpoWsException;
import ru.masterdm.spo.integration.domain.DealPercent;
import ru.masterdm.spo.integration.domain.Percent;
import ru.masterdm.spo.integration.domain.Rate;
import ru.md.domain.percenthistory.DealPercentHistory;
import ru.md.domain.percenthistory.FactPercentHistory;
import ru.md.domain.percenthistory.IndrateHistory;
import ru.md.persistence.MdTaskMapper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Логика работы с процентной ставкой
 * @author akirilchev@masterdm.ru
 */
public class RateLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLogic.class.getName());

    /**
     * Конструктор
     */
    private RateLogic() {
    }

    /**
     * Конструктор
     * @param mdTaskMapper {@link MdTaskMapper маппер работы с заявкой}
     */
    public RateLogic(MdTaskMapper mdTaskMapper) {
        this.mdTaskMapper = mdTaskMapper;
    }

    private MdTaskMapper mdTaskMapper;

    /**
     * Возвращает {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
     *
     * @param creditDealNumber {@link Long номер} кредитной сделки
     * @param percentSumSanctionForDeal {@link Long размер санкций} по сделке, если вызываем из модуля Мониторинг
     * @param idDealPayment {@link Long id} заявки на выдачу, если вызываем из модуля Выдача
     * @return {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
     * @throws SpoWsException {@link SpoWsException ошибка}
     */
    @WebMethod
    public List<DealPercent> getDealPercentHistories(@WebParam(name = "creditDealNumber") Long creditDealNumber, @WebParam(name = "percentSumSanctionForDeal") BigDecimal percentSumSanctionForDeal, @WebParam(name = "idDealPayment") Long idDealPayment) throws SpoWsException {
        try {
            if (creditDealNumber == null)
                throw new Exception("creditDealNumber is null");

            List<DealPercentHistory> dealPercentHistories = mdTaskMapper.getDealPercentHistories(creditDealNumber, null, null, null, null, true);
            DealPercentHistory dealPercentHistory = (dealPercentHistories != null && !dealPercentHistories.isEmpty()) ? dealPercentHistories.get(dealPercentHistories.size() - 1) : null;

            LOGGER.debug("============RateLogic.getDealPercentHistories[creditDealNumber, percentSumSanctionForDeal, idDealPayment] hasHistory '" + (dealPercentHistory != null) + "'");

            return getDealPercentHistories(dealPercentHistory, creditDealNumber, percentSumSanctionForDeal, idDealPayment, false);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SpoWsException(e.getMessage(), e);
        }
    }

    /**
     * Возвращает {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
     *
     * @param idCreditDeal {@link Long id} кредитной сделки
     * @param percentSumSanctionForDeal {@link Long размер санкций} по сделке, если вызываем из модуля Мониторинг
     * @param idDealPayment {@link Long id} заявки на выдачу, если вызываем из модуля Выдача
     * @return {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
     * @throws SpoWsException {@link SpoWsException ошибка}
     */
    @WebMethod
    public List<DealPercent> getNotConfirmedDealPercentHistories(@WebParam(name = "idCreditDeal") Long idCreditDeal, @WebParam(name = "percentSumSanctionForDeal") BigDecimal percentSumSanctionForDeal, @WebParam(name = "idDealPayment") Long idDealPayment) throws SpoWsException {
        try {
            if (idCreditDeal == null)
                throw new Exception("idCreditDeal is null");

            Long creditDealNumber = mdTaskMapper.getMdTaskNumberById(idCreditDeal);

            List<DealPercentHistory> dealPercentHistories = mdTaskMapper.getDealPercentHistories(creditDealNumber, null, null, null, null, true);

            DealPercentHistory dealPercentHistory = (dealPercentHistories != null && !dealPercentHistories.isEmpty()) ? dealPercentHistories.get(dealPercentHistories.size() - 1) : null;
            boolean hasHistory = true;

            if (dealPercentHistory == null) {
                hasHistory = false;
                dealPercentHistories = mdTaskMapper.getNotConfirmedDealPercentHistories(idCreditDeal);

                dealPercentHistory = (dealPercentHistories != null && !dealPercentHistories.isEmpty()) ? dealPercentHistories.get(dealPercentHistories.size() - 1) : null;
            }

            LOGGER.debug("============RateLogic.getNotConfirmedDealPercentHistories[idCreditDeal, percentSumSanctionForDeal, idDealPayment] idCreditDeal '" + idCreditDeal + "', creditDealNumber '" + creditDealNumber + "', hasHistory '" + hasHistory + "', percentSumSanctionForDeal '" + percentSumSanctionForDeal + "'");

            return getDealPercentHistories(dealPercentHistory, creditDealNumber, percentSumSanctionForDeal, idDealPayment, true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SpoWsException(e.getMessage(), e);
        }
    }

    /**
     * Возвращает {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
     * @param dealPercentHistory {@link DealPercentHistory хронология изменения} общих данных для процентной ставки сделки
     * @param creditDealNumber номер сделки. Для уменьшения начиток может быть <code><b>null</b></code>, если <code><b>notConfirmed</b></code>=<code><b>true</b></code>
     * @param percentSumSanctionForDeal {@link Long размер санкций} по сделке, если вызываем из модуля Мониторинг
     * @param idDealPayment {@link Long id} заявки на выдачу, если вызываем из модуля Выдача
     * @param notConfirmed {@link Boolean признак} неодобренности сделки
     * @return {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
     * @throws Exception ошибка
     */
    private List<DealPercent> getDealPercentHistories(DealPercentHistory dealPercentHistory, Long creditDealNumber, BigDecimal percentSumSanctionForDeal, Long idDealPayment, boolean notConfirmed) throws Exception {
        try {
            List<DealPercent> results = new ArrayList<DealPercent>();
            if (dealPercentHistory != null) {
                if (!notConfirmed && percentSumSanctionForDeal == null) {
                    MonitoringService ws = ServiceFactory.getService(MonitoringService.class);
                    Double monitoringSumSanctionForDeal = ws.getPercentSumSanctionForDeal(creditDealNumber.toString());
                    percentSumSanctionForDeal = BigDecimal.valueOf(monitoringSumSanctionForDeal);
                }

                DealPercent result = new DealPercent();
                results.add(result);

                result.setIdCreditDeal(dealPercentHistory.getIdCreditDeal());
                result.setIdHistory(dealPercentHistory.getId());
                if (dealPercentHistory.getPercentHistories() != null && !dealPercentHistory.getPercentHistories().isEmpty()) {
                    result.setPercents(new ArrayList<Percent>());
                    for (FactPercentHistory factPercentHistory : dealPercentHistory.getPercentHistories()) {
                        Percent newPercent = newPercent(factPercentHistory);
                        result.getPercents().add(newPercent);

                        if (factPercentHistory.getIndrateHistories() != null && !factPercentHistory.getIndrateHistories().isEmpty()) {
                            newPercent.setRates(new ArrayList<Rate>());

                            for (IndrateHistory indrateHistory : factPercentHistory.getIndrateHistories())
                                if (!(indrateHistory.getId() == null && indrateHistory.getRateType() == null)) {
                                    Rate newRate = newRate(indrateHistory, percentSumSanctionForDeal);
                                    newPercent.getRates().add(newRate);
                                }
                        }
                    }

                    if (!notConfirmed)
                        result.setActualOnDateRates(getActualOnDateRates(dealPercentHistory.getId(), result.getPercents(), percentSumSanctionForDeal, creditDealNumber, idDealPayment));
                }
                if (idDealPayment != null) {
                    Rate paymentRateByIdPayment = getRateByIdPayment(idDealPayment, percentSumSanctionForDeal);
                    if (paymentRateByIdPayment != null) {
                        result.setPaymentRateByIdPayment(paymentRateByIdPayment);
                        result.setIsPaymentRateNotActual(true);
                        if (result.getActualOnDateRates() != null)
                            for(Rate rate: result.getActualOnDateRates())
                                if (getIsRateEquals(paymentRateByIdPayment, rate)) {
                                    result.setIsPaymentRateNotActual(false);
                                    break;
                                }
                    }
                }
            }

            return results;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SpoWsException(e.getMessage(), e);
        }
    }

    /**
     * Возвращает {@link Boolean признак}, что ставки одинаковы по составному primary key (idFixed, idFloat)
     *
     * @param rate1 {@link Rate} ставка 1
     * @param rate2 {@link Rate} ставка 2
     * @return {@link Boolean признак}, что ставки одинаковы по составному primary key (idFixed, idFloat)
     */
    private boolean getIsRateEquals(Rate rate1, Rate rate2) {
        boolean isFloatEquals = rate1.getIdFloatHistory() != null && rate1.getIdFloatHistory().equals(rate2.getIdFloatHistory());
        boolean isFixedEquals = rate1.getIdFloatHistory() == null && rate2.getIdFloatHistory() == null && rate1.getIdFixedHistory() != null && rate1.getIdFixedHistory().equals(rate2.getIdFixedHistory());
        return isFloatEquals || isFixedEquals;
    }

    /**
     * Возвращает {@link List список} актуальных на текущую дату {@link Rate ставок}
     * MONITORING-607 Если на текущую системную дату есть период, то ищем последнюю одобренную завершенную выдачу в этом периоде - если такая выдача нашлась, то в поле "Процентная ставка"
     * показываем значение ставки этой выдачи - если такая выдача не нашлась, то в поле "Процентная ставка" показываем значение ставок из актуального периода/
     *
     * Если на текущую системную дату нет периода, то ищем последнюю одобренную завершенную выдачу, которая не попадает ни в один актуальный период (в последнем изменении ставок сделки) - если
     * такая выдача нашлась, то в поле "Процентная ставка" показываем значение ставки этой выдачи - если такая выдача не нашлась, то в поле "Процентная ставка" показываем значение ставок по
     * сделке
     * @param idMdTaskAudit {@link Long id} изменения хронологии сделки
     * @param percents {@link List список} всех {@link Percent периодов} по сделке, включая саму сделку
     * @param percentSumSanctionForDeal percentSumSanctionForDeal {@link Long размер санкций} по сделке
     * @param creditDealNumber {@link Long номер} сделки
     * @param idDealPaymentOpened {@link Long id} заявки на выдачу, в которой отображаем секцию
     * @return {@link List список} актуальных на текущую дату {@link Rate ставок}
     */
    private List<Rate> getActualOnDateRates(Long idMdTaskAudit, List<Percent> percents, BigDecimal percentSumSanctionForDeal, Long creditDealNumber, Long idDealPaymentOpened) {
        List<Rate> results = new ArrayList<Rate>();
        if (idDealPaymentOpened == null) {// для мониторинга
            Long idLastPayment = mdTaskMapper.getIdLastPaymentInPeriod(creditDealNumber, idMdTaskAudit);
            if (idLastPayment != null) {
                Rate rate = getRateByIdPayment(idLastPayment, percentSumSanctionForDeal);
                if (rate != null)
                    results.add(rate);
            }
        }

        if (results.isEmpty() && percents != null) {
            Boolean hasActualPeriodsOnDate = false;
            Long periodCount = 0L;
            for (Percent percent : percents)
                if (!getIsDealPeriod(percent)) {
                    periodCount++;
                    if (isPeriodActualOnCurrentDate(percent.getStartDate(), percent.getEndDate()))
                        hasActualPeriodsOnDate = true;
                }
            Boolean moreThenOnePeriod = periodCount > 1L;

            if (moreThenOnePeriod && hasActualPeriodsOnDate) {
                for (Percent percent : percents)
                    if (!getIsDealPeriod(percent) && isPeriodActualOnCurrentDate(percent.getStartDate(), percent.getEndDate()))
                        results.addAll(percent.getRates());
            }
            else
                for (Percent percent : percents)
                    if (getIsDealPeriod(percent))
                        results.addAll(percent.getRates());
        }
        return results;
    }

    /**
     * Возвращает {@link Rate ставку}, выбранную в заявке на выдачу, заданной по {@link Long id} заявки на выдачу
     *
     * @param idPayment {@link Long id} заявки на выдачу
     * @param percentSumSanctionForDeal percentSumSanctionForDeal {@link Long размер санкций} по сделке
     * @return {@link Rate ставка}
     */
    private Rate getRateByIdPayment(Long idPayment, BigDecimal percentSumSanctionForDeal) {
        Rate result = null;
        if (idPayment != null) {
            IndrateHistory indrateHistory = mdTaskMapper.getIndrateHistoryByIdPayment(idPayment);
            if (indrateHistory == null) {
                FactPercentHistory factPercentHistory = mdTaskMapper.getFactPercentHistoryByIdPayment(idPayment);
                if (factPercentHistory != null)
                    indrateHistory = new IndrateHistory(factPercentHistory);
            }
            if (indrateHistory != null)
                result = newRate(indrateHistory, percentSumSanctionForDeal);
        }
        return result;
    }

    /**
     * Возвращает {@link Boolean признак} общего периода по сделке
     *
     * @param percent {@link Percent период} или общий период по сделке
     * @return {@link Boolean признак} общего периода по сделке
     */
    private Boolean getIsDealPeriod(Percent percent) {
        return (percent.getPeriodNumber() == null);
    }

    /**
     * Возвращает {@link Boolean признак} попадает ли текущая {@link Date дата} в интервал заданный по {@link Date дате начала} и {@link Date дате окончания}, включая границы
     *
     * @param startDate {@link Date дата начала}
     * @param endDate {@link Date дата окончания}
     * @return {@link Boolean признак} попадает ли текущая {@link Date дата} в интервал заданный по {@link Date дате начала} и {@link Date дате окончания}, включая границы
     */
    private Boolean isPeriodActualOnCurrentDate(Date startDate, Date endDate) {
        Date curDate = DateUtils.truncate(new Date(), Calendar.DATE);
        startDate = startDate != null ? DateUtils.truncate(startDate, Calendar.DATE) : curDate;
        endDate = endDate != null ? DateUtils.truncate(endDate, Calendar.DATE) : curDate;

        return (curDate.compareTo(startDate) >= 0 && curDate.compareTo(endDate) <= 0);
    }

    /**
     * Возвращает заполненный {@link Percent период}
     *
     * @param factPercentHistory {@link FactPercentHistory объект} хронологии процентных периодов
     * @return заполненный {@link Percent период}
     */
    private Percent newPercent(FactPercentHistory factPercentHistory) {
        Percent newPercent = new Percent();
        newPercent.setPositiveRateCount(factPercentHistory.getIndrateHistoryCount());
        newPercent.setInterestRateDerivative(factPercentHistory.getInterestRateDerivative());
        newPercent.setInterestRateFixed(factPercentHistory.getInterestRateFixed());
        newPercent.setPeriodNumber(factPercentHistory.getPeriodNumber());
        newPercent.setStartDate(factPercentHistory.getStartDate());
        newPercent.setEndDate(factPercentHistory.getEndDate());
        return newPercent;
    }

    /**
     * Возвращает заполненную {@link Rate ставку}
     *
     * @param indrateHistory {@link IndrateHistory хронология индикативной ставки}
     * @param percentSumSanctionForDeal percentSumSanctionForDeal {@link Long размер санкций} по сделке
     * @return {@link Rate ставка}
     */
    private Rate newRate(IndrateHistory indrateHistory, BigDecimal percentSumSanctionForDeal) {
        Rate newRate = new Rate();

        // записывается либо IdFixedHistory, либо IdFloatHistory
        newRate.setIdFixedHistory(indrateHistory.getId() != null ? null : indrateHistory.getIdFactpercent());
        newRate.setIdFloatHistory(indrateHistory.getId());
        newRate.setRateType(indrateHistory.getRateType());
        newRate.setReason(indrateHistory.getReason());
        newRate.setStartDate(indrateHistory.getStartDate());
        newRate.setEndDate(indrateHistory.getEndDate());
        newRate.setValue(indrateHistory.getValue());
        newRate.setValueComment(indrateHistory.getValueComment());
        newRate.setAdditionValue(indrateHistory.getAdditionValue());
        newRate.setRateShortType(indrateHistory.getRateShortType());
        newRate.setRateCrmId(indrateHistory.getRateCrmId());
        newRate.setRatePart1of3(indrateHistory.getRatePart1of3());
        newRate.setRatePart2of3(indrateHistory.getRatePart2of3());
        newRate.setRatePart3of3(indrateHistory.getRatePart3of3());

        BigDecimal fullValueWithSanction = BigDecimal.ZERO;
        if (indrateHistory.getValue() != null)
            fullValueWithSanction = fullValueWithSanction.add(indrateHistory.getValue());
        if (indrateHistory.getAdditionValue() != null)
            fullValueWithSanction = fullValueWithSanction.add(indrateHistory.getAdditionValue());
        if (percentSumSanctionForDeal != null)
            fullValueWithSanction = fullValueWithSanction.add(percentSumSanctionForDeal);
        newRate.setFullValueWithSanction(fullValueWithSanction);
        return newRate;
    }
}
