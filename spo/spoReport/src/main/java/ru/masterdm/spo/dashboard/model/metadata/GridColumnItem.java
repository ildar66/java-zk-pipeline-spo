package ru.masterdm.spo.dashboard.model.metadata;

/**
 * База для статичной информации по колонке
 * @author pmasalov
 */
public interface GridColumnItem {

    String getDescription();

    String getUnitOfMeasure();

    String getCode();

    boolean isDefault();

    String getNumberFormat();

    String getDateFormat();

    boolean isLabel();

    /**
     * Column item is right align.
     * @return
     */
    boolean isRight();
}
