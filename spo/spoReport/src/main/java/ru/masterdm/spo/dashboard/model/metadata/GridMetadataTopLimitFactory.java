package ru.masterdm.spo.dashboard.model.metadata;

import java.util.ArrayList;
import java.util.List;

import ru.masterdm.spo.dashboard.PipelineSettings;
import ru.masterdm.spo.dashboard.PipelineVM;

/**
 * @author pmasalov
 */
public class GridMetadataTopLimitFactory extends GridMetadataFactoryAbstract {

    public GridMetadataTopLimitFactory(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    @Override
    public PipelineSettings.GridColumnSelection getConfigSettings() {
        return getPipelineVM().getSettings().getTopGridColumnSelection();
    }
}
