package ru.masterdm.spo.dashboard;

import static ru.masterdm.spo.dashboard.PipelineConstants.*;
import static ru.masterdm.spo.dashboard.model.EModel.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.Series;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.ext.Selectable;

import ru.md.domain.DepartmentExt;
import ru.md.domain.IndRate;
import ru.md.domain.User;
import ru.md.domain.dashboard.DetailReportRow;
import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.PipelineTradingDesk;
import ru.md.domain.dashboard.Sum;
import ru.md.domain.dashboard.TaskTypeStatus;
import ru.md.domain.dashboard.TopReportRow;
import ru.md.report.DashboardReportData;

import ru.masterdm.spo.dashboard.domain.SummaryData;
import ru.masterdm.spo.dashboard.model.EModel;
import ru.masterdm.spo.dashboard.model.ModelFactory;
import ru.masterdm.spo.dashboard.model.PieChartModelFactory;
import ru.masterdm.spo.dashboard.model.SummaryDataCreator;
import ru.masterdm.spo.dashboard.model.metadata.GridMetadataFactory;
import ru.masterdm.spo.dashboard.tree.SelectChanger;
import ru.masterdm.spo.dashboard.tree.TreeUtils;
import ru.masterdm.spo.list.EDashStatus;
import ru.masterdm.spo.list.ETaskType;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.service.IReporterService;
import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

