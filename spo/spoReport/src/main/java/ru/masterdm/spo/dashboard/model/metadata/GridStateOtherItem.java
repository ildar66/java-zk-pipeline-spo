package ru.masterdm.spo.dashboard.model.metadata;

import static ru.masterdm.spo.dashboard.PipelineConstants.Formats;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure;

import ru.masterdm.spo.dashboard.PipelineConstants;

/**
 * @author pmasalov
 */
public enum GridStateOtherItem implements GridColumnItem {
    // Основные атрибуты, «Сделки»
    STATE("Состояние", null, "statusName", true, null, null, false),
    COUNT_ALL("Количество", UnitOfMeasure.PIECE, "countAll", true, null, null, true, true),
    SUM_RUB("Сумма", UnitOfMeasure.MLN_RUB, "sumRub", true, Formats.NUMBER_FORMAT, null, true, true),
    SUM_USD("Сумма", UnitOfMeasure.MLN_USD, "sumUsd", true, Formats.NUMBER_FORMAT, null, true, true),
    AVG_PERIOD_MONTH("Средний срок", UnitOfMeasure.TERM_MONTH, "avgPeriodMonth", true, Formats.INT_FORMAT, null, true, true),
    WAV_MARGIN("Средняя маржа", UnitOfMeasure.PERCENT, "wavMargin", true, Formats.NUMBER_FORMAT, null, true, true),
    //SUM_PROFIT("Ожидаемый доход", UnitOfMeasure.MLN_RUB, "sumProfit", true, Formats.NUMBER_FORMAT, null, true),
    // Доп атрибуты, «Сделки»
    AVG_WAL("Средневзвешенный срок сделки (WAL)", UnitOfMeasure.TERM_MONTH, "avgWal", false, Formats.INT_FORMAT, null, true, true),
    SUM_LINE_COUNT("Объем утилизации", UnitOfMeasure.MLN_RUB, "sumLineCount", false, Formats.NUMBER_FORMAT, null, true, true),
        AVG_LINE_COUNT("Средняя сумма выборки", UnitOfMeasure.MLN_RUB, "avgLineCount", false, Formats.NUMBER_FORMAT, null, true, true),
    SUM_AVAILABLE_LINE_VOLUME("Объем линии, доступный для выборки", UnitOfMeasure.MLN_RUB, "sumAvailibleLineVolume", false, Formats.NUMBER_FORMAT, null, true, true),
    SUM_RUB_PROB("Сумма сделок с учетом вероятности", UnitOfMeasure.MLN_RUB, "sumRubProb", false, Formats.NUMBER_FORMAT, null, true, true),
    AVG_WEEKS("Среднее кол-во недель в пайплайне", null, "avgWeeks", false, null, null, true, true),
    AVG_SUM_RUB("Средняя сумма", UnitOfMeasure.MLN_RUB, "avgSumRub", false, Formats.NUMBER_FORMAT, null, true, true );

    private String description;
    private String unitOfMeasure;
    private String code;
    private boolean _default;
    private String numberFormat;
    private String dateFormat;
    private boolean label;
    private boolean right;

    GridStateOtherItem(String description, String unitOfMeasure, String code, boolean _default, String numberFormat, String dateFormat,
                       boolean label) {
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.code = code;
        this._default = _default;
        this.numberFormat = numberFormat;
        this.dateFormat = dateFormat;
        this.label = label;
    }

    GridStateOtherItem(String description, String unitOfMeasure, String code, boolean _default, String numberFormat, String dateFormat,
                       boolean label, boolean right) {
        this(description, unitOfMeasure, code, _default, numberFormat, dateFormat, label);
        this.right = right;
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
    public String getCode() {
        return code;
    }

    @Override
    public boolean isDefault() {
        return _default;
    }

    @Override
    public String getNumberFormat() {
        return numberFormat;
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public boolean isLabel() {
        return label;
    }

    @Override
    public boolean isRight() {
        return right;
    }

}
