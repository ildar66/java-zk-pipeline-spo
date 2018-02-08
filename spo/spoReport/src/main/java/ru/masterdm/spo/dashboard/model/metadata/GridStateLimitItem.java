package ru.masterdm.spo.dashboard.model.metadata;

import static ru.masterdm.spo.dashboard.PipelineConstants.Formats;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure;

/**
 * @author pmasalov
 */
public enum GridStateLimitItem implements GridColumnItem {
    // Основные атрибуты
    STATE("Состояние", null, "statusName", true, null, null, false),
    COUNT_ALL("Количество", UnitOfMeasure.PIECE, "countAll", true, null, null, true, true),
    SUM_RUB("Сумма", UnitOfMeasure.MLN_RUB, "sumRub", true, Formats.NUMBER_FORMAT, null, true, true),
    SUM_USD("Сумма", UnitOfMeasure.MLN_USD, "sumUsd", true, Formats.NUMBER_FORMAT, null, true, true),
    AVG_PERIOD_MONTH("Средний срок", UnitOfMeasure.TERM_MONTH, "avgPeriodMonth", true, null, null, true, true),
    AVG_WAL("Средневзвешенный срок Погашения (WAL)", UnitOfMeasure.TERM_MONTH, "avgWal", true, null, null, true, true),
    AVG_WEEKS("Среднее кол-во недель в пайплайне", null, "avgWeeks", true, null, null, true, true),
    // Доп атрибуты
    AVG_SUM_RUB("Средняя сумма", UnitOfMeasure.MLN_RUB, "avgSumRub", false, Formats.NUMBER_FORMAT, null, true, true);


    private String description;
    private String unitOfMeasure;
    private String code;
    private boolean _default;
    private String numberFormat;
    private String dateFormat;
    private boolean label;
    private boolean right = false;

    GridStateLimitItem(String description, String unitOfMeasure, String code, boolean _default, String numberFormat, String dateFormat,
                       boolean label) {
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.code = code;
        this._default = _default;
        this.numberFormat = numberFormat;
        this.dateFormat = dateFormat;
        this.label = label;
    }

    GridStateLimitItem(String description, String unitOfMeasure, String code, boolean _default, String numberFormat, String dateFormat,
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
