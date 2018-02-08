package ru.masterdm.spo.service;

import java.math.BigDecimal;

/**
 * Created by Andrey Pavlenko
 */
public interface IPriceService {
    /**
     Возвращает комиссию за выдачу в процентах годовых.
     */
    BigDecimal getComissionZaVidSum(Long mdtaskid);

    /**
     * Устанавливает статус и вероятность закрытия.
     * @param mdTaskId код заявки
     * @param status статус
     */
    void setCloseProbabilityByStatus(Long mdTaskId, String status);
}
