package ru.masterdm.spo.dashboard;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.Color;
import org.zkoss.chart.Lang;
import org.zkoss.chart.Legend;
import org.zkoss.chart.Options;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.ChartsModel;
import org.zkoss.chart.plotOptions.DataLabels;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.chart.util.AnyVal;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.zul.Popup;

/**
 * @author Andrey Pavlenko
 */
public class TrinityPieCharts extends TrinityAbstractCharts {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrinityPieCharts.class);

    public TrinityPieCharts() {
        init();
    }

    protected Map<String, String> pieChartColors;

    public Map<String, String> getPieChartColors() {
        return pieChartColors;
    }

    public void setPieChartColors(Map<String, String> pieChartColors) {
        this.pieChartColors = pieChartColors;
    }

    @Override
    public void setModel(ChartsModel model) {
        setupChartModel0((CategoryModel) model);
    }

    private Color getColor(String industry) {
        //LOGGER.info("getColor "+(getPieChartColors().size()));
        if(getPieChartColors().containsKey(industry))
            return new Color(getPieChartColors().get(industry));
        return new Color(PipelineConstants.PIE_COLORS[0]);
    }

    private void init() {
        JavaScriptValue emptyJsFormatter = new JavaScriptValue("function() { return ''; }");
        getTooltip().setPointFormatter(emptyJsFormatter);

        Series compare = this.getSeries();
        Series report = this.getSeries(1);
        report.setName(PipelineConstants.Period.REPORT);
        compare.setName(PipelineConstants.Period.COMPARE);

        PiePlotOptions comparePlotOptions = new PiePlotOptions();
        comparePlotOptions.setSize("60%");
        comparePlotOptions.setInnerSize("20%");
        comparePlotOptions.setCursor("pointer");
        compare.setPlotOptions(comparePlotOptions);
        compare.getDataLabels().setEnabled(false);

        PiePlotOptions reportPlotOptions = new PiePlotOptions();
        reportPlotOptions.setSize("80%");
        reportPlotOptions.setInnerSize("60%");
        reportPlotOptions.setCursor("pointer");
        report.setPlotOptions(reportPlotOptions);
        report.getDataLabels().setEnabled(false);

        getTooltip().setPointFormat("{series.name} период: {point.y:0.0f} млн.руб. <b>({point.percentage:.1f}%)</b>");

        comparePlotOptions.setShowInLegend(false);
        reportPlotOptions.setShowInLegend(true);
        getExporting().setEnabled(false);

        Legend legend = getLegend();
        legend.setEnabled(true);
        legend.setItemStyle("fontSize: 12px; fontWeight: 'normal';");
        legend.setPadding(0);
        legend.getNavigation().addExtraAttr("enabled", new AnyVal<Boolean>(false));
        legend.setItemHoverStyle("cursor: default;");
        legend.setItemHiddenStyle("cursor: default; color: #333333;");

        Options options = getOptions();
        if (options == null) options = new Options();
        Lang lang = options.getLang();
        if (lang == null) lang = new Lang();
        lang.setNoData("Нет заявок для текущего фильтра");
        options.setLang(lang);
        setOptions(options);

        /*LOGGER.info("getEventListenerMap");
        LOGGER.info(getEventListenerMap().toString());
        LOGGER.info("getEventHandlerNames");
        for (String s : getEventHandlerNames())
            LOGGER.info(s);
        LOGGER.info("getClientEvents");
        for (String s : getClientEvents().keySet())
            LOGGER.info(s);
        getClientEvents().remove("onClick");*/
    }

    private void setupChartModel0(CategoryModel m) {
        Series compare = this.getSeries();
        Series report = this.getSeries(1);

        for(Object indObj : m.getSeries()){
            String industry = (String) indObj;
            Point p = new Point(industry, m.getValue(industry,PipelineConstants.Period.REPORT));
            p.setColor(getColor(industry));
            report.addPoint(p);
            p = new Point(industry, m.getValue(industry,PipelineConstants.Period.COMPARE));
            p.setColor(getColor(industry));
            compare.addPoint(p);
        }
    }

    public void onPlotClick(ChartsEvent event) {
        LOGGER.info("onPlotClick" + event.getCategory().toString());
        Popup detailReportSwitch = (Popup) query("#detailReportSwitch");
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.STATUS_ID, new Integer(idStatus));
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.CATEGORY,
                            event.getSeriesIndex()==1?PipelineConstants.Period.REPORT:PipelineConstants.Period.COMPARE);
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.BRANCH, event.getCategory().toString());
        detailReportSwitch.open(event.getTarget(), "at_pointer");
    }
}
