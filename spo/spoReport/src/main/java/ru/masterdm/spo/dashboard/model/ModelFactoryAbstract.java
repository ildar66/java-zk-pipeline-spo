package ru.masterdm.spo.dashboard.model;

import java.util.List;

import ru.md.domain.dashboard.MainReportRow;

import ru.masterdm.spo.dashboard.PipelineVM;

/**
 * @author pmasalov
 */
public abstract class ModelFactoryAbstract<T> implements ModelFactory<T> {

    private PipelineVM pipelineVM;

    public ModelFactoryAbstract(PipelineVM pipelineVM) {
        this.pipelineVM = pipelineVM;
    }

    /**
     * Returns .
     * @return
     */
    public PipelineVM getPipelineVM() {
        return pipelineVM;
    }

    /** Rows not more than 5. Iterate simplest fast method to find row */
    protected static MainReportRow findByIdStatus(List<MainReportRow> rows, int idStatus) {
        if (rows.isEmpty())
            return null;

        for (MainReportRow r : rows) {
            if (r.getIdStatus().intValue() == idStatus)
                return r;
        }
        return null;
    }
}
