package ru.masterdm.spo.service;

import java.util.Date;
import java.util.List;

import ru.md.domain.AuditDurationStage;

/**
 * Created by Andrey Pavlenko on 05.05.16.
 */
public interface IStandardPeriodService {
    /**
     * возвращает выбранный срок в рабочих днях для этапа и итерации. МОжет быть null, если не выбран.
     * @param mdtaskid айди заявки
     * @param groupname название этапа нормативных сроков
     * @param iterDate дата начала итерации
     * @param iterEnd дата окончания итерации
     */
    Long getStandardPeriodValue(Long mdtaskid, String groupname, Date iterDate, Date iterEnd);
}
