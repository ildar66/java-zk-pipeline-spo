package ru.masterdm.spo.dashboard;

import static ru.masterdm.spo.dashboard.PipelineConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindComposer;
import org.zkoss.chart.Charts;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zkmax.zul.Tablelayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.North;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;
import org.zkoss.zuti.zul.ForEach;

import ru.masterdm.spo.dashboard.domain.SummaryData;

/**
 * Created by drone on 22.07.16.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PipelineController extends BindComposer<Window> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineController.class);

    @Wire
    private Popup detailReportSwitch;

    @Wire
    private Window dataWindow;

    @Wire
    private Listbox tradingDeskListbox;
    @Wire
    private Component tradingDesksSelected;
    @Wire
    private Component tradingDesksSelectedPopup;
    @Wire
    private Tree departmentsTree;
    @Wire
    private Component departmentsSelected;
    @Wire
    private Component departmentsSelectedPopup;

    @Wire
    private Tablelayout chartsTable;
    @Wire
    private ForEach chartsTableForEach;

    @Wire
    private Portallayout portalLayout;
    @Wire
    private Portallayout allPortal;
    @Wire
    private Center allPortalCenter;
    @Wire
    private North allPortalNorth;

    @Wire
    private Panel chartPanel;
    @Wire
    Component chartTableParent;
    @Wire
    Columns stateGridColumns;

    @Wire
    private Panel top3Panel;
    @Wire
    private Panel summaryPanel;

    private Checkbox chartPanelSwitch;
    @Wire
    private Checkbox top3PanelSwitch;
    @Wire
    private Checkbox summaryPanelSwitch;
    @Wire
    private Popup editSettingsPopup;

    @Wire
    private Popup tradingDeskPopup;
    @Wire
    private Popup departmentPopup;

    @Listen("onPortalMove = #allPortal")
    public void savePositionAllPortal() {
        savePosition();
    }

    @Listen("onPortalMove = #portalLayout")
    public void savePositionPortalLayout() {
        savePosition();
    }

    private void savePosition() {
        List<String> allPortalIds = new ArrayList<String>();
        for (Component portalChild : allPortal.getChildren())
            for (Component portlet : portalChild.getChildren())
                allPortalIds.add(portlet.getId());
        List<String> portalLayoutIds = new ArrayList<String>();
        for (Component portalChild : portalLayout.getChildren())
            for (Component portlet : portalChild.getChildren())
                portalLayoutIds.add(portlet.getId());
        getPipelineSettings().savePosition(allPortalIds, portalLayoutIds);
        PipelineVM vm = (PipelineVM) getViewModel();
        vm.savePipelineSettings();
    }

    @Listen("onCreate = #portalLayout")
    public void initStatusPortalLayout() {//Верхняя панель chart-and-summary
        if (getPipelineSettings() == null)
            return;
        initPortal(portalLayout, getPipelineSettings().getPortalPosition().portalLayout);
    }

    private PipelineSettings getPipelineSettings() {
        PipelineVM vm = (PipelineVM) getViewModel();
        return vm.getSettings();
    }

    private void initPortal(Portallayout layout, List<String> idList) {
        try {

        } catch (Exception e) {

        }
        List<? extends Component> panelchildrens = layout.getChildren();
        if (layout.getChildren().size() > 0 && idList != null && idList.size() > 0) {
            Component panelchildren = panelchildrens.get(0);
            String first = idList.get(0);
            if (panelchildren.getChildren().size() > 0 && !first.equals(panelchildren.getChildren().get(0).getId())) {
                panelchildren.insertBefore(layout.getFellow(first), panelchildren.getChildren().get(0));
            }
        }
    }

    @Listen("onCreate = #allPortal")
    public void initStatusAllPortal() {//top 10
        if (getPipelineSettings() == null)
            return;
        initPortal(allPortal, getPipelineSettings().getPortalPosition().allPortal);
    }

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) throws Exception {
        super.doBeforeCompose(page, parent, compInfo);
        Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), BindComposer.class));

        /*page.addEventListener("onClientInfo", new EventListener<ClientInfoEvent>() {

            @Override
            public void onEvent(ClientInfoEvent event) throws Exception {
                //System.out.println("PAGE - " + event + " dh=" + event.getDesktopHeight() + " dw=" + event.getDesktopWidth());
                portalLayout.invalidate();
            }
        });*/

        return compInfo;
    }

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        Selectors.wireComponents(comp, this, false);
        Selectors.wireEventListeners(comp, this);

        EventListener listBoxChange = new EventListener() {

            @Override
            public void onEvent(Event event) throws Exception {
                tradingDesksSelected.invalidate();
                tradingDesksSelectedPopup.invalidate();
            }
        };
        tradingDeskListbox.addEventListener("onSelect", listBoxChange);
        tradingDeskListbox.addEventListener("onCheckSelectAll", listBoxChange);

        departmentsTree.addEventListener("onSelect", new EventListener<SelectEvent>() {

            @Override
            public void onEvent(SelectEvent event) throws Exception {
                // выбираеются не только текущая строка но и все её дочернии элементы
                departmentsTree.setModel(departmentsTree.getModel());
                departmentsSelected.invalidate();
                departmentsSelectedPopup.invalidate();
            }
        });

    }

    @Listen("onCheck = #chartPanelSwitch, #top3PanelSwitch, #summaryPanelSwitch")
    public void onPanelSwitcherClick(CheckEvent event) {
        if ("summaryPanelSwitch".equals(event.getTarget().getId()) || "chartPanelSwitch".equals(event.getTarget().getId())) {
            portalLayout.invalidate();
        }

        editSettingsPopup.close();
    }

    /**
     * Переопределение закрытия окон
     * Окна делаются невидимыми
     */
    @Listen("onClose = #chartPanel, #top3Panel, #summaryPanel")
    public void onCloseReportPanels(Event event) {
        //System.out.println("onCloseReportPanels -> " + event.getTarget().getId());
        if ("summaryPanel".equals(event.getTarget().getId()) || "chartPanel".equals(event.getTarget().getId())) {
            portalLayout.invalidate();
        }

        event.stopPropagation();
    }

    @Listen("onClick = #detailReportInitiator")
    public void onClickDetailReportInitiator(Event event) {
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.STATUS_ID, null);
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.CATEGORY, null);
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.BRANCH, null);
    }

    @Listen("onClick = toolbarbutton#toTable, toolbarbutton#toPdf, toolbarbutton#toExcel")
    public void onClickPopupToolbar() {
        detailReportSwitch.close();
    }

    @Listen("onCancel = #dataWindow, #editSettingsPopup, #detailReportSwitch")
    public void escPressed() {
        // close popup if any is opened
        for (Component c : dataWindow.queryAll("popup")) {
            Popup p = (Popup) c;
            p.close();
        }
    }

    @Listen("onClick = button#columnChartMode, button#pieChartMode, button#lineChartMode")
    public void onClickChartToolbar() {
        // более "мягко" обновляется
        invalidateChart();
    }

    @Listen("onCheck = #editSummaryPanels > menuitem")
    public void onSummaryMenuCheck(CheckEvent event) {
        SummaryData so = (SummaryData) event.getTarget().getAttribute("summaryData");
        String panelId = "#panel-" + so.getSummaryFigure().getCode();
        SummaryFigurePanel pc = (SummaryFigurePanel) dataWindow.query(panelId);
        if (pc != null)
            pc.setVisible(event.isChecked());

        PipelineVM vm = (PipelineVM) getViewModel();
        vm.doSelectSummaryData(so, event.isChecked(), pc);
        vm.savePipelineSettings();
    }

    private void invalidateChart() {
        dataWindow.invalidate();
    }

    @Listen("onClick = #searchAtTradingDeskButton")
    public void onSearchAtTradingDeskButton() {
        tradingDeskPopup.close();
        invalidateChart();
    }

    @Listen("onClick = #searchAtDepartmentButton")
    public void onSearchAtDepartmentButton() {
        departmentPopup.close();
        invalidateChart();
    }

    @Listen("onClick = #searchButton")
    public void onSearchButton() {
        invalidateChart();
    }

    @Listen("onAfterSize = #summaryPanel")
    public void onAfterSizeSummaryPanel(AfterSizeEvent event) {
        //System.out.println("onAfterSizeSummaryPanel - " + event + " w=" + event.getWidth() + " h=" + event.getHeight());
        if (((PipelineVM) getViewModel()).getSettings().getSummarySelection().getSelected().size() >= MIN_SUMMARY_TO_SYNC_HEIGHT
                && event.getHeight() >= 40 && chartPanel.isVisible()) { // способ известный на данный момент как определить что панель не свёрнута
            //System.out.println("chartPanel.setHeight(event.getHeight() + \"px\"); vis = " + chartPanel.isVisible());
            chartPanel.setHeight(event.getHeight() + "px");
            chartPanel.invalidate();
        }
    }

    private int chartPanelHeight = 0;

    @Listen("onAfterSize = #chartPanel")
    public void onAfterSizeChartPanel(AfterSizeEvent event) {
        /*System.out.println(
                ">>>onAfterSizeChartPanel - " + event + " w=" + event.getWidth() + " h=" + event.getHeight() + " do: tb=" + chartToolBarHeight
                        + " gr="
                        + stateGridHeight);*/
        escPressed();
        chartPanelHeight = event.getHeight();
    }

    private int chartTableParentHeight = 0, chartTableParentWidth = 0;

    @Listen("onAfterSize = #chartTableParent")
    public void onAfterSizeChartTableParent(AfterSizeEvent event) {
        //System.out.println("onAfterSizeChartTableParent - " + event + " w=" + event.getWidth() + " h=" + event.getHeight());
        chartTableParentHeight = event.getHeight();
        chartTableParentWidth = event.getWidth();
    }

    private int chartToolBarHeight = 0;

    @Listen("onAfterSize = #chartToolBar")
    public void onAfterSizeChartToolBar(AfterSizeEvent event) {
        //System.out.println("onAfterSizeChartToolBar - " + event + " w=" + event.getWidth() + " h=" + event.getHeight());
        chartToolBarHeight = event.getHeight();
    }

    private int stateGridHeight = 0;

    @Listen("onAfterSize = #stateGrid")
    public void onAfterSizeStateGrid(AfterSizeEvent event) {
        //System.out.println("onAfterSizeStateGrid - " + event + " w=" + event.getWidth() + " h=" + event.getHeight());
        stateGridHeight = event.getHeight();
    }

    private int stateGridColumnsHeight = 0;

    @Listen("onAfterSize = #stateGridColumns")
    public void onAfterSizeStateGridColumns(AfterSizeEvent event) {
        //System.out.println("onAfterSizeStateGridColumns - " + event + " w=" + event.getWidth() + " h=" + event.getHeight());
        stateGridColumnsHeight = event.getHeight();
        doResizeCharts();
    }

    private void doResizeCharts() {
        if (stateGridHeight != 0 && chartToolBarHeight != 0 && stateGridColumnsHeight != 0 && chartTableParentHeight != 0
                && chartTableParentWidth != 0 && chartPanelHeight != 0) {
            resizeCharts(chartTableParentWidth, chartPanelHeight - chartToolBarHeight - stateGridHeight - stateGridColumnsHeight);
            stateGridHeight = chartToolBarHeight = stateGridColumnsHeight = 0;
            chartTableParentHeight = 0;
            chartTableParentWidth = 0;
            chartPanelHeight = 0;
        }
    }

    private void resizeCharts(int plateWidth, int plateHeight) {
        int w = (int) (plateWidth * .45);
        int h = (plateHeight - 50) / 2;

                    /*- 6 //tool bottom
                    - 15 //table row inner
                    - 10 //vlayout
                    - 22 //title
                    ;*/

        //System.out.println("chart width = " + w + " height=" + h);
        for (Component c : dataWindow.queryAll("#chartsTable charts")) {
            ((Charts) c).setWidth(w);
            ((Charts) c).setHeight(h);
        }
    }
}
