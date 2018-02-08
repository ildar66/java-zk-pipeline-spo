package ru.masterdm.spo.dashboard;

import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.Legend;
import org.zkoss.chart.Series;
import org.zkoss.chart.util.AnyVal;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import static ru.masterdm.spo.dashboard.PipelineConstants.Characteristics;

import java.util.List;

/**
 * @author pmasalov
 */
public class ChartLegend extends Charts {

    public static class LegendSeries {

        private String id;
        private String name;
        private boolean visible = true;

        public LegendSeries() {
        }

        public LegendSeries(String id, String name, boolean visible) {
            this.id = id;
            this.name = name;
            this.visible = visible;
        }

        /**
         * Returns .
         * @return
         */
        public String getId() {
            return id;
        }

        /**
         * Sets .
         * @param id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Returns .
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * Sets .
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Returns .
         * @return
         */
        public boolean isVisible() {
            return visible;
        }

        /**
         * Sets .
         * @param visible
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    public ChartLegend() {
        JavaScriptValue emptyJsFormatter = new JavaScriptValue("function() { return ''; }");

        this.setMarginBottom(0);
        this.setMarginTop(0);

        this.getExporting().setEnabled(false);

        this.getTitle().setStyle("visibility: hidden; height: 0px; font-size: 0px; line-height: 0px;");
        this.getTitle().setText("");

        this.getYAxis().getTitle().setText("");
        this.getYAxis().getLabels().setFormatter(emptyJsFormatter);
        this.getYAxis().setShowEmpty(false);
        this.getYAxis().addExtraAttr("visible", new AnyVal<Boolean>(false));

        this.getXAxis().setShowEmpty(false);
        this.getXAxis().getLabels().setFormatter(emptyJsFormatter);
        this.getXAxis().addExtraAttr("visible", new AnyVal<Boolean>(false));
        this.getXAxis().setLineWidth(0);
        this.getXAxis().getTitle().setStyle("visibility: hidden; height: 0px; font-size: 0px; line-height: 0px;");

        this.getNoData().setStyle("visibility: hidden; height: 0px; font-size: 0px; line-height: 0px;");
        this.getNoData().addExtraAttr("useHTML", new AnyVal<Boolean>(true));

        this.setSpacingBottom(0);
        this.setSpacingTop(0);

        Legend legend = getLegend();
        legend.setVerticalAlign("middle");
        legend.setAlign("left");
        //this.getLegend().setSymbolPadding(0);
        legend.setItemStyle("fontSize: 12px; fontWeight: 'normal';");
        legend.setPadding(0);
        legend.getNavigation().addExtraAttr("enabled", new AnyVal<Boolean>(false));

    }

    public void setSeries(List<LegendSeries> series) {
        int i = 0;
        for (LegendSeries ls : series) {
            Series ser = this.getSeries(i++);
            ser.setName(ls.getName());
            ser.setId(ls.getId());
            if ("column".equals(getType()))
                ser.setColor(TrinityColumnCharts.seriesColor(ser.getName()));
            else if ("spline".equals(getType()))
                ser.setColor(TrinityLineCharts.seriesColor(ser.getName()));
            ser.setVisible(ls.isVisible());
        }
    }

}
