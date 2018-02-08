package ru.masterdm.spo.dashboard.model;

import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure.MLN_RUB;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure.MLN_RUB;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure.TERM_MONTH;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure.PERCENT;

/**
 * @author pmasalov
 */
public enum SummaryFigureOther implements SummaryFigure {
    SUM_RUB_NEW("Объем новых сделок", MLN_RUB, true),
    SUM_RUB_FIX("Объем одобренных сделок", MLN_RUB, true),
    AVG_SUM_RUB_SELECTED("Средняя сумма выбранных сделок", MLN_RUB, true),
    SUM_RUB_LOST("Объем отказанных сделок", MLN_RUB, true),
    WAV_MARGIN_NEW("Средняя маржа новых сделок (NIM)", PERCENT, true),
    AVG_WAL_NEW("Средневзвешенный срок новых сделок (WAL)", TERM_MONTH, true),
    WAV_MARGIN_FIX("Средняя маржа одобренных сделок (NIM)", PERCENT, true),
    // non default
    SUM_RUB_SELECTED("Объем выбранных сделок", MLN_RUB, false),
    AVG_SUM_RUB_NEW("Средняя сумма новых сделок", MLN_RUB, false),
    AVG_SUM_RUB_FIX("Средняя сумма одобренных сделок", MLN_RUB, false),
    AVG_SUM_RUB_LOST("Средняя сумма отказанных сделок", MLN_RUB, false),
    AVG_PERIOD_MONTH_NEW("Средний срок новых сделок", TERM_MONTH, false),
    AVG_PERIOD_MONTH_FIX("Средний срок одобренных сделок", TERM_MONTH, false),
    AVG_PERIOD_MONTH_SELECTED("Средний срок выбранных сделок", TERM_MONTH, false),
    AVG_PERIOD_MONTH_LOST("Средний срок отказанных сделок", TERM_MONTH, false),
    WAV_MARGIN_SELECTED("Средняя маржа выбранных сделок (NIM)", PERCENT, false),
    WAV_MARGIN_LOST("Средняя маржа отказанных сделок (NIM)", PERCENT, false),
    AVG_WAL_FIX("Средневзвешенный срок одобренных сделок (WAL)", TERM_MONTH, false),
    AVG_WAL_SELECTED("Средневзвешенный срок выбранных сделок (WAL)", TERM_MONTH, false),
    AVG_WAL_LOST("Средневзвешенный срок отказанных сделок (WAL)", TERM_MONTH, false),
    SUM_LINE_COUNT_FIX("Объем утилизации", MLN_RUB, false),
    AVG_LINE_COUNT_SELECTED("Средняя сумма выборки", MLN_RUB, false),
    SUM_AVAILIBLE_LINE_VOLUME_FIX("Объем линии, доступный для выборки", MLN_RUB, false);
    //old
    //DEAL_AVG_AMOUNT("Средняя сумма сделок", MLN_RUB, false),
    //AVG_PERIOD("Средний срок", MLN_RUB, false),
    //WAV_MARGIN("Средняя маржа (NIM)", PERCENT, true),
    //PROFIT("Ожидаемый доход", MLN_RUB, false),
    //WAL("Средневзвешенный срок сделки (WAL)", TERM_MONTH, false),
    //LINE_COUNT("Объем утилизации", PERCENT, false),
    //AVG_LINE_COUNT("Средняя сумма выборки", MLN_RUB, false),
    //AVAILIBLE_LINE_VOLUME("Объем линии, доступный для выборки", MLN_RUB, false),
    //DEAL_AMOUNT_PROB("Сумма сделки с учетом вероятности", MLN_RUB, false),
    //AVG_WEEKS("Среднее кол-во недель в пайплайне", null, false);

    private String description;
    private boolean bdefault;
    private String unitOfMeasure;

    private SummaryFigureOther(String description, String unitOfMeasure, boolean bdefault) {
        this.description = description;
        this.bdefault = bdefault;
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    @Override
    public boolean isDefault() {
        return bdefault;
    }

    @Override
    public String getCode() {
        return toString();
    }
}
