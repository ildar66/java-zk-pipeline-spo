package ru.masterdm.spo.dashboard.model.metadata;

import static ru.masterdm.spo.dashboard.PipelineConstants.Formats;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure;

import ru.masterdm.spo.dashboard.PipelineConstants;

/**
 * @author pmasalov
 */
public enum GridTopLimitItem implements GridColumnItem {
    NN("#", null, "nn", true, null, null, true),
    // Основные аттрибуты
    MDTASK_NUMBER_AND_VERSION("№ версия заявки", null, "mdtaskNumber", true, null, null, false),
    WEEKS("Недель в PL", null, "weeksCalc", true, null, null, true, true),
    ORGNAME("Компания", null, "orgname", true, null, null, true),
    GROUPNAME("Группа компаний", null, "groupname", true, null, null, true),
    STAGE("Стадия", null, "statusPipeline", true, null, null, true),
    SUM("Сумма, валюта", null, "sum", true, Formats.NUMBER_FORMAT, null, true, true),
    PERIOD_MONTH("Срок", UnitOfMeasure.TERM_MONTH, "periodMonth", true, null, null, true, true),
    INDUSTRY("Отрасль", null, "industry", true, null, null, true),
    // Слегка дополнительные аттрибуты
    CONTRACTOR("Фондирующий Банк", null, "contractor", false, null, null, true),
    VTB_CONTRACTOR("Выдающий Банк", null, "vtbContractor", false, null, null, true),
    // Дополнительные аттрибуты
    INN("ИНН", null, "inn", false, null, null, true),
    SLX_CODE_KZ("SLX-код КЗ", null, "slxCodeKz", false, null, null, true),
    SLX_CODE_EK("SLX-код ЕК", null, "slxCodeEk", false, null, null, true),
    TREADE_DESK("Трейдинг Деск", null, "tradeDesc", false, null, null, true),
    PROLONGATION("Пролонгация", null, "prolongation", false, null, null, true),
    PUB("Возможность залога в ЦБ (312-П)", null, "pub", false, null, null, true),
    CMNT("Комментарии", null, "cmnt", false, null, null, true),
    PLAN_DATE("Плановая даты выборки", null, "planDate", false, null, Formats.DATE_FORMAT, true),
    WAL("Средневзвешенный срок Погашения (WAL)", UnitOfMeasure.TERM_MONTH, "wal", false, null, null, true, true),
    LINE_COUNT_RUB("Выбранный объем линии", UnitOfMeasure.MLN_RUB, "lineCountRub", false, Formats.NUMBER_FORMAT, null, true),
    AVAILABLE_LINE_VOLUME("Объем линии, доступный для выборки", UnitOfMeasure.MLN_RUB, "availibleLineVolumeRub", false, Formats.NUMBER_FORMAT, null, true),
    SUM_LAST_RUB("Оставшаяся сумма к выдаче с учетом вероятности", UnitOfMeasure.MLN_RUB, "sumLastRub", false, Formats.NUMBER_FORMAT, null, true, true),
    CLOSE_PROBABILITY("Вероятность закрытия", UnitOfMeasure.PERCENT, "closeProbability", false, null, null, true);

    GridTopLimitItem(String description, String unitOfMeasure, String code, boolean _default, String numberFormat, String dateFormat,
                     boolean label) {
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.code = code;
        this._default = _default;
        this.numberFormat = numberFormat;
        this.dateFormat = dateFormat;
        this.label = label;
    }

    GridTopLimitItem(String description, String unitOfMeasure, String code, boolean _default, String numberFormat, String dateFormat,
                     boolean label, boolean right) {
        this(description, unitOfMeasure, code, _default, numberFormat, dateFormat, label);
        this.right = right;
    }

    private String description;
    private String unitOfMeasure;
    private String code;
    private boolean _default;
    private String numberFormat;
    private String dateFormat;
    private boolean label;
    private boolean right;

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
