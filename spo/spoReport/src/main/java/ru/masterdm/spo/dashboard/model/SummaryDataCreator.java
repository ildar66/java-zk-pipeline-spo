package ru.masterdm.spo.dashboard.model;

import java.util.List;

import ru.masterdm.spo.dashboard.domain.SummaryData;

/**
 * @author pmasalov
 */
public interface SummaryDataCreator {

    List<SummaryData> getSummaryData();

    void clear();

    //  doSelect(SummaryData , boolean)
}
