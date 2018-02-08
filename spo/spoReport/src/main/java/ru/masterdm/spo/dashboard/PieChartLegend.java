package ru.masterdm.spo.dashboard;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Color;
import org.zkoss.chart.Legend;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.chart.util.AnyVal;
import org.zkoss.json.JavaScriptValue;

/**
 * @author pmasalov
 */
public class PieChartLegend extends Charts {

    protected Map<String, String> pieChartColors;

    public PieChartLegend() {
        // torn off any except legend
        JavaScriptValue emptyJsFormatter = new JavaScriptValue("function() { return ''; }");

        Series series = getSeries();

        this.setMarginBottom(0);
        this.setMarginTop(0);
        this.getExporting().setEnabled(false);

        this.getTitle().setStyle("visibility: hidden; height: 0px; font-size: 0px; line-height: 0px;");
        this.getTitle().setText("");

        this.getNoData().setStyle("visibility: hidden; height: 0px; font-size: 0px; line-height: 0px;");
        this.getNoData().addExtraAttr("useHTML", new AnyVal<Boolean>(true));

        this.setSpacingBottom(0);
        this.setSpacingTop(5);

        Legend legend = getLegend();
        legend.setVerticalAlign("top");
        legend.setAlign("left");
        //this.getLegend().setSymbolPadding(0);
        legend.setItemStyle("fontSize: 12px; fontWeight: 'normal';");
        legend.setPadding(0);
        legend.setEnabled(true);
        legend.getNavigation().addExtraAttr("enabled", new AnyVal<Boolean>(false));
        legend.setItemHoverStyle("cursor: default;");
        legend.setItemHiddenStyle("cursor: default; color: #333333;");
        legend.setY(0);

        getTooltip().setPointFormatter(emptyJsFormatter);

        PiePlotOptions reportPlotOptions = getPlotOptions().getPie();
        reportPlotOptions.setShowInLegend(true);
        reportPlotOptions.setSize("0px");
        reportPlotOptions.setInnerSize("0px");
        reportPlotOptions.setBorderWidth(0);
        reportPlotOptions.setSlicedOffset(0);
        reportPlotOptions.setEnableMouseTracking(false);
        reportPlotOptions.getStates().getHover().setEnabled(false);
        reportPlotOptions.setAllowPointSelect(false);

        series.getTooltip().setEnabled(false);
        series.getDataLabels().setEnabled(false);

    }

    public void setPieChartColors(Map<String, String> pieChartColors) {
        this.pieChartColors = pieChartColors;
    }

    private Color getColor(String industry) {
        if(pieChartColors.containsKey(industry))
            return new Color(pieChartColors.get(industry));
        return new Color(PipelineConstants.PIE_COLORS[0]);
    }

    public void setIndustries(Set<String> industriesName) {
        Series s = this.getSeries();
        s.setData(new ArrayList());

        for(String industry : industriesName){
            Point p = new Point(industry, 10);
            p.setColor(getColor(industry));
            s.addPoint(p);
        }

    }
}
