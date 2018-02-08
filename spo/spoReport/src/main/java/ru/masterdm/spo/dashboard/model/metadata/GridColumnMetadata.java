package ru.masterdm.spo.dashboard.model.metadata;

/**
 * База для динамической информации по колонке
 *
 * @author pmasalov
 */
public interface GridColumnMetadata extends GridColumnItem {

    boolean isVisible();

    void setVisible(boolean visible);
}
