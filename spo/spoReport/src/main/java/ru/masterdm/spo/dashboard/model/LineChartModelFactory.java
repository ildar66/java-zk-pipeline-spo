package ru.masterdm.spo.dashboard.model;

import static ru.masterdm.spo.dashboard.PipelineConstants.Characteristics;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;

import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.TaskListParam;
import ru.md.domain.dashboard.TaskTypeStatus;

import ru.masterdm.spo.dashboard.PipelineVM;
import ru.masterdm.spo.dashboard.domain.DatePeriod;
import ru.masterdm.spo.dashboard.helper.DatePeriodSplitter;
import ru.masterdm.spo.list.ETaskType;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.utils.SBeanLocator;

/**
 * @author Andrey Pavlenko
 */
public class LineChartModelFactory extends ModelFactoryAbstract<Map<String, CategoryModel>> {
    private static transient final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
    private static final Logger LOGGER = LoggerFactory.getLogger(LineChartModelFactory.class);

    public LineChartModelFactory(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    private Map<String, CategoryModel> initModel() {
        IDashboardService service = SBeanLocator.getDashboardService();
        Map<String, CategoryModel> generalModel= getPipelineVM().getGeneralLineChartModelsMap();
        List<TaskTypeStatus> statuses = getPipelineVM().getTaskTypeStatuses();
        boolean isLimit = ETaskType.LIMIT.isEqual(getPipelineVM().getTaskType());

        CategoryModel modelSumRub = new DefaultCategoryModel();
        CategoryModel modelCountAll = new DefaultCategoryModel();
        CategoryModel modelWavMargin = new DefaultCategoryModel();

        TaskListParam param = new TaskListParam();
        param.creditDocumentary = 0L;

        for (DatePeriod period : getDates()) {
            List<MainReportRow> rows = service.getMainReport(period.from, period.to, getPipelineVM().getTaskType(), getPipelineVM().getCreditDocumentary(),
                                         getPipelineVM().getSettings().getTradingDeskSelected(), getPipelineVM().getSettings().isTradingDeskOthers(),
                                         getPipelineVM().getSettings().getDepartmentsSelected());
            for (TaskTypeStatus s : statuses) {
                MainReportRow row = filterRowByStatus(s.getIdStatus(), rows);
                //String periodTitle = df.format(period.from) + " - " + df.format(period.to);
                String periodTitle = df.format(period.to);
                //LOGGER.info(s.getStatus() + " - " + periodTitle);
                modelSumRub.setValue(s.getStatus(), periodTitle, row == null ? BigDecimal.ZERO : row.getSumRub());
                modelCountAll.setValue(s.getStatus(), periodTitle, row == null ? 0 :row.getCountAll());
                if (!isLimit)
                    modelWavMargin.setValue(s.getStatus(), periodTitle,
                                            (row == null || row.getWavMargin() == null) ? BigDecimal.ZERO : row.getWavMargin());
            }
        }

        generalModel.put(Characteristics.SUM_RUB, modelSumRub);
        generalModel.put(Characteristics.COUNT_ALL, modelCountAll);
        if (!isLimit) {
            generalModel.put(Characteristics.WAV_MARGIN, modelWavMargin);
        }

        return generalModel;
    }
    private MainReportRow filterRowByStatus(int idStatus, List<MainReportRow> rows){
        for(MainReportRow row : rows)
            if(row.getIdStatus().intValue()==idStatus)
                return row;
        return null;
    }

    private DatePeriod[] getDates() {
        Date start = DatePeriodSplitter.min(getPipelineVM().getDateFrom(), getPipelineVM().getDateFromCompare());
        Date to = DatePeriodSplitter.max(getPipelineVM().getDateTo(), getPipelineVM().getDateToCompare());
        DatePeriodSplitter splitter = new DatePeriodSplitter(start, to);
        return splitter.getPeriod();
    }

    @Override
    public Map<String, CategoryModel> createModel() {
        return initModel();
    }
}