/**
 * @author pmasalov
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PipelineVM implements DatePeriod {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineVM.class);
    /** Аттрибут в котором в сессии сохраняется обьект {@link PipelineSettings} */
    public static final String C_SESSION_SETTINGS = "pipeline-dashboard-settings";
    private static final String REPORT_PERIOD = Period.REPORT;
    private static final String DASHBOARD_EXCEL_REPORT = "dashboard_report_xlsx";
    private static final String DASHBOARD_EXCEL_PDF_REPORT = "dashboard_report_4pdf_xlsx";
    private static final String ROLE_CLIENT_MANAGER = "Клиентский менеджер";
    private static final String ROLE_PRODUCT_MANAGER = "Продуктовый менеджер";
    private static final String ROLE_STRUCTURATOR = "Структуратор";
    private static final String ROLE_CREDIT_ANALYST = "Кредитный аналитик";

    private IDashboardService dashboardService;
    private IReporterService reporterService;

    private Map<EModel, ModelFactory> myModelFactories = new HashMap<EModel, ModelFactory>();

    private String taskType;
    private List<TaskTypeStatus> taskTypeStatuses;

    private Integer creditDocumentary;

    private List<PipelineTradingDesk> tradingDesks;

    /** Данные сводного отчёта основного периуда */
    private List<MainReportRow> generalRows;
    /** Данные сводного отчёта периуда сравнения */
    private List<MainReportRow> generalRowsCompare;
    /** Модели для графиков по состояниям для текущего taskType. */
    private Map<Integer, CategoryModel> generalChartModel = new HashMap<Integer, CategoryModel>(); // indexed by idStatus
    /** Модели для графиков по показателям для текущего taskType */
    private Map<Integer, CategoryModel> generalPieChartModel = new HashMap<Integer, CategoryModel>(); // indexed by Characteristics
    private Map<String, CategoryModel> generalLineChartModel = new HashMap<String, CategoryModel>(); // indexed by Characteristics
    /** Модель для грида с состояниями */
    private ListModel<MainReportRow> generalGridModel;
    // листенер создаётся на долго так как приаттачивается к филтруемым моделям
    private TreeDataListener departmentSelectionListener;

    /** Параметры работы дашборда в разрезе сессии пользователя */
    private PipelineSettings settings;

    private static DateFormat warningDf = new SimpleDateFormat("dd.MM.yyyy");
    private static DateFormat reportDf = new SimpleDateFormat("dd.MM.yyyy");

    private String filterWarning;

    private String departmentSearch;

    /** Создатели данных сводных показателей для limit и для прочих taskType */
    SummaryDataCreator summaryDataCreatorLimit, summaryDataCreatorOther;

    /**
     * автоматическое селектирование/деселектирование дочерних узлов
     */
    private class DepartmentTreeListener implements TreeDataListener {

        // блокировка рекурсивного вызова обработчика при выборе дочерних нодов автоматически
        boolean blockSelectionChanged = false;

        @Override
        public void onChange(TreeDataEvent event) {
            if (event.getType() == TreeDataEvent.SELECTION_CHANGED) {
                if (!blockSelectionChanged) {
                    try {
                        blockSelectionChanged = true;
                        DefaultTreeModel<DepartmentExt> tm = (DefaultTreeModel<DepartmentExt>) event.getModel();
                        int[] path = event.getPath();
                        TreeNode tn = tm.getChild(path);
                        SelectChanger ch = new SelectChanger(tm, tn);

                        if (tm.isPathSelected(path)) {
                            ch.select();
                        } else {
                            ch.unselect();
                        }
                        settings.setDepartmentsSelectedNodes(((Selectable) event.getModel()).getSelection());

                        BindUtils.postNotifyChange(null, null, PipelineVM.this, "departmentsSelected");
                        BindUtils.postNotifyChange(null, null, PipelineVM.this, "departmentsSelectedCut");
                    } finally {
                        blockSelectionChanged = false;
                        savePipelineSettings();
                    }
                }
            }

        }

    }

    ;

    public PipelineVM() {
        super();
        departmentSelectionListener = new DepartmentTreeListener();
    }

    /**
     * Returns .
     * @return
     */
    public PipelineSettings getSettings() {
        return settings;
    }

    public Map<Integer, CategoryModel> getGeneralChartModelsMap() {
        return generalChartModel;
    }

    public Map<Integer, CategoryModel> getGeneralPieChartModelsMap() {
        return generalPieChartModel;
    }

    public Map<String, CategoryModel> getGeneralLineChartModelsMap() {
        return generalLineChartModel;
    }

    /** Обнуляет содержимое буферов модели */
    private void clearData() {
        generalRows = null;
        generalRowsCompare = null;
        generalChartModel.clear();
        generalPieChartModel.clear();
        generalLineChartModel.clear();
        generalGridModel = null;

        ((SummaryDataCreator) getModelFactory(SUMMARY_DATA_CREATOR_LIMIT)).clear();
        ((SummaryDataCreator) getModelFactory(SUMMARY_DATA_CREATOR_OTHER)).clear();
    }

    @Init
    public void init(@ContextParam(ContextType.EXECUTION) Execution execution, @ExecutionArgParam("taskType") String taskType,
                     @ContextParam(ContextType.SESSION) Session session, @ExecutionArgParam("creditDocumentary") Integer creditDocumentary) {
        this.taskType = taskType;
        this.creditDocumentary = creditDocumentary;
        dashboardService = SBeanLocator.getDashboardService();
        reporterService = SBeanLocator.singleton().getReporterService();

        // long live settings of dashboard
        settings = (PipelineSettings) session.getAttribute(C_SESSION_SETTINGS);
        if (settings == null) {
            settings = initPipelineSettings(execution);
            LOGGER.info("settings1=" + settings.toString());
            session.setAttribute(C_SESSION_SETTINGS, settings);
        } else {
            settings.setMainMenuSelection(taskType, creditDocumentary);
            LOGGER.info("settings2=" + settings.toString());
        }

        clearData();
        savePipelineSettings();
    }

    //В сессии настроек не оказалось. Нужно взять из базы или использовать дефолтные
    private PipelineSettings initPipelineSettings(Execution execution) {
        // userid by login
        HttpServletRequest request = (HttpServletRequest) execution.getNativeRequest();
        User user = AbstractAction.getUser(request);

        String settingJson = SBeanLocator.getDashboardMapper().getPipelineSettings(
                PipelineSettings.SETTING_USER == null ? user.getId() : PipelineSettings.SETTING_USER);
        LOGGER.info("settingJson=" + settingJson);
        if (!Formatter.str(settingJson).isEmpty()) {
            PipelineSettings loadsettings = new PipelineSettings(user, settingJson, null);
            return loadsettings;
        }

        //нет истории. Берем настройки по умолчанию
        PipelineSettings newsettings = new PipelineSettings(user);

        // по умолчанию форма в режиме месяца
        //monthMode(null);
        return newsettings;
    }

    /**
     * Returns service.
     * @return
     */
    public IDashboardService getDashboardService() {
        return dashboardService;
    }

    /** Фабричный метод для подготовки создателей сводных данных */
    public SummaryDataCreator getSummaryDataCreator() {
        return (SummaryDataCreator) getModelFactory(ETaskType.LIMIT.isEqual(taskType) ? SUMMARY_DATA_CREATOR_LIMIT : SUMMARY_DATA_CREATOR_OTHER);
    }

    public GridMetadataFactory getGridStateMetadata() {
        return (GridMetadataFactory) getModelFactory(
                ETaskType.LIMIT.isEqual(taskType) ? GRID_METADATA_STATE_LIMIT_FACTORY : GRID_METADATA_STATE_OTHER_FACTORY);
    }

    public GridMetadataFactory getGridTopMetadata() {
        return (GridMetadataFactory) getModelFactory(
                ETaskType.LIMIT.isEqual(taskType) ? GRID_METADATA_TOP_LIMIT_FACTORY : GRID_METADATA_TOP_OTHER_FACTORY);
    }

    private ModelFactory getModelFactory(EModel modelType) {
        if (myModelFactories.containsKey(modelType))
            return myModelFactories.get(modelType);

        ModelFactory f = modelType.<PipelineVM>createModelFactory(this);
        myModelFactories.put(modelType, f);
        return f;
    }

    /**
     * Returns .
     * @return
     */
    public DefaultTreeModel<DepartmentExt> getFullDepartmentsModel() {
        DefaultTreeModel<DepartmentExt> fullDepartmentsModel;
        fullDepartmentsModel = (DefaultTreeModel<DepartmentExt>) getModelFactory(FULL_DEPARTMENT_MODEL).createModel();
        fullDepartmentsModel.addTreeDataListener(departmentSelectionListener);
        return fullDepartmentsModel;
    }

    private DefaultTreeModel<DepartmentExt> getCurrentDepartmentModel() {
        if (departmentSearch == null || departmentSearch.isEmpty())
            return getFullDepartmentsModel();

        DefaultTreeModel<DepartmentExt> filteredDepartmentModel;
        filteredDepartmentModel = (DefaultTreeModel<DepartmentExt>) getModelFactory(FILTERED_DEPARTMENT_MODEL).createModel();
        if (filteredDepartmentModel != null)
            filteredDepartmentModel.addTreeDataListener(departmentSelectionListener);
        return filteredDepartmentModel;
    }

    /**
     * Returns .
     * @return
     */
    public List<MainReportRow> getGeneralRows() {
        if (generalRows == null)
            generalRows = dashboardService
                    .getMainReport(settings.getDateFrom(), settings.getDateTo(), taskType, creditDocumentary, settings.getTradingDeskSelected(),
                                   settings.isTradingDeskOthers(),
                                   settings.getDepartmentsSelected());
        return generalRows;
    }

    /**
     * Returns .
     * @return
     */
    public List<MainReportRow> getGeneralRowsCompare() {
        if (generalRowsCompare == null)
            generalRowsCompare = dashboardService
                    .getMainReport(settings.getDateFromCompare(), settings.getDateToCompare(), taskType, creditDocumentary,
                                   settings.getTradingDeskSelected(),
                                   settings.isTradingDeskOthers(),
                                   settings.getDepartmentsSelected());
        return generalRowsCompare;
    }

    public List<PipelineTradingDesk> getTradingDeskRows() {
        if (tradingDesks == null) {
            // Trading Desk
            tradingDesks = dashboardService.getPipelineTradingDesk();
            tradingDesks.add(PipelineTradingDesk.OTHERS);
        }
        return tradingDesks;
    }

    /**
     * MODEL
     * Returns .
     * @return
     */
    public ListModel<PipelineTradingDesk> getTradingDeskModel() {
        /** Модель для списка выбора трайдинг десков */
        ListModel<PipelineTradingDesk> tradingDeskModel;
        // Trading Desk
        List<PipelineTradingDesk> tradingDesks = getTradingDeskRows();

        tradingDeskModel = new ListModelSet<PipelineTradingDesk>(tradingDesks);
        ((Selectable) tradingDeskModel).setMultiple(true);
        ((Selectable) tradingDeskModel).setSelection(settings.getTradingDeskSelected());

        tradingDeskModel.addListDataListener(new ListDataListener() {

            @Override
            public void onChange(ListDataEvent event) {
                if (event.getType() == ListDataEvent.SELECTION_CHANGED) {
                    settings.setTradingDeskSelected(((Selectable) event.getModel()).getSelection());
                    BindUtils.postNotifyChange(null, null, PipelineVM.this, "tradingDesksSelected");
                    BindUtils.postNotifyChange(null, null, PipelineVM.this, "tradingDesksSelectedCut");
                }
            }
        });
        savePipelineSettings();
        return tradingDeskModel;
    }

    public List<String> getTradingDesksSelectedCut() {
        List<String> ret = new ArrayList<String>();

        List<PipelineTradingDesk> tradingDesks = getTradingDeskRows();
        Set<PipelineTradingDesk> tsSelected = settings.getTradingDeskSelected();
        if (tsSelected.size() > 0 && tradingDesks.size() > 0 && tradingDesks.size() == tsSelected.size()) {
            ret.add("Все");
            return ret;
        }

        int i = 1;
        for (PipelineTradingDesk td : tsSelected) {
            if (i < 10) {
                ret.add(td.getName());
            } else {
                ret.add("...");
                break;
            }

            i++;
        }
        return ret;
    }

    public Collection<PipelineTradingDesk> getTradingDesksSelected() {
        return settings.getTradingDeskSelected();
    }

    public boolean isTradingDeskOthers() {
        return settings.isTradingDeskOthers();
    }

    public void setTradingDeskOthers(boolean b) {
        settings.setTradingDeskOthers(b);
        savePipelineSettings();
    }

    /*
        public int getTradingDeskOthersIndex() {
            return settings.isTradingDeskOthers() ? 0 : -1;
        }

        public void setTradingDeskOthersIndex(int i) {
            settings.setTradingDeskOthers(i == 0);
        }
    */
    public TreeModel<TreeNode<DepartmentExt>> getDepartmentsTreeModel() {
        return getCurrentDepartmentModel();
    }

    public Collection<DepartmentExt> getDepartmentsSelected() {
        return settings.getDepartmentsSelected();
    }

    public List<String> getDepartmentsSelectedCut() {
        List<String> ret = new ArrayList<String>();
        int i = 1;
        for (DepartmentExt d : getDepartmentsSelected()) {
            if (i < 20) {
                ret.add(d.getName());
            } else {
                ret.add("...");
                break;
            }
            ++i;
        }

        return ret;
    }

    @Command
    @NotifyChange({"departmentsTreeModel", "departmentSearch", "departmentsSelectedCut", "departmentsSelected"})
    public void clearDepartmentSearch() {
        this.departmentSearch = null;
    }

    @Command
    @NotifyChange({"departmentsTreeModel", "departmentsSelectedCut", "departmentsSelected"})
    public void changeDepartmentSearch() {
        // simple recreate filtered model
    }

    /**
     * Returns .
     * @return
     */
    public String getDepartmentSearch() {
        return departmentSearch;
    }

    /**
     * Sets .
     * @param departmentSearch
     */
    public void setDepartmentSearch(String departmentSearch) {
        this.departmentSearch = departmentSearch;
    }

    public String getFilterWarning() {
        validateDates();
        return filterWarning;
    }

    /** MODEL */
    public Map<Integer, CategoryModel> getGeneralChartModel() {
        if (generalChartModel.isEmpty()) {
            //initColumnChartsModel();
            generalChartModel = (Map<Integer, CategoryModel>) getModelFactory(COLUMN_CHART_MODEL).createModel();
        }
        return generalChartModel;
    }

    public Map<String, String> getPieChartColors() {
        HashMap<String, String> map = new HashMap<String, String>();
        PieChartModelFactory factory = (PieChartModelFactory) getModelFactory(PIE_CHART_MODEL);
        int i = 0;
        for (String industry : factory.getIndustrySet())
            if (industry.equalsIgnoreCase("Прочие"))
                map.put(industry, PipelineConstants.OTHER_COLOR);
            else if (industry.equalsIgnoreCase("Не определено"))
                map.put(industry, PipelineConstants.NOT_DEFINE_COLOR);
            else if (i < PipelineConstants.PIE_COLORS.length)
                map.put(industry, PipelineConstants.PIE_COLORS[i++]);
        return map;
    }

    public Set<String> getPieIndustries() {
        PieChartModelFactory factory = (PieChartModelFactory) getModelFactory(PIE_CHART_MODEL);
        Set<String> ss = factory.getIndustrySet();
        Set<String> outSs = new HashSet<String>();

        for (String s : ss) {
            if (s.equals("Прочие") || s.equals("Не определено"))
                continue;
            outSs.add(s);
            if (outSs.size() == 5)
                break;
        }
        //System.out.println("outSs = " + outSs);
        return outSs;
    }

    public int getPieLegendHeight() {
        int r = 40 * getPieIndustries().size() / 2;
        //System.out.println("r=" + r);
        return r;
    }

    /** MODEL */
    public Map<Integer, CategoryModel> getGeneralPieChartModel() {
        if (generalPieChartModel.isEmpty()) {
            //initPieChartsModel();
            generalPieChartModel = (Map<Integer, CategoryModel>) getModelFactory(PIE_CHART_MODEL).createModel();
        }
        return generalPieChartModel;
    }

    /** MODEL */
    public Map<String, CategoryModel> getGeneralLineChartModel() {
        if (generalLineChartModel.isEmpty()) {
            generalLineChartModel = (Map<String, CategoryModel>) getModelFactory(LINE_CHART_MODEL).createModel();
        }
        return generalLineChartModel;
    }

    public Map<String, String> getGeneralLineChartTitle() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Characteristics.SUM_RUB, Characteristics.SUM_RUB + ", млн. " + "\u0584");
        map.put(Characteristics.COUNT_ALL, Characteristics.COUNT_ALL + ", шт.");
        map.put(Characteristics.WAV_MARGIN, Characteristics.WAV_MARGIN + ", %");
        return map;
    }

    /** MODEL */
    public ListModel<MainReportRow> getGeneralGridModel() {
        if (generalGridModel == null) {
            List<MainReportRow> list = getGeneralRows();
            generalGridModel = new ListModelList<MainReportRow>(list);
        }
        return generalGridModel;
    }

    /** MODEL */
    public ListModel<TopReportRow> getTop3Model() {
        List<TopReportRow> top = dashboardService.getTopReport(settings.getDateFrom(), settings.getDateTo(), taskType, creditDocumentary,
                                                               settings.getTradingDeskSelected(),
                                                               settings.isTradingDeskOthers(),
                                                               settings.getDepartmentsSelected());
        return (new ListModelList<TopReportRow>(top));
    }

    public List<TaskTypeStatus> getTaskTypeStatuses() {
        if (taskTypeStatuses == null)
            taskTypeStatuses = dashboardService.getTaskTypeStatusesInOrder(taskType);
        return taskTypeStatuses;
    }

    public List<String> getLcharacteristics() {
        if (ETaskType.LIMIT.isEqual(taskType))
            return Arrays.asList(Characteristics.SUM_RUB, Characteristics.COUNT_ALL);
        else
            return Arrays.asList(Characteristics.SUM_RUB, Characteristics.COUNT_ALL, Characteristics.WAV_MARGIN);
    }

    public List<String> getCharacteristics() {
        if (ETaskType.LIMIT.isEqual(taskType))
            return Arrays.asList(Characteristics.SUM_RUB, Characteristics.COUNT_ALL);
        else
            return Arrays.asList(Characteristics.SUM_RUB, Characteristics.COUNT_ALL, Characteristics.WAV_MARGIN);
    }

    /** nailed */
    public String getTaskTypeName() {
        // nailed
        return getTaskTypeName(taskType);
    }

    private String getTaskTypeName(String tt) {
        ETaskType ett = ETaskType.findByCode(tt);
        if (ett != null)
            return ett.getName();

        return null;
    }

    public String getTaskTypeTitleName() {
        return getTaskTypeTitleName(taskType);
    }

    public String getTaskTypeTitleName(String tt) {
        ETaskType ett = ETaskType.findByCode(tt);
        if (ett != null) {
            return ett.getTitleName() != null ? ett.getTitleName() : ett.getName();
        }
        return null;
    }

    /**
     * Returns .
     * @return
     */
    public String getTaskType() {
        return taskType;
    }

    /** nailed */
    public String getCreditDocumentaryName() {
        if (creditDocumentary == null)
            return null;
        return creditDocumentary == 1 ? PipelineConstants.CreditDocumentary.CREDIT : PipelineConstants.CreditDocumentary.DOCUMENTARY;
    }

    /** nailed */
    public String getCreditDocumentaryGenitiveName() {
        if (creditDocumentary == null)
            return null;
        return creditDocumentary == 1 ? PipelineConstants.CreditDocumentGenitive.CREDIT : PipelineConstants.CreditDocumentGenitive.DOCUMENTARY;
    }

    public String getGeneralReportTitleAppend() {
        String ret = "";
        if (ETaskType.CROSS_SELL != ETaskType.findByCode(taskType)) {
            ret = (creditDocumentary == null ? "всe" : getCreditDocumentaryName().toLowerCase()) + ' ';
        }

        return ret + getTaskTypeTitleName();
    }

    /**
     * Returns .
     * @return
     */
    public Integer getCreditDocumentary() {
        return creditDocumentary;
    }

    /**
     * Returns .
     * @return
     */
    public Long getUserId() {
        return settings.getUserId();
    }

    /**
     * Returns .
     * @return
     */
    @Override
    public Date getDateFrom() {
        return settings.getDateFrom();
    }

    /**
     * Sets .
     * @param dateFrom
     */
    @Override
    @NotifyChange({"filterWarning", "dateMode", "dayMode", "monthMode", "yearMode", "dateFromMoreDateTo",
                   "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError", "disableSearchButton"})
    public void setDateFrom(Date dateFrom) {
        settings.setDateFrom(dateFrom);
        clearDateMode();
        savePipelineSettings();
    }

    /**
     * Returns .
     * @return
     */
    @Override
    public Date getDateTo() {
        return settings.getDateTo();
    }

    /**
     * Sets .
     * @param dateTo
     */
    @Override
    @NotifyChange({"filterWarning", "dateToMoreToday", "dateMode", "dayMode", "monthMode", "yearMode", "dateFromMoreDateTo",
                   "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError", "disableSearchButton"})
    public void setDateTo(Date dateTo) {
        settings.setDateTo(dateTo);
        clearDateMode();
        savePipelineSettings();
    }

    private void addFilterWarning(String s) {
        filterWarning = Formatter.str(filterWarning).isEmpty() ? s : filterWarning + " " + s;
    }

    /** Валидация установок диапазона дат по отношению к текущей дате */
    private void validateDates() {
        filterWarning = null;

        if (isDateFromMoreDateTo())
            addFilterWarning("Дата начала отчётного периода превышает дату окончания.");

        if (isDateFromMoreDateToCompare())
            addFilterWarning("Дата начала сравнительного периода превышает дату окончания.");

        Date today = DateUtils.truncate(new Date(), Calendar.DATE);

        if (getDateTo() != null && getDateTo().compareTo(today) == 0)
            addFilterWarning("Данные на " + warningDf.format(today) + " могут менятся. День не закрыт.");
        else if (getDateFrom() != null && getDateTo() != null && getDateFrom().compareTo(today) <= 0 && getDateTo().compareTo(today) > 0) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            addFilterWarning("C " + warningDf.format(c.getTime()) + " по " + warningDf
                    .format(getDateTo())
                                     + " данных нет. Данные на " + warningDf
                    .format(today) + " могут менятся. День не закрыт.");
        } else if (getDateFrom() != null && today.compareTo(getDateFrom()) < 0) {
            addFilterWarning("C " + warningDf.format(getDateFrom()) + " по " + warningDf
                    .format(getDateTo()) + " данных нет.");
        }

        if (settings.getDateFromCompare() == null)
            addFilterWarning("Заполните начало периода сравнения.");
        if (settings.getDateToCompare() == null)
            addFilterWarning("Заполните окончание периода сравнения.");
        if (settings.getDateFrom() == null)
            addFilterWarning("Заполните начало отчетного периода.");
        if (settings.getDateTo() == null)
            addFilterWarning("Заполните окончание отчетного периода.");

    }

    public boolean isDisableSearchButton() {
        return settings.getDateTo() == null || isDateFromError() || isDateToCompareError() || isDateFromCompareError();
    }

    public boolean isDateToError() {
        return isDateToMoreToday() || isDateFromMoreDateTo() || settings.getDateTo() == null;
    }

    public boolean isDateFromError() {
        return isDateFromMoreDateTo() || settings.getDateFrom() == null;
    }

    public boolean isDateToCompareError() {
        return isDateFromMoreDateToCompare() || settings.getDateToCompare() == null;
    }

    public boolean isDateFromCompareError() {
        return isDateFromMoreDateToCompare() || settings.getDateFromCompare() == null;
    }

    /** проверка что окончание периуда превышает сегодня */
    public boolean isDateToMoreToday() {
        if (settings.getDateTo() == null)
            return false;
        return settings.getDateTo().compareTo(DateUtils.truncate(new Date(), Calendar.DATE)) >= 0;
    }

    /** Проверка что дата начала отчётного периуда превышает дату окончания */
    public boolean isDateFromMoreDateTo() {
        if (settings.getDateFrom() == null || settings.getDateTo() == null)
            return false;
        return (settings.getDateFrom().compareTo(settings.getDateTo()) > 0);
    }

    /** Проверка что дата начала сравнительного периуда превышает дату окончания */
    public boolean isDateFromMoreDateToCompare() {
        if (settings.getDateFromCompare() == null || settings.getDateToCompare() == null)
            return false;
        return (settings.getDateFromCompare().compareTo(settings.getDateToCompare()) > 0);
    }

    /**
     * Returns .
     * @return
     */
    public Date getDateFromCompare() {
        return settings.getDateFromCompare();
    }

    /**
     * Sets .
     * @param dateFromCompare
     */
    @NotifyChange({"filterWarning", "dateFromMoreDateToCompare", "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError",
                   "disableSearchButton"})
    public void setDateFromCompare(Date dateFromCompare) {
        settings.setDateFromCompare(dateFromCompare);
        savePipelineSettings();
    }

    /**
     * Returns .
     * @return
     */
    public Date getDateToCompare() {
        return settings.getDateToCompare();
    }

    /**
     * Sets .
     * @param dateToCompare
     */
    @NotifyChange({"filterWarning", "dateFromMoreDateToCompare",
                   "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError", "disableSearchButton"})
    public void setDateToCompare(Date dateToCompare) {
        settings.setDateToCompare(dateToCompare);
        savePipelineSettings();
    }

    public boolean isDayMode() {
        DateMode dateMode = settings.getDateMode();
        return dateMode != null && dateMode == DateMode.DAY;
    }

    public boolean isMonthMode() {
        DateMode dateMode = settings.getDateMode();
        return dateMode != null && dateMode == DateMode.MONTH;
    }

    public boolean isYearMode() {
        DateMode dateMode = settings.getDateMode();
        return dateMode != null && dateMode == DateMode.YEAR;
    }

    public DateMode getDateMode() {
        return settings.getDateMode();
    }

    @Command
    @NotifyChange({//"generalChartModel", "generalPieChartModel", "generalLineChartModel",
                   "generalGridModel", "top3Model", "summaryDataCreator",
                   "columnChartMode", "pieChartMode", "lineChartMode", "taskTypeStatuses",
                   "characteristics", "pieIndustries", "pieChartColors"})
    public void search() {
        if (isDisableSearchButton())
            return;
        // очистка данных. данные загружаются по требованию
        clearData();
        notifyChartModelChange();
        savePipelineSettings();
    }

    private void notifyChartModelChange() {
        if (isColumnChartMode()) {
            BindUtils.postNotifyChange(null, null, PipelineVM.this, "generalChartModel");

        } else if (isLineChartMode()) {
            BindUtils.postNotifyChange(null, null, PipelineVM.this, "characteristics");
            BindUtils.postNotifyChange(null, null, PipelineVM.this, "generalLineChartModel");

        } else if (isPieChartMode()) {
            BindUtils.postNotifyChange(null, null, PipelineVM.this, "generalPieChartModel");
        }
    }

    @Command
    @NotifyChange({"dateFrom", "dateTo", "filterWarning", "dateToMoreToday", "dateFromMoreDateTo"})
    public void toPrevious(@ContextParam(ContextType.BINDER) Binder binder) {
        if (settings.getDateMode() == null)
            return; // nothing to do

        settings.getDateMode().getDateManipulator().decrement(settings);
    }

    @Command
    @NotifyChange({"dateFrom", "dateTo", "filterWarning", "dateToMoreToday", "dateFromMoreDateTo"})
    public void toNext(@ContextParam(ContextType.BINDER) Binder binder) {
        if (settings.getDateMode() == null)
            return; // nothing to do

        settings.getDateMode().getDateManipulator().increment(settings);
    }

    @Command
    @NotifyChange({"dateFromCompare", "dateToCompare", "filterWarning", "dateFromMoreDateToCompare"})
    public void toPreviousCompare() {
        if (settings.getDateMode() == null)
            return; // nothing to do

        settings.getDateMode().getDateManipulator().decrement(settings.getCompareDatePeriod());
    }

    @Command
    @NotifyChange({"dateFromCompare", "dateToCompare", "filterWarning", "dateFromMoreDateToCompare"})
    public void toNextCompare() {
        if (settings.getDateMode() == null)
            return; // nothing to do

        settings.getDateMode().getDateManipulator().increment(settings.getCompareDatePeriod());
    }

    @Command
    @NotifyChange({"dateFrom", "dateTo", "dateMode", "dayMode", "monthMode", "yearMode", "filterWarning", "dateToMoreToday",
                   "dateFromMoreDateTo", "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError", "disableSearchButton"})
    public void dayMode(@ContextParam(ContextType.BINDER) Binder binder) {
        settings.setDateMode(DateMode.DAY);

        Calendar c = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        c.add(Calendar.DATE, -1);
        // в пределах предыдущего дня от текущей даты
        settings.setDateFrom(c.getTime());
        settings.setDateTo(c.getTime());
        //binder.sendCommand("search", null);
        savePipelineSettings();
    }

    @Command
    @NotifyChange({"dateFrom", "dateTo", "dateMode", "dayMode", "monthMode", "yearMode", "filterWarning", "dateToMoreToday",
                   "dateFromMoreDateTo", "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError", "disableSearchButton"})
    public void monthMode(@ContextParam(ContextType.BINDER) Binder binder) {
        settings.setDateMode(DateMode.MONTH);

        // с даты назад на один календарный месяц по день предшествующий текущему дню
        Calendar c = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        Calendar c2 = (Calendar) c.clone();
        c.add(Calendar.MONTH, -1);
        settings.setDateFrom(c.getTime());
        c2.add(Calendar.DATE, -1);
        settings.setDateTo(c2.getTime());
        //if (binder != null)
        //    binder.sendCommand("search", null);
        savePipelineSettings();
    }

    @Command
    @NotifyChange({"dateFrom", "dateTo", "dateMode", "dayMode", "monthMode", "yearMode", "filterWarning", "dateToMoreToday",
                   "dateFromMoreDateTo", "dateToError", "dateToCompareError", "dateFromError", "dateFromCompareError", "disableSearchButton"})
    public void yearMode(@ContextParam(ContextType.BINDER) Binder binder) {
        settings.setDateMode(DateMode.YEAR);

        // с даты назад на один календарный год по день предшествующий текущему дню
        Calendar c = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        Calendar c2 = (Calendar) c.clone();
        c.add(Calendar.YEAR, -1);
        settings.setDateFrom(c.getTime());
        c2.add(Calendar.DATE, -1);
        settings.setDateTo(c2.getTime());
        //binder.sendCommand("search", null);
        savePipelineSettings();
    }

    private void clearDateMode() {
        settings.setDateMode(null);
        savePipelineSettings();
    }

    private String constructRepotName(boolean mainPeriod) {
        Date dateFrom = mainPeriod ? settings.getDateFrom() : settings.getDateFromCompare();
        Date dateTo = mainPeriod ? settings.getDateTo() : settings.getDateToCompare();

        String result = "Pipline";
        if (dateFrom != null || dateTo != null) {
            result += " (";
            if (dateFrom != null) {
                result += reportDf.format(dateFrom);
                if (dateTo != null)
                    result += "-";
            }
            if (dateTo != null)
                result += reportDf.format(dateTo);
            result += ")";
        }
        return result;
    }

    private String constructWorksheetName(boolean mainPeriod) {
        Date dateFrom = mainPeriod ? settings.getDateFrom() : settings.getDateFromCompare();
        Date dateTo = mainPeriod ? settings.getDateTo() : settings.getDateToCompare();

        String result = "";
        if (dateFrom != null || dateTo != null) {
            if (dateFrom != null) {
                result += reportDf.format(dateFrom);
                if (dateTo != null)
                    result += "-";
            }
            if (dateTo != null)
                result += reportDf.format(dateTo);
        }
        return result;
    }

    private String formatSumValue(BigDecimal val) {
        if (val == null)
            return "";
        return dashboardService.getNativeCurrencySumFormated(val.doubleValue());
    }

    private String getIndRate(DetailReportRow reportRow) {
        String result = "";

        if (!reportRow.getInterestRateFixed() && reportRow.getInterestRateDerivative()) {
            List<IndRate> rates = dashboardService.getRate(reportRow.getIdSumHistory());
            if (rates != null) {
                for (IndRate rate : rates) {
                    if (rate.getName() == null)
                        continue;
                    result += rate.getName();
                    if (rate.getRate() != null) {
                        result += " + " + Formatter.format2point(rate.getRate()) + "%\n";
                    }
                }
            }
        } else
            result = Formatter.format2point(reportRow.getRate());
        return result;
    }

    private void processReportRow(DetailReportRow reportRow) {
        //тип сделки
        reportRow.setTaskTypeReport(getTaskTypeName(reportRow.getTaskType()));
        //форматирование сумм
        reportRow.setSumReport(formatSumValue(reportRow.getSum()));
        reportRow.setLineCountReport(formatSumValue(reportRow.getLineCount()));
        reportRow.setAvailibleLineVolumeReport(formatSumValue(reportRow.getAvailibleLineVolume()));
        reportRow.setSumLastReport(formatSumValue(reportRow.getSumLast()));
        //дата создания заявки
        reportRow.setDealCreateDate(dashboardService.getDealCreateDate(reportRow.getIdMdtask()));
        //дата изменения заявки
        reportRow.setDealChangeDate(dashboardService.getDealChangeDate(reportRow.getIdMdtask()));
        //клиентский менеджер
        reportRow.setClientManagerReportName(dashboardService.getDealTeamMemberByRoleName(reportRow.getIdMdtask(), ROLE_CLIENT_MANAGER));
        //продуктовый менеджер
        reportRow.setProductManagerReportName(dashboardService.getDealTeamMemberByRoleName(reportRow.getIdMdtask(), ROLE_PRODUCT_MANAGER));
        //структуратор
        reportRow.setStructuratorReportName(dashboardService.getDealTeamMemberByRoleName(reportRow.getIdMdtask(), ROLE_STRUCTURATOR));
        //кредитный аналитик
        reportRow.setCreditAnalystReportName(dashboardService.getDealTeamMemberByRoleName(reportRow.getIdMdtask(), ROLE_CREDIT_ANALYST));
        //индикативная ставка
        reportRow.setIndRate(getIndRate(reportRow));
    }

    private String calcReportSum(List<DetailReportRow> list) {
        Double sum = 0.0;
        if (list != null) {
            Sum sumParams;
            Date rateDate = (settings.getDateTo() == null) ? new Date() : settings.getDateTo();
            for (DetailReportRow reportRow : list) {
                if (reportRow.getCurrency() == null || reportRow.getSum() == null)
                    continue;
                sumParams = new Sum(reportRow.getCurrency(), reportRow.getSum().doubleValue());
                sum += dashboardService.toRur(sumParams, rateDate);
            }
        }
        return dashboardService.getRurSumFormated(sum);
    }

    private String createReportTitle(List<DetailReportRow> list, Long statusId, boolean mainPeriod) {
        if (list == null)
            return "";
        String result = "Список ";
        if (getCreditDocumentaryGenitiveName() != null)
            result += getCreditDocumentaryGenitiveName() + " ";
        result += dashboardService.getTitleTypeByCode(taskType);

        if (statusId != null) {
            EDashStatus status = EDashStatus.find(statusId);
            if (status != null)
                result += " со статусом \"" + status.getName() + "\"";

        }

        Date dateFrom = mainPeriod ? settings.getDateFrom() : settings.getDateFromCompare();
        Date dateTo = mainPeriod ? settings.getDateTo() : settings.getDateToCompare();

        if (dateFrom != null || dateTo != null) {
            if (statusId == null)
                result += " за период ";
            else
                result += (mainPeriod ? " за отчётный период " : " за период сравнения ");
            if (dateFrom != null) {
                result += "с " + reportDf.format(dateFrom) + " ";
                if (dateTo != null)
                    result += "по ";
            }
            if (dateTo != null)
                result += reportDf.format(dateTo) + " ";
        }
        result += "на сумму " + calcReportSum(list);
        return result;
    }

    /**
     * Подготовка имени файла отчёта с учётом соместимости с IE (замена пробелов и т.д.).
     * @param rawFilename исходное имя файла.
     * @return именя файла отчёта с учётом соместимости с IE
     */
    private String prepaireFileName(String rawFilename) {
        String result = rawFilename;
        if (rawFilename != null)
            result = rawFilename.replace(" ", "_");
        return result;
    }

    /**
     * Генерация отчёта Excel или Pdf.
     * @param statusId статус для которого необходимо показывать детализацию. null еслм команда пришла из общего выпатающего переключателя.
     * @param category значения из @link{PipelineConstants.Category}. Если значение == null то использовать основной диапазон дат.
     * @param excelReport флаг типа отчёта. eсли == true, то отчёт Excel, если == false, то отчёт Pdf
     * @throws FileNotFoundException
     */
    private void detailReport(Integer statusId, String category, boolean excelReport, String branch) throws FileNotFoundException {
        DashboardReportData dataContainer = new DashboardReportData();

        Long statusIdLong = (statusId == null) ? null : statusId.longValue();
        boolean mainPeriod = (category == null || category.equals(REPORT_PERIOD));
        Date dateFrom = mainPeriod ? settings.getDateFrom() : settings.getDateFromCompare();
        Date dateTo = mainPeriod ? settings.getDateTo() : settings.getDateToCompare();

        List<DetailReportRow> list = dashboardService.getDetailReport(dateFrom,
                                                                      dateTo,
                                                                      taskType,
                                                                      creditDocumentary,
                                                                      settings.getTradingDeskSelected(),
                                                                      settings.isTradingDeskOthers(),
                                                                      settings.getDepartmentsSelected(),
                                                                      statusId, branch);
        if (list != null) {
            for (DetailReportRow reportRow : list)
                processReportRow(reportRow);
        }

        Long[] departments = null;
        if (settings.getDepartmentsSelected() != null && settings.getDepartmentsSelected().size() != 0) {
            departments = new Long[settings.getDepartmentsSelected().size()];
            int i = 0;
            for (DepartmentExt dep : settings.getDepartmentsSelected()) {
                departments[i] = dep.getId();
                i++;
            }
        }

        Long[] tradingDeskSelected = null;
        if (settings.getTradingDeskSelected() != null && settings.getTradingDeskSelected().size() != 0) {
            tradingDeskSelected = new Long[settings.getTradingDeskSelected().size()];
            int i = 0;
            for (PipelineTradingDesk desk : settings.getTradingDeskSelected()) {
                tradingDeskSelected[i] = new Long(desk.getId());
                i++;
            }
        }

        String title = dashboardService.getTitle(statusIdLong, taskType, dateFrom, dateTo,
                                                 (creditDocumentary == null) ? null : creditDocumentary.longValue(),
                                                 departments,
                                                 tradingDeskSelected, settings.isTradingDeskOthers(), mainPeriod, branch);
        String fileName = dashboardService.getFileName(statusIdLong, taskType, dateFrom, dateTo,
                                                       (creditDocumentary == null) ? null : creditDocumentary.longValue(),
                                                       departments,
                                                       tradingDeskSelected, settings.isTradingDeskOthers(), mainPeriod);
        //подготовка имени файла с учётом совместимости с IE
        fileName = prepaireFileName(fileName);

        //dataContainer.setTitle(createReportTitle(list, statusIdLong, mainPeriod));
        dataContainer.setTitle(title);
        dataContainer.setReportRows(list);
        dataContainer.setLimit(taskType != null && taskType.equals("limit"));
        dataContainer.setWorkSheetName(constructWorksheetName(mainPeriod));

        byte[] result;
        if (excelReport)
            result = reporterService.buidReport(DASHBOARD_EXCEL_REPORT, dataContainer);
        else
            result = reporterService.buildPdfFromExcelReport(DASHBOARD_EXCEL_PDF_REPORT, dataContainer);

        ByteArrayInputStream is = new ByteArrayInputStream(result);
        if (is != null) {
            if (excelReport)
                Filedownload.save(is, "application/vnd.ms-excel", /*constructRepotName(mainPeriod)*/fileName + ".xlsx");
            else
                Filedownload.save(is, "application/x-pdf", /*constructRepotName(mainPeriod)*/fileName + ".pdf");
        }
    }

    /**
     * Генерация отчёта Excel.
     * @param statusId статус для которого необходимо показывать детализацию. null еслм команда пришла из общего выпатающего переключателя.
     * @param category значения из @link{PipelineConstants.Category}. Если значение == null то использовать основной диапазон дат.
     * @throws FileNotFoundException
     */
    @Command
    public void detailReport(@BindingParam(PipelineConstants.DetailReportAttribute.STATUS_ID) Integer statusId,
                             @BindingParam(PipelineConstants.DetailReportAttribute.BRANCH) String branch,
                             @BindingParam(PipelineConstants.DetailReportAttribute.CATEGORY) String category) throws FileNotFoundException {
        detailReport(statusId, category, true, branch); //отчёт Excel
    }

    /**
     * Генерация отчёта Pdf.
     * @param statusId статус для которого необходимо показывать детализацию. null еслм команда пришла из общего выпатающего переключателя.
     * @param category значения из @link{PipelineConstants.Category}. Если значение == null то использовать основной диапазон дат.
     * @throws FileNotFoundException
     */
    @Command
    public void detailReportPdf(@BindingParam(PipelineConstants.DetailReportAttribute.STATUS_ID) Integer statusId,
                                @BindingParam(PipelineConstants.DetailReportAttribute.BRANCH) String branch,
                                @BindingParam(PipelineConstants.DetailReportAttribute.CATEGORY) String category) throws FileNotFoundException {
        detailReport(statusId, category, false, branch); //отчёт Pdf
    }

    /**
     * @param statusId статус для которого необходимо показывать детализацию. null еслм команда пришла из общего выпатающего переключателя.
     * @param category значения из @link{PipelineConstants.Period}. Если значение == null то использовать основной диапазон дат.
     */
    @Command
    public void detailTable(@BindingParam(PipelineConstants.DetailReportAttribute.STATUS_ID) Integer statusId,
                            @BindingParam(PipelineConstants.DetailReportAttribute.CATEGORY) String category,
                            @BindingParam(PipelineConstants.DetailReportAttribute.BRANCH) String branch,
                            @ContextParam(ContextType.EXECUTION) Execution execution) {
        LOGGER.info("statusId=" + statusId);
        LOGGER.info("category=" + category);
        LOGGER.info("branch=" + branch);
        boolean mainPeriod = category == null || category.equals(Period.REPORT);
        Long from = mainPeriod ? settings.getDateFrom().getTime() : settings.getDateFromCompare().getTime();
        Long to = mainPeriod ? settings.getDateTo().getTime() : settings.getDateToCompare().getTime();
        String statusOrTypeFilter = statusId == null ? "taskType=" + getTaskType() : "statusid=" + statusId;
        String url = "/../ProdflexWorkflow/dash_list_table.html?" + statusOrTypeFilter +
                "&from=" + from + "&to=" + to + "&mainPeriod=" + mainPeriod;
        if (settings.getDepartmentsSelectedNodes() != null && settings.getDepartmentsSelectedNodes().size() > 0)
            url += "&departments=" + settings.getDepartmentsCommaSeparated();
        if (settings.getTradingDeskSelected() != null && settings.getTradingDeskSelected().size() > 0)
            url += "&tradingDeskSelected=" + settings.getTradingDeskCommaSeparated();
        //также для фильтрации по прочим традинг дескам должен учитываться параметр фильтрации settings.isTradingDeskOthers
        url += "&isTradingDeskOthers=" + String.valueOf(settings.isTradingDeskOthers());
        url += "&creditDocumentary=" + (getCreditDocumentary() == null ? "0" : getCreditDocumentary().toString());
        if (branch != null)
            url += "&branch=" + branch;
        execution.sendRedirect(url, "_blank");
    }

    public boolean isColumnChartMode() {
        //System.out.println("isColumnChartMode - " + settings.getChartType());
        return (settings.getChartType() == ChartType.COLUMN);
    }

    public boolean isPieChartMode() {
        //System.out.println("isPieChartMode - " + settings.getChartType());
        return (settings.getChartType() == ChartType.PIE);
    }

    public boolean isLineChartMode() {
        //System.out.println("isLineChartMode - " + settings.getChartType());
        return (settings.getChartType() == ChartType.LINE);
    }

    @Command
    @NotifyChange({"columnChartMode", "pieChartMode", "lineChartMode",
                   //"generalChartModel", "generalPieChartModel", "generalLineChartModel",
                   "taskTypeStatuses",
                   "characteristics", "chartLegendSeries", "pieIndustries", "pieChartColors"})
    public void columnChartMode() {
        settings.setChartType(ChartType.COLUMN);
        savePipelineSettings();
        notifyChartModelChange();
    }

    @Command
    @NotifyChange({"columnChartMode", "pieChartMode", "lineChartMode",
                   //"generalChartModel", "generalPieChartModel", "generalLineChartModel",
                   "taskTypeStatuses",
                   "characteristics", "chartLegendSeries", "pieIndustries", "pieChartColors"})
    public void pieChartMode() {
        settings.setChartType(ChartType.PIE);
        savePipelineSettings();
        notifyChartModelChange();
    }

    @Command
    @NotifyChange({"columnChartMode", "pieChartMode", "lineChartMode",
                   //"generalChartModel", "generalPieChartModel", "generalLineChartModel",
                   "taskTypeStatuses",
                   "characteristics", "chartLegendSeries", "pieIndustries", "pieChartColors"})
    public void lineChartMode() {
        settings.setChartType(ChartType.LINE);
        savePipelineSettings();
        notifyChartModelChange();
    }

    public List<ChartLegend.LegendSeries> getChartLegendSeries() {
        List<ChartLegend.LegendSeries> l = new ArrayList<ChartLegend.LegendSeries>();
        PipelineSettings.ChartSeriesSelection selection = settings.getChartSeriesUnSelection();

        if (isColumnChartMode()) {
            l.add(new ChartLegend.LegendSeries(CharacteristicsE.SUM_RUB.toString(), Characteristics.SUM_RUB,
                                               selection.isSelected(CharacteristicsE.SUM_RUB.toString())));
            l.add(new ChartLegend.LegendSeries(CharacteristicsE.COUNT_ALL.toString(), Characteristics.COUNT_ALL,
                                               selection.isSelected(CharacteristicsE.COUNT_ALL.toString())));
            if (!taskType.equals("limit"))
                l.add(new ChartLegend.LegendSeries(CharacteristicsE.WAV_MARGIN.toString(), Characteristics.WAV_MARGIN,
                                                   selection.isSelected(CharacteristicsE.WAV_MARGIN.toString())));
        } else if (isLineChartMode()) {
            List<TaskTypeStatus> tsl = getTaskTypeStatuses();
            for (TaskTypeStatus ts : tsl) {
                String id = Integer.toString(ts.getIdStatus());
                l.add(new ChartLegend.LegendSeries(id, ts.getStatus(), selection.isSelected(id)));
            }
        }

        return l;
    }

    @Command
    @NotifyChange({"chartSeriesIdInvisible", "generalLineChartModel", "generalLineChartTitle"})
    public void chartLegendChange(@BindingParam("chartEvent") ChartsEvent chartEvent, @ContextParam(ContextType.VIEW) Component view) {
        Series legendSeries = chartEvent.getSeries();
        if (legendSeries == null)
            return;

        boolean toDoSelection = !legendSeries.isVisible();
        chartEvent.getSeries().setVisible(toDoSelection);
        if (toDoSelection)
            settings.getChartSeriesUnSelection().getSelected().remove(legendSeries.getId());
        else
            settings.getChartSeriesUnSelection().getSelected().add(legendSeries.getId());
        savePipelineSettings();
        Component p = view.query("#chartPanel");
        if (p != null)
            p.invalidate();
    }

    public Set<String> getChartSeriesIdInvisible() {
        return settings.getChartSeriesUnSelection().getSelected();
    }

    @Command
    public void closeSummaryFigure(@BindingParam("summaryData") SummaryData summaryData, @BindingParam("closeEvent") Event closeEvent) {
        doSelectSummaryData(summaryData, false, closeEvent.getTarget());
    }

    public void doSelectSummaryData(SummaryData summaryData, boolean selected, Component targetFigurePanel) {
        summaryData.setSelected(selected);
        if (selected)
            settings.getSummarySelection().addSelection(summaryData.getSummaryFigure());
        else
            settings.getSummarySelection().removeSelection(summaryData.getSummaryFigure());

        BindUtils.postNotifyChange(null, null, PipelineVM.this, "addSummaryVisible");

        if (settings.getSummarySelection().getSelected().size() >= MIN_SUMMARY_TO_SYNC_HEIGHT) {
            Component p = targetFigurePanel.query("#summaryPanel");
            p.invalidate();
            p = targetFigurePanel.query("#chartPanel");
            p.invalidate();

            p = targetFigurePanel.query("#allPortal");
            p.invalidate();
        }
        savePipelineSettings();
    }

    public boolean isAddSummaryVisible() {
        return settings.getSummarySelection().getSelected().size() <= MIN_SUMMARY_TO_SYNC_HEIGHT;
    }

    @Command
    public void gridColumnVisibilityChange() {
        //System.out.println("gridColumnVisibilityChange");
        savePipelineSettings();
    }

    @Command
    public void dropGridColumn(@ContextParam(ContextType.TRIGGER_EVENT) DropEvent event, @BindingParam("isTop") Boolean isTop) {
        DataGridColumn draggedColumn = (DataGridColumn) event.getDragged();
        DataGridColumn droppedColumn = (DataGridColumn) event.getTarget();
        if (!draggedColumn.getParent().equals(droppedColumn.getParent()))
            return;

        Columns columns = (Columns) draggedColumn.getParent();
        int draggedColumnIndex = columns.getChildren().indexOf(draggedColumn);
        int droppedColumnIndex = columns.getChildren().indexOf(droppedColumn);
        boolean append = (droppedColumnIndex == columns.getChildren().size() - 1);

        Grid grid = (Grid) columns.getParent();

        Rows rows = grid.getRows();
        for (int i = 0; i < rows.getChildren().size(); i++) {
            Row row = (Row) rows.getChildren().get(i);
            if (append)
                row.appendChild(row.getChildren().get(draggedColumnIndex));
            else
                row.insertBefore(row.getChildren().get(draggedColumnIndex), row.getChildren().get(droppedColumnIndex + 1));
        }

        boolean isIsTop = (isTop == null) ? false : isTop;
        PipelineSettings.GridColumnSelection selection = isIsTop ? settings.getTopGridColumnSelection() : settings.getStateGridColumnSelection();

        if (append) {
            selection.moveToEnd(draggedColumn.getMetadata());
            columns.appendChild(draggedColumn);
        } else {
            selection.moveToBefore(draggedColumn.getMetadata(), ((DataGridColumn)droppedColumn.getNextSibling()).getMetadata());
            columns.insertBefore(draggedColumn, droppedColumn.getNextSibling());
        }

        savePipelineSettings();
    }

    public void savePipelineSettings() {
        //System.out.println("savePipelineSettings");
        if (settings == null)
            return;
        dashboardService.savePipelineSettings(PipelineSettings.SETTING_USER == null ? settings.getUserId() : PipelineSettings.SETTING_USER,
                                              settings.serialize());
    }

    @org.zkoss.bind.annotation.GlobalCommand
    public void reportApply(@BindingParam("rn") Long rn,@ContextParam(ContextType.EXECUTION) Execution execution,
                            @ContextParam(ContextType.SESSION) Session session) {
        LOGGER.info("reportApply "+rn);
        String settingJson = SBeanLocator.getDashboardMapper().getPipelineSettingsName(rn);
        LOGGER.info("settingJson=" + settingJson);
        if (!Formatter.str(settingJson).isEmpty()) {
            PipelineSettings loadsettings = new PipelineSettings(settings.getUser(), settingJson, "");
            settings = loadsettings;
            savePipelineSettings();
            session.setAttribute(C_SESSION_SETTINGS, settings);
        }
        execution.sendRedirect("index.zul");
    }

    @Command
    @NotifyChange({"chartPanelVisible", "top3PanelVisible", "summaryPanelVisible"})
    public void settingsPanelCheck(@BindingParam("checkEvent") CheckEvent checkEvent) {
        //System.out.println("settingsPanelCheck - " + checkEvent);

        if ("chartPanelSwitch".equals(checkEvent.getTarget().getId())) {
            settings.setChartPanelOn(checkEvent.isChecked());
        } else if ("top3PanelSwitch".equals(checkEvent.getTarget().getId())) {
            settings.setTop3PanelOn(checkEvent.isChecked());
        } else if ("summaryPanelSwitch".equals(checkEvent.getTarget().getId())) {
            settings.setSummaryPanelOn(checkEvent.isChecked());

        } else {
            throw new RuntimeException("Unknown panel switcher: " + checkEvent.getTarget().getId());
        }
    }

    @Command
    @NotifyChange({"chartPanelVisible", "top3PanelVisible", "summaryPanelVisible"})
    public void panelClose(@BindingParam("closeEvent") Event closeEvent) {
        //System.out.println("panelClose - " + closeEvent);

        if ("chartPanel".equals(closeEvent.getTarget().getId())) {
            settings.setChartPanelOn(false);
        } else if ("top3Panel".equals(closeEvent.getTarget().getId())) {
            settings.setTop3PanelOn(false);
        } else if ("summaryPanel".equals(closeEvent.getTarget().getId())) {
            settings.setSummaryPanelOn(false);
        }
    }

    public boolean isChartPanelVisible() {
        return settings.isChartPanelOn();
    }

    public boolean isTop3PanelVisible() {
        return settings.isTop3PanelOn();
    }

    public boolean isSummaryPanelVisible() {
        return settings.isSummaryPanelOn();
    }

}
