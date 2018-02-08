package ru.masterdm.spo.dashboard.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;

import ru.md.domain.BranchStatistic;
import ru.md.domain.BranchStatisticRur;
import ru.md.domain.DepartmentExt;
import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.PipelineTradingDesk;
import ru.md.domain.dashboard.Sum;
import ru.md.domain.dashboard.TaskListFilter;
import ru.md.domain.dashboard.TaskListParam;
import ru.md.domain.dashboard.TaskTypeStatus;
import ru.md.persistence.DashboardMapper;

import ru.masterdm.spo.dashboard.PipelineConstants;
import ru.masterdm.spo.list.ETaskType;
import ru.masterdm.spo.dashboard.PipelineVM;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.utils.SBeanLocator;

import static ru.masterdm.spo.dashboard.PipelineConstants.Period;
import static ru.masterdm.spo.dashboard.PipelineConstants.Characteristics;

/**
 * @author pmasalov
 */
public class PieChartModelFactory extends ModelFactoryAbstract<Map<Integer, CategoryModel>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PieChartModelFactory.class);
    public PieChartModelFactory(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    private Map<Integer, CategoryModel> initPieChartsModel() {
        Map<Integer, CategoryModel> generalPieChartModel = getPipelineVM().getGeneralPieChartModelsMap();
        List<TaskTypeStatus> statuses = getPipelineVM().getTaskTypeStatuses();

        for (TaskTypeStatus s : statuses) {
            CategoryModel model = new DefaultCategoryModel();
            for (BranchStatisticRur statistic : getBranchStatisticRur(s.getIdStatus(),getPipelineVM().getDateFrom(),getPipelineVM().getDateTo()))
                model.setValue(statistic.getName(), PipelineConstants.Period.REPORT, statistic.getSumRur());
            for (BranchStatisticRur statistic : getBranchStatisticRur(s.getIdStatus(),getPipelineVM().getDateFromCompare(),getPipelineVM().getDateToCompare()))
                model.setValue(statistic.getName(), PipelineConstants.Period.COMPARE, statistic.getSumRur());
            generalPieChartModel.put(s.getIdStatus(), model);
        }

        return generalPieChartModel;
    }

    private List<BranchStatisticRur> getBranchStatisticRur(int idStatus, Date from, Date to){
        Long[] statusids = {Long.valueOf(idStatus)};
        DashboardMapper mapper = SBeanLocator.getDashboardMapper();
        IDashboardService service = SBeanLocator.getDashboardService();
        List<BranchStatistic> list = (from==null || to == null)?new ArrayList<BranchStatistic>():
                                     mapper.getBranchStatistic(statusids, from, to, new TaskListFilter(), getTaskListParam());
        HashMap<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        for (BranchStatistic bs : list){
            String name = bs.getName()==null?"Не определено":bs.getName();
            if (!map.keySet().contains(name))
                map.put(name, BigDecimal.ZERO);
            if (bs.getSum()!=null)
                map.put(name, map.get(name).add(BigDecimal.valueOf(service.toRur(new Sum(bs.getCurrency(), bs.getSum().doubleValue()), to))));
        }
        ArrayList<BranchStatisticRur> res = new ArrayList<BranchStatisticRur>();
        for (String branch : map.keySet())
            res.add(new BranchStatisticRur(map.get(branch).divide(BigDecimal.valueOf(1000000)),branch));
        Collections.sort(res, new Comparator<BranchStatisticRur>() {
            @Override
            public int compare(BranchStatisticRur o1, BranchStatisticRur o2) {
                return o2.getSumRur().compareTo(o1.getSumRur());
            }
        });
        if (res.size() < 7)
            return res;
        List<BranchStatisticRur> shortlist = new ArrayList<BranchStatisticRur>();
        for (int i=0; i<5; i++)
            shortlist.add(res.get(i));
        BigDecimal other = BigDecimal.ZERO;
        for (int i=5; i<res.size(); i++)
            other.add(res.get(i).getSumRur());
        BranchStatisticRur bs =  new BranchStatisticRur(other, "Прочие");
        shortlist.add(bs);
        return shortlist;
    }

    private TaskListParam getTaskListParam(){
        TaskListParam param = new TaskListParam();
        param.creditDocumentary = getPipelineVM().getCreditDocumentary()==null?0L:getPipelineVM().getCreditDocumentary().longValue();
        param.isTradingDeskOthers = getPipelineVM().isTradingDeskOthers()?"true":"false";

        ArrayList<Long> tradingDeskSelectedIds = new ArrayList<Long>();
        if(getPipelineVM().getTradingDesksSelected()!=null)
            for(PipelineTradingDesk td : getPipelineVM().getTradingDesksSelected())
                tradingDeskSelectedIds.add(Long.valueOf(td.getId()));
        if(tradingDeskSelectedIds.size()>0)
            param.tradingDeskSelectedIds = tradingDeskSelectedIds.toArray(new Long[tradingDeskSelectedIds.size()]);

        ArrayList<Long> departmentsIds = new ArrayList<Long>();
        if(getPipelineVM().getDepartmentsSelected()!=null)
            for(DepartmentExt d : getPipelineVM().getDepartmentsSelected())
                departmentsIds.add(d.getId());
        if(departmentsIds.size()>0)
            param.departmentsIds = departmentsIds.toArray(new Long[departmentsIds.size()]);

        return param;
    }

    @Override
    public Map<Integer, CategoryModel> createModel() {
        return initPieChartsModel();
    }

    public Set<String> getIndustrySet(){
        HashSet<String> set = new HashSet<String>();
        for (TaskTypeStatus s : getPipelineVM().getTaskTypeStatuses()) {
            for (BranchStatisticRur statistic : getBranchStatisticRur(s.getIdStatus(),getPipelineVM().getDateFrom(),getPipelineVM().getDateTo()))
                set.add(statistic.getName());
            for (BranchStatisticRur statistic : getBranchStatisticRur(s.getIdStatus(),getPipelineVM().getDateFromCompare(),getPipelineVM().getDateToCompare()))
                set.add(statistic.getName());
        }
        return set;
    }
}
