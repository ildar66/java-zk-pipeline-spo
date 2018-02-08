package ru.masterdm.spo.dashboard.model;

import java.util.List;

import ru.masterdm.spo.dashboard.PipelineVM;
import ru.masterdm.spo.dashboard.domain.SummaryData;

/**
 * @author pmasalov
 */
public abstract class SummaryDataCreatorAbstract extends ModelFactoryAbstract<List<SummaryData>> implements SummaryDataCreator {

    List<SummaryData> model = null;

    public SummaryDataCreatorAbstract(PipelineVM pipelineVm) {
        super(pipelineVm);
    }

    @Override
    public List<SummaryData> getSummaryData() {
        if (model == null)
            model = createModel();
        return model;
    }

    @Override
    public void clear() {
        model = null;
    }

    protected boolean detectSelectionFor(SummaryData summaryData) {
        return true;
    }

    public boolean idEverySingleOneSelected() {
        for (SummaryData d : getSummaryData()) {
            if (!d.isSelected())
                return false;
        }
        return true;
    }

}
