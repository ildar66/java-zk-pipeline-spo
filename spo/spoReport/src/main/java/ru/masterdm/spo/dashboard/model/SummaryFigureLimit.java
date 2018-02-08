package ru.masterdm.spo.dashboard.model;

import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure.MLN_RUB;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure.TERM_MONTH;

/**
 * @author pmasalov
 */
public enum SummaryFigureLimit implements SummaryFigure {
    SUM_RUB_NEW("Объем новых лимитов", MLN_RUB, true),
    SUM_RUB_INPROGRESS("Объем лимитов в работе", MLN_RUB, true),
    SUM_RUB_ACCEPT("Объем одобренных лимитов", MLN_RUB, true),
    SUM_RUB_LOST("Объем отказанных лимитов", MLN_RUB, true),
    AVG_PERIOD_MONTH_INPROGRESS("Средний срок лимитов в работе", TERM_MONTH, true),
    AVG_WEEKS_INPROGRESS("Среднее количество недель в Pipeline по лимитам в работе", null, true),
    AVG_WEEKS_ACCEPT("Среднее количество недель в Pipeline по одобренным лимитам", null, true),
    // non default
    AVG_SUM_RUB_NEW("Средняя сумма новых лимитов", MLN_RUB, false),
    AVG_SUM_RUB_INPROGRESS("Средняя сумма лимитов в работе", MLN_RUB, false),
    AVG_SUM_RUB_ACCEPT("Средняя сумма одобренных лимитов", MLN_RUB, false),
    AVG_SUM_RUB_LOST("Средняя сумма отказанных лимитов", MLN_RUB, false),
    AVG_PERIOD_MONTH_NEW("Средний срок новых лимитов", TERM_MONTH, false),
    AVG_PERIOD_MONTH_ACCEPT("Средний срок одобренных лимитов", TERM_MONTH, false),
    AVG_PERIOD_MONTH_LOST("Средний срок отказанных лимитов", TERM_MONTH, false),
    AVG_WEEKS_NEW("Среднее количество недель в Pipeline по новым лимитам", null, false),
    AVG_WEEKS_LOST("Среднее количество недель в Pipeline по отказанным лимитам", null, false);
    // old
    //LIMIT_AVG_SUM("Средняя сумма лимита", MLN_RUB, false),
    //AVG_PERIOD_MONTH("Средний срок", TERM_MONTH, false),
    //AVG_WEEKS("Среднее кол-во недель в пайплайне", null, false);

    private String description;
    private boolean bdefault;
    private String unitOfMeasure;

    SummaryFigureLimit(String description, String unitOfMeasure, boolean bdefault) {
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
