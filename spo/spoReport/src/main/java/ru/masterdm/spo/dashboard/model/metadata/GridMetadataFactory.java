package ru.masterdm.spo.dashboard.model.metadata;

import java.util.Collection;
import java.util.List;

/**
 * @author pmasalov
 */
public interface GridMetadataFactory {

    Collection<GridColumnMetadata> getColumnMetadata();

    void resetToDefault();
}
