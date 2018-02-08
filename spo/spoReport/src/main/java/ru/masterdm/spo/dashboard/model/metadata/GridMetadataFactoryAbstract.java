package ru.masterdm.spo.dashboard.model.metadata;

import java.util.Collection;
import java.util.List;

import ru.masterdm.spo.dashboard.PipelineSettings;
import ru.masterdm.spo.dashboard.PipelineVM;
import ru.masterdm.spo.dashboard.model.ModelFactoryAbstract;

/**
 * @author pmasalov
 */
public abstract class GridMetadataFactoryAbstract extends ModelFactoryAbstract<Collection<GridColumnMetadata>> implements GridMetadataFactory {

    private Collection<GridColumnMetadata> metadata = null;

    public GridMetadataFactoryAbstract(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    @Override
    public Collection<GridColumnMetadata> getColumnMetadata() {
        if (metadata == null) {
            metadata = createModel();
        }
        return metadata;
    }

    @Override
    public void resetToDefault() {
        metadata = null;
        getConfigSettings().resetSelection();
    }


    public Collection<GridColumnMetadata> createModel() {
        PipelineSettings.GridColumnSelection columnSelection = getConfigSettings();
        return columnSelection.getSelected();
    }

    public abstract PipelineSettings.GridColumnSelection getConfigSettings();

}
