package ru.masterdm.spo.dashboard;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.TreeNode;

import com.google.gson.Gson;
import com.vtb.util.CollectionUtils;
import ru.md.domain.DepartmentExt;
import ru.md.domain.User;
import ru.md.domain.dashboard.PipelineTradingDesk;

import ru.masterdm.spo.dashboard.model.SummaryFigure;
import ru.masterdm.spo.dashboard.model.SummaryFigureLimit;
import ru.masterdm.spo.dashboard.model.SummaryFigureOther;
import ru.masterdm.spo.dashboard.model.metadata.GridColumnItem;
import ru.masterdm.spo.dashboard.model.metadata.GridColumnMetadata;
import ru.masterdm.spo.dashboard.model.metadata.GridStateLimitItem;
import ru.masterdm.spo.dashboard.model.metadata.GridStateOtherItem;
import ru.masterdm.spo.dashboard.model.metadata.GridTopLimitItem;
import ru.masterdm.spo.dashboard.model.metadata.GridTopOtherItem;
import ru.masterdm.spo.list.ETaskType;
import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

/**
 * Настройки и параметры дашборда создаваемые и сохраняемые в разрезе сессии пользователя
 * @author pmasalov
 */
public class PipelineSettings implements DatePeriod {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineSettings.class);
    public static final Long SETTING_USER = null;

    /** фильтр по датам */
    protected class DateFilter {

        public Date dateFrom, dateTo, dateFromCompare, dateToCompare;
        /** Установленный режим быстрого выбора текущего периуда. Null если быстрый режим не установлен */
        public DateMode dateMode;

        public String toJSON() {
            return "{" +
                    "dateFrom:_" + Formatter.format(dateFrom) +
                    "_, dateTo:_" + Formatter.format(dateTo) +
                    "_, dateFromCompare:_" + Formatter.format(dateFromCompare) +
                    "_, dateToCompare:" + Formatter.format(dateToCompare) +"_"+
                    (dateMode==null?"":", dateMode:_" + dateMode.name()+"_") +
                    '}';
        }

        public DateFilter() {
        }
        public DateFilter(String s) {
            Gson gson = new Gson();
            DateFilterSerialize dfs = gson.fromJson(s.replaceAll("_","\""),DateFilterSerialize.class);
            dateFrom = Formatter.parseDate(dfs.dateFrom);
            dateTo = Formatter.parseDate(dfs.dateTo);
            dateFromCompare = Formatter.parseDate(dfs.dateFromCompare);
            dateToCompare = Formatter.parseDate(dfs.dateToCompare);
            if (dfs.dateMode != null)
                dateMode = DateMode.valueOf(dfs.dateMode);
        }
    }
    protected class DateFilterSerialize {
        public String dateFrom, dateTo, dateFromCompare, dateToCompare, dateMode;
    }

    private HashMap<String, DateFilter> dateFilter = new HashMap<String, DateFilter>();

    protected class PortalPosition {

        public List<String> allPortal, portalLayout;
    }

    private HashMap<String, PortalPosition> portalPosition = new HashMap<String, PortalPosition>();

    private HashMap<String, ChartType> chartTypes = new HashMap<String, ChartType>();

    private User user;
    private TreeNode<DepartmentExt> userDepartmentTreeNode;

    private HashMap<String, Set<PipelineTradingDesk>> tradingDeskSelectedMap = new HashMap<String, Set<PipelineTradingDesk>>();

    private HashMap<String, Set<TreeNode<DepartmentExt>>> departmentsSelectedNodesMap = new HashMap<String, Set<TreeNode<DepartmentExt>>>();

    private Set<DepartmentExt> departmentsSelected;

    protected class PortletOnOff {

        private boolean chartPanelOn = true, top3PanelOn = true, summaryPanelOn = true;
    }

    private HashMap<String, PortletOnOff> portlets = new HashMap<String, PortletOnOff>();

    /** Указатель выбора в меню дашборда */
    protected class MainMenuSelection {

        protected ETaskType taskType;
        protected Integer creditDocumentary;
        protected String hash;

        private MainMenuSelection(ETaskType taskType, Integer creditDocumentary) {
            this.taskType = taskType;
            this.creditDocumentary = creditDocumentary;
            calcHashValue();
        }

        private MainMenuSelection(ETaskType taskType) {
            this(taskType, null);
        }

        protected void calcHashValue() {
            String result = taskType.getCode() + ":";
            result = result + (creditDocumentary != null ? creditDocumentary.toString() : "");
            hash = result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MainMenuSelection that = (MainMenuSelection) o;

            if (!taskType.equals(that.taskType)) return false;
            return creditDocumentary != null ? creditDocumentary.equals(that.creditDocumentary) : that.creditDocumentary == null;
        }

        public String hash() {
            return hash;
        }

        @Override
        public String toString() {
            return "MainMenuSelection{" +
                    "taskType=" + taskType +
                    ", creditDocumentary=" + creditDocumentary +
                    '}';
        }
    }

    /** Выбранные элементы (колонки таблицы, плашки показателей) с распределением зависимости от выбранного текущего пункта меню */
    public abstract class Selection<E> {

        Map<String, Set<E>> selectionSetMap = new HashMap<String, Set<E>>();

        public void addSelection(E e) {
            getSelected().add(e);
        }

        public void removeSelection(E e) {
            getSelected().remove(e);
        }

        public Set<E> getSelected() {
            Set<E> s = selectionSetMap.get(mainMenuSelectionHash());
            if (s == null) {
                s = loadSelection();
                if (s == null)
                    s = defaultSelection();
                selectionSetMap.put(mainMenuSelectionHash(), s);
            }
            return s;
        }

        public void resetSelection() {
            selectionSetMap.remove(mainMenuSelectionHash());
        }

        public boolean isSelected(E e) {
            return getSelected().contains(e);
        }

        private Set<E> loadSelection() {
            // зарезервированно для загрузки селекции из базы
            return null;
        }

        public abstract Set<E> defaultSelection();
    }

    public class SummarySelection extends Selection<SummaryFigure> {

        public Set<SummaryFigure> defaultSelection() {
            Set summaryFigureSet = new HashSet<SummaryFigure>();
            for (SummaryFigure f : (mainMenuSelection.taskType.equals(ETaskType.LIMIT) ? SummaryFigureLimit.values() : SummaryFigureOther.values())) {
                if (f.isDefault())
                    summaryFigureSet.add(f);
            }
            return summaryFigureSet;
        }
    }

    public static class GridColumnSetting implements GridColumnMetadata {

        private boolean visible;
        private GridColumnItem originalItem;

        protected GridColumnSetting(GridColumnItem originalItem) {
            this.originalItem = originalItem;
            visible = originalItem.isDefault();
        }

        @Override
        public String getDescription() {
            return originalItem.getDescription();
        }

        @Override
        public String getUnitOfMeasure() {
            return originalItem.getUnitOfMeasure();
        }

        @Override
        public String getCode() {
            return originalItem.getCode();
        }

        @Override
        public boolean isDefault() {
            return originalItem.isDefault();
        }

        @Override
        public String getNumberFormat() {
            return originalItem.getNumberFormat();
        }

        @Override
        public String getDateFormat() {
            return originalItem.getDateFormat();
        }

        @Override
        public boolean isLabel() {
            return originalItem.isLabel();
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public boolean isRight() {
            return originalItem.isRight();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof GridColumnItem)) return false;

            if (o instanceof GridColumnSetting) {
                GridColumnSetting that = (GridColumnSetting) o;
                return originalItem.equals(that.originalItem);
            } else {
                return originalItem.equals(o);
            }
        }

        @Override
        public int hashCode() {
            return originalItem.hashCode();
        }
    }

    /** Колонки гридов являются как бы выбранными всеми и всегда. видимость определяется свойством GridColumnSetting.visible */
    public class GridColumnSelection extends Selection<GridColumnMetadata> {

        private GridColumnItem[] limit, others;

        private GridColumnSelection(GridColumnItem[] limit, GridColumnItem[] others) {
            this.limit = limit;
            this.others = others;
        }

        public Set<GridColumnMetadata> defaultSelection() {
            Set columnItemsSet = new ListOrderedSet();
            GridColumnItem[] items = (mainMenuSelection.taskType.equals(ETaskType.LIMIT) ? limit : others);
            for (GridColumnItem i : items) {
                columnItemsSet.add(new GridColumnSetting(i));
            }
            return columnItemsSet;
        }

        public void moveToEnd(GridColumnMetadata moveThis) {
            Set set = getSelected();
            set.remove(moveThis);
            set.add(moveThis);
        }

        public void moveToBefore(GridColumnMetadata moveThis, GridColumnMetadata beforeThis) {
            if (moveThis == beforeThis)
                return;

            ListOrderedSet set = (ListOrderedSet) getSelected();
            set.remove(moveThis);
            int i = set.indexOf(beforeThis);
            if (i >= 0) {
                set.add(i, moveThis);
            } else {
                set.add(moveThis);
            }
        }
    }

    public class ChartSeriesSelection extends Selection<String> {

        @Override
        public Set<String> defaultSelection() {
            return new HashSet<String>();
        }

        public boolean isSelected(String seriesId) {
            Set<String> ss = getSelected();
            return (ss == null || ss.size() == 0 || !ss.contains(seriesId));
        }
    }

    MainMenuSelection mainMenuSelection = new MainMenuSelection(ETaskType.PRODUCT);

    //Map<MainMenuSelection, Set<SummaryFigure>> mainMenuSelectionSetMap = new HashMap<MainMenuSelection, Set<SummaryFigure>>();
    private SummarySelection summarySelection = new SummarySelection();

    GridColumnSelection stateGridColumnSelection = new GridColumnSelection(GridStateLimitItem.values(), GridStateOtherItem.values());
    GridColumnSelection topGridColumnSelection = new GridColumnSelection(GridTopLimitItem.values(), GridTopOtherItem.values());

    // не выбранные (невидимые) серии графиков
    ChartSeriesSelection columnChartUnSelection = new ChartSeriesSelection();
    ChartSeriesSelection splineChartUnSelection = new ChartSeriesSelection();

    DatePeriod compareDatePeriod = new DatePeriod() {

        @Override
        public Date getDateFrom() {
            return getDateFromCompare();
        }

        @Override
        public void setDateFrom(Date dateFrom) {
            setDateFromCompare(dateFrom);
        }

        @Override
        public Date getDateTo() {
            return getDateToCompare();
        }

        @Override
        public void setDateTo(Date dateTo) {
            setDateToCompare(dateTo);
        }
    };

    public PipelineSettings(User user) {
        this.user = user;
    }

    /** Устанавливает значение дат по умолчанию */
    public DateFilter defaultDates() {
        DateFilter f = new DateFilter();
        Calendar c = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        c.set(2016, 11, 4);
        f.dateTo = c.getTime(); // 2016-12-04
        c.set(java.util.Calendar.DAY_OF_MONTH, 1);
        c.set(2016, 8, 9);
        f.dateFrom = c.getTime(); // 2016-09-09
        c = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        c.add(java.util.Calendar.MONTH, -1);
        c.set(2016, 8, 8);
        f.dateToCompare = c.getTime(); // 2016-09-08
        c.set(java.util.Calendar.DAY_OF_MONTH, 1);
        c.set(2016, 4, 9);
        f.dateFromCompare = c.getTime(); // 2016-05-09
        return f;
    }

    /** Дла маппировки параметров зависящих от выбора позиции в левом меню дашборд taskType */
    void setMainMenuSelection(String taskType, Integer creditDocumentary) {
        ETaskType tt = ETaskType.findByCode(taskType);
        if (tt == null)
            tt = ETaskType.PRODUCT;
        mainMenuSelection = new MainMenuSelection(tt, creditDocumentary);
    }

    /**
     * Returns .
     * @return
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * Returns .
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns .
     * @return
     */
    public TreeNode<DepartmentExt> getUserDepartmentTreeNode() {
        return userDepartmentTreeNode;
    }

    /**
     * Sets .
     * @param userDepartmentTreeNode
     */
    public void setUserDepartmentTreeNode(TreeNode<DepartmentExt> userDepartmentTreeNode) {
        this.userDepartmentTreeNode = userDepartmentTreeNode;
        // департамент пользователя всегда выбран
        if (!departmentsSelectedNodesMap.containsKey(mainMenuSelectionHash()))
            departmentsSelectedNodesMap.put(mainMenuSelectionHash(), new HashSet<TreeNode<DepartmentExt>>());
        departmentsSelectedNodesMap.get(mainMenuSelectionHash()).add(userDepartmentTreeNode);
        departmentsSelected = null;
    }

    public SummarySelection getSummarySelection() {
        return summarySelection;
    }

    public GridColumnSelection getStateGridColumnSelection() {
        return stateGridColumnSelection;
    }

    public GridColumnSelection getTopGridColumnSelection() {
        return topGridColumnSelection;
    }

    public PortalPosition getPortalPosition() {
        if (!portalPosition.containsKey(mainMenuSelectionHash()))
            portalPosition.put(mainMenuSelectionHash(), new PortalPosition());
        return portalPosition.get(mainMenuSelectionHash());
    }

    public PortletOnOff getPortlets() {
        if (!portlets.containsKey(mainMenuSelectionHash()))
            portlets.put(mainMenuSelectionHash(), new PortletOnOff());
        return portlets.get(mainMenuSelectionHash());
    }

    public boolean isChartPanelOn() {
        return getPortlets().chartPanelOn;
    }

    /**
     * Sets .
     * @param chartPanelOn
     */
    public void setChartPanelOn(boolean chartPanelOn) {
        getPortlets().chartPanelOn = chartPanelOn;
    }

    /**
     * Returns .
     * @return
     */
    public boolean isTop3PanelOn() {
        return getPortlets().top3PanelOn;
    }

    /**
     * Sets .
     * @param top3PanelOn
     */
    public void setTop3PanelOn(boolean top3PanelOn) {
        getPortlets().top3PanelOn = top3PanelOn;
    }

    /**
     * Returns .
     * @return
     */
    public boolean isSummaryPanelOn() {
        return getPortlets().summaryPanelOn;
    }

    /**
     * Sets .
     * @param summaryPanelOn
     */
    public void setSummaryPanelOn(boolean summaryPanelOn) {
        getPortlets().summaryPanelOn = summaryPanelOn;
    }

    public DateFilter getDateFilter() {
        if (!dateFilter.containsKey(mainMenuSelectionHash()))
            dateFilter.put(mainMenuSelectionHash(), defaultDates());
        return dateFilter.get(mainMenuSelectionHash());
    }

    /**
     * Returns .
     * @return
     */
    @Override
    public Date getDateFrom() {
        return getDateFilter().dateFrom;
    }

    /**
     * Sets .
     * @param dateFrom
     */
    @Override
    public void setDateFrom(Date dateFrom) {
        getDateFilter().dateFrom = dateFrom;
    }

    /**
     * Returns .
     * @return
     */
    @Override
    public Date getDateTo() {
        return getDateFilter().dateTo;
    }

    /**
     * Sets .
     * @param dateTo
     */
    @Override
    public void setDateTo(Date dateTo) {
        getDateFilter().dateTo = dateTo;
    }

    /**
     * Returns .
     * @return
     */
    public DateMode getDateMode() {
        return getDateFilter().dateMode;
    }

    /**
     * Sets .
     * @param dateMode
     */
    public void setDateMode(DateMode dateMode) {
        getDateFilter().dateMode = dateMode;
    }

    /**
     * Returns .
     * @return
     */
    public Date getDateFromCompare() {
        return getDateFilter().dateFromCompare;
    }

    /**
     * Sets .
     * @param dateFromCompare
     */
    public void setDateFromCompare(Date dateFromCompare) {
        getDateFilter().dateFromCompare = dateFromCompare;
    }

    /**
     * Returns .
     * @return
     */
    public Date getDateToCompare() {
        return getDateFilter().dateToCompare;
    }

    /**
     * Sets .
     * @param dateToCompare
     */
    public void setDateToCompare(Date dateToCompare) {
        getDateFilter().dateToCompare = dateToCompare;
    }

    DatePeriod getCompareDatePeriod() {
        return compareDatePeriod;
    }

    /**
     * Returns .
     * @return
     */
    public Set<PipelineTradingDesk> getTradingDeskSelected() {
        if (tradingDeskSelectedMap.containsKey(mainMenuSelectionHash()))
            return tradingDeskSelectedMap.get(mainMenuSelectionHash());
        return new HashSet<PipelineTradingDesk>();
    }

    /**
     * Sets .
     * @param tradingDeskSelected
     */
    public void setTradingDeskSelected(Set<PipelineTradingDesk> tradingDeskSelected) {
        tradingDeskSelectedMap.put(mainMenuSelectionHash(), tradingDeskSelected);
    }

    /**
     * Returns .
     * @return
     */
    public boolean isTradingDeskOthers() {
        return getTradingDeskSelected().contains(PipelineTradingDesk.OTHERS);
    }

    /**
     * Sets .
     * @param tradingDeskOthers
     */
    public void setTradingDeskOthers(boolean tradingDeskOthers) {
        if (!tradingDeskSelectedMap.containsKey(mainMenuSelectionHash()))
            tradingDeskSelectedMap.put(mainMenuSelectionHash(), new HashSet<PipelineTradingDesk>());
        tradingDeskSelectedMap.get(mainMenuSelectionHash()).add(PipelineTradingDesk.OTHERS);
    }

    /**
     * Returns .
     * @return
     */
    public Set<TreeNode<DepartmentExt>> getDepartmentsSelectedNodes() {
        if (departmentsSelectedNodesMap.containsKey(mainMenuSelectionHash()))
            return departmentsSelectedNodesMap.get(mainMenuSelectionHash());
        return new HashSet<TreeNode<DepartmentExt>>();
    }

    /**
     * Sets .
     * @param departmentsSelectedNodes
     */
    public void setDepartmentsSelectedNodes(Set<TreeNode<DepartmentExt>> departmentsSelectedNodes) {
        departmentsSelectedNodesMap.put(mainMenuSelectionHash(), departmentsSelectedNodes);
        this.departmentsSelected = null;
    }

    private Set<DepartmentExt> extractDataCollectionFromNodes(Collection<TreeNode<DepartmentExt>> nodes) {
        if (nodes == null || nodes.size() == 0)
            return new HashSet<DepartmentExt>();

        HashSet<DepartmentExt> c = new HashSet<DepartmentExt>(Math.max((int) (nodes.size() / .75f) + 1, 16));
        for (TreeNode<DepartmentExt> n : nodes) {
            // псевдо подразделения не показываются
            if (c != null && n != null && n.getData() != null && n.getData().getId().compareTo(0L) > 0)
                c.add(n.getData());
        }
        return c;
    }

    /**
     * Returns .
     * @return
     */
    public Set<DepartmentExt> getDepartmentsSelected() {
        if (departmentsSelected == null)
            departmentsSelected = extractDataCollectionFromNodes(getDepartmentsSelectedNodes());
        return departmentsSelected;
    }

    public String getDepartmentsCommaSeparated() {
        return PipelineSettingsSerializer.jsonfyDepartments(getDepartmentsSelectedNodes());
    }

    public String getTradingDeskCommaSeparated() {
        return PipelineSettingsSerializer.jsonfyTradingDesk(getTradingDeskSelected());
    }

    /**
     * Returns .
     * @return
     */
    public ChartType getChartType() {
        if (chartTypes.containsKey(mainMenuSelectionHash()))
            return chartTypes.get(mainMenuSelectionHash());
        return ChartType.COLUMN;
    }

    public ChartSeriesSelection getChartSeriesUnSelection() {
        return getChartType() == ChartType.COLUMN ? columnChartUnSelection : splineChartUnSelection;
    }

    /**
     * Sets .
     * @param chartType
     */
    public void setChartType(ChartType chartType) {
        chartTypes.put(mainMenuSelectionHash(), chartType);
    }

    public String serialize() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<String, String>();//сериализуемые настройки. Только часть будем сохранять, доставать.
        for (String hash : chartTypes.keySet()) {
            map.put("chartType_" + hash, chartTypes.get(hash).name());
            if (hash.contains("product"))
                LOGGER.info("save chartType_" + hash + " - " + chartTypes.get(hash).name());
        }
        for (String hash : tradingDeskSelectedMap.keySet())
            map.put("tradingDesk_" + hash, PipelineSettingsSerializer.jsonfyTradingDesk(tradingDeskSelectedMap.get(hash)));
        for (String hash : departmentsSelectedNodesMap.keySet())
            map.put("departments_" + hash, PipelineSettingsSerializer.jsonfyDepartments(departmentsSelectedNodesMap.get(hash)));

        // grid columns
        for (String s : stateGridColumnSelection.selectionSetMap.keySet())
            map.put("stateGridColumnSelection_" + s, PipelineSettingsSerializer.jsonfyGridColumnItem(stateGridColumnSelection.selectionSetMap.get(s)));
        for (String s : topGridColumnSelection.selectionSetMap.keySet())
            map.put("topGridColumnSelection_" + s, PipelineSettingsSerializer.jsonfyGridColumnItem(topGridColumnSelection.selectionSetMap.get(s)));

        // summary windows
        for (String s : summarySelection.selectionSetMap.keySet())
            map.put("summarySelection_" + s, PipelineSettingsSerializer.jsonfySummaryFigure(summarySelection.selectionSetMap.get(s)));

        for (String hash : portalPosition.keySet())
            map.put("portalPosition_" + hash, gson.toJson(portalPosition.get(hash)).replaceAll("\"", "_"));
        for (String hash : portlets.keySet())
            map.put("portlets_" + hash, gson.toJson(portlets.get(hash)).replaceAll("\"", ""));
        for (String hash : splineChartUnSelection.selectionSetMap.keySet())
            map.put("splineChartUnSelection_" + hash, CollectionUtils.setJoin(splineChartUnSelection.selectionSetMap.get(hash)));
        for (String hash : columnChartUnSelection.selectionSetMap.keySet())
            map.put("columnChartUnSelection_" + hash, CollectionUtils.setJoin(columnChartUnSelection.selectionSetMap.get(hash)));
        map.put("mainMenuSelection-taskType", mainMenuSelection.taskType.getCode());
        map.put("mainMenuSelection-creditDocumentary", mainMenuSelection.creditDocumentary == null? "0":String.valueOf(mainMenuSelection.creditDocumentary));
        //TODO сохраняем ещё и даты. Они пригодятся для именованных настроек
        for (String hash : dateFilter.keySet())
            map.put("dates_"+hash, dateFilter.get(hash).toJSON());
        return gson.toJson(map);
    }

    public PipelineSettings(User user, String setting, String settingName) {
        this.user = user;
        List<DepartmentExt> departmentExtList = SBeanLocator.getDashboardService().getDepartmentsExtForTree(user);
        try {
            Gson gson = new Gson();
            HashMap<String, String> map = new HashMap<String, String>();
            map = gson.fromJson(setting, map.getClass());
            for (String key : map.keySet())
                if (key.startsWith("chartType_")) {
                    chartTypes.put(key.replaceAll("chartType_", ""), ChartType.valueOf(map.get(key)));
                    if (key.contains("product"))
                        LOGGER.info("restore chartType_" + key.replaceAll("chartType_", "") + " - " + map.get(key));
                }
            for (String key : map.keySet())
                if (key.startsWith("tradingDesk_"))
                    tradingDeskSelectedMap.put(key.replaceAll("tradingDesk_", ""),
                                               PipelineSettingsSerializer.parsePipelineTradingDesk(map.get(key)));
            for (String key : map.keySet())
                if (key.startsWith("departments_"))
                    departmentsSelectedNodesMap.put(key.replaceAll("departments_", ""),
                                                    PipelineSettingsSerializer.parseDepartmentExt(map.get(key), departmentExtList));

            // grid columns
            for (String key : map.keySet())
                if (key.startsWith("stateGridColumnSelection_")) {
                    String hash = key.replaceAll("stateGridColumnSelection_", "");
                    stateGridColumnSelection.selectionSetMap.put(hash,
                                                                 PipelineSettingsSerializer
                                                                         .parseGridColumnItem(map.get(key),
                                                                                              isLimitHash(hash) ? GridStateLimitItem.values() :
                                                                                              GridStateOtherItem.values()));
                }
            for (String key : map.keySet())
                if (key.startsWith("topGridColumnSelection_")) {
                    String hash = key.replaceAll("topGridColumnSelection_", "");
                    topGridColumnSelection.selectionSetMap.put(hash,
                                                               PipelineSettingsSerializer
                                                                       .parseGridColumnItem(map.get(key),
                                                                                            isLimitHash(hash) ? GridTopLimitItem.values() :
                                                                                            GridTopOtherItem.values()));
                }

            // summary wondows
            for (String key : map.keySet())
                if (key.startsWith("summarySelection_")) {
                    String hash = key.replaceAll("summarySelection_", "");
                    summarySelection.selectionSetMap.put(hash,
                                                         PipelineSettingsSerializer.parseSummaryFigure(map.get(key),
                                                                                                       isLimitHash(hash) ?
                                                                                                       SummaryFigureLimit.values() :
                                                                                                       SummaryFigureOther.values()));
                }
            for (String key : map.keySet())
                if (key.startsWith("portlets_")) {
                    String hash = key.replaceAll("portlets_", "");
                    portlets.put(hash, gson.fromJson(map.get(key), PortletOnOff.class));
                }
            for (String key : map.keySet())
                if (key.startsWith("portalPosition_")) {
                    String hash = key.replaceAll("portalPosition_", "");
                    portalPosition.put(hash, gson.fromJson(map.get(key).replaceAll("_", "\""), PortalPosition.class));
                }
            for (String key : map.keySet())
                if (key.startsWith("splineChartUnSelection_")) {
                    String hash = key.replaceAll("splineChartUnSelection_", "");
                    splineChartUnSelection.selectionSetMap.put(hash, PipelineSettingsSerializer.parseHashSet(map.get(key)));
                }
            for (String key : map.keySet())
                if (key.startsWith("columnChartUnSelection_")) {
                    String hash = key.replaceAll("columnChartUnSelection_", "");
                    columnChartUnSelection.selectionSetMap.put(hash, PipelineSettingsSerializer.parseHashSet(map.get(key)));
                }

            setMainMenuSelection(map.get("mainMenuSelection-taskType"),Integer.valueOf(map.get("mainMenuSelection-creditDocumentary")));
            if (settingName!=null)//восстанавливаем даты
                for (String key : map.keySet())
                    if(key.startsWith("dates_")){
                        String hash = key.replaceAll("dates_","");
                        dateFilter.put(hash, new DateFilter(map.get(key)));
                    }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public boolean isLimitHash(String hash) {
        return hash.contains("limit");
    }

    private String mainMenuSelectionHash() {
        return mainMenuSelection.hash();
    }

    @Override
    public String toString() {
        return "PipelineSettings{" +
                "mainMenuSelection=" + mainMenuSelection +
                ", chartTypes=" + chartTypes +
                '}';
    }

    /** запомнить позицию */
    public void savePosition(List<String> allPortal, List<String> portalLayout) {
        LOGGER.info("savePosition");
        getPortalPosition().allPortal = allPortal;
        getPortalPosition().portalLayout = portalLayout;
    }
}
