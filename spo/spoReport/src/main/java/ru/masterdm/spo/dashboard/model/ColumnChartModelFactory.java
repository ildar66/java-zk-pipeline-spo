package ru.masterdm.spo.dashboard.model;

import java.util.List;
import java.util.Map;

import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;

import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.TaskTypeStatus;

import ru.masterdm.spo.list.ETaskType;
import ru.masterdm.spo.dashboard.PipelineConstants;
import ru.masterdm.spo.dashboard.PipelineVM;

/**
 * @author pmasalov
 */
public class ColumnChartModelFactory extends ModelFactoryAbstract<Map<Integer, CategoryModel>> {

    public ColumnChartModelFactory(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    private Map<Integer, CategoryModel> initColumnChartsModel() {
        Map<Integer, CategoryModel> generalChartModel = getPipelineVM().getGeneralChartModelsMap();

        List<TaskTypeStatus> statuses = getPipelineVM().getTaskTypeStatuses();
        List<MainReportRow> rows = getPipelineVM().getGeneralRows();
        List<MainReportRow> compareRows = getPipelineVM().getGeneralRowsCompare();
        boolean isLimit = ETaskType.LIMIT.isEqual(getPipelineVM().getTaskType());

        for (TaskTypeStatus s : statuses) {
            MainReportRow row = findByIdStatus(rows, s.getIdStatus()); // may be null
            MainReportRow compare = findByIdStatus(compareRows, s.getIdStatus()); // may be null
            CategoryModel model = new DefaultCategoryModel();

            //fill model
            //series {"Сумма" "количество" "маржа"
            //category {"отчетный", "сравнительный"}     "сравнительный"    "отчетный"
            // ex:  "Сумма" "отчетный" row.getSumRub
            //      "Сумма" "сравнительный" compare.getSumRub
            // порядок инициализации серий должен совпадать с порядком инициализации серий в чарте (sum, count{, маржа})
            model.setValue(PipelineConstants.Characteristics.SUM_RUB, PipelineConstants.Period.COMPARE, (compare != null) ? compare.getSumRub() : 0);
            model.setValue(PipelineConstants.Characteristics.COUNT_ALL, PipelineConstants.Period.COMPARE,
                           (compare != null) ? compare.getCountAll() : 0);
            if (!isLimit)
                model.setValue(PipelineConstants.Characteristics.WAV_MARGIN, PipelineConstants.Period.COMPARE,
                               (compare != null) ? compare.getWavMargin() : 0);

            model.setValue(PipelineConstants.Characteristics.SUM_RUB, PipelineConstants.Period.REPORT, (row != null) ? row.getSumRub() : 0);
            model.setValue(PipelineConstants.Characteristics.COUNT_ALL, PipelineConstants.Period.REPORT, (row != null) ? row.getCountAll() : 0);
            if (!isLimit)
                model.setValue(PipelineConstants.Characteristics.WAV_MARGIN, PipelineConstants.Period.REPORT, (row != null) ? row.getWavMargin() : 0);

            generalChartModel.put(s.getIdStatus(), model);
        }

        return generalChartModel;
    }

    @Override
    public Map<Integer, CategoryModel> createModel() {
        return initColumnChartsModel();
    }
}
