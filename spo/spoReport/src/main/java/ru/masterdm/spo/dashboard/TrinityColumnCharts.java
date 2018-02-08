package ru.masterdm.spo.dashboard;

import org.zkoss.chart.Lang;
import org.zkoss.chart.Options;
import org.zkoss.chart.Series;
import org.zkoss.chart.YAxis;
import org.zkoss.chart.model.ChartsModel;
import org.zkoss.json.JavaScriptValue;

import static ru.masterdm.spo.dashboard.PipelineConstants.Characteristics;
import static ru.masterdm.spo.dashboard.PipelineConstants.CharacteristicsE;
import static ru.masterdm.spo.dashboard.PipelineConstants.UnitOfMeasure;

/**
 * @author pmasalov
 */
public class TrinityColumnCharts extends TrinityAbstractCharts {

    public TrinityColumnCharts() {
        init();
    }

    private void init() {
        boolean isLimit = "limit".equals(taskType);
        JavaScriptValue emptyJsFormatter = new JavaScriptValue("function() { return ''; }");

        this.getXAxis().setCategories(new String[] {"сравнительный", "отчетный"});

        // First Y axis
        YAxis yAxisAmount = this.getYAxis();
        yAxisAmount.setMin(0);
        yAxisAmount.getTitle().setText("");
        yAxisAmount.getLabels().setFormatter(emptyJsFormatter);
        // Secondary y Axis
        YAxis yAxisCount = this.getYAxis(1);
        yAxisCount.setTitle("");
        yAxisCount.getLabels().setFormatter(emptyJsFormatter);
        yAxisCount.setOpposite(true);

        if (!isLimit) {
            // Third y Axis
            YAxis yAxisPercent = this.getYAxis(2);
            yAxisPercent.setTitle("");
            yAxisPercent.getLabels().setFormatter(emptyJsFormatter);
            yAxisPercent.setOpposite(true);
        }

        this.getPlotOptions().getColumn().setPointPadding(0.05);
        this.getPlotOptions().getColumn().setBorderWidth(0);

        int series = 0;
        // порядок инициализации серий должен совпадать с порядком инициализации серий в модели (sum, profit, count, маржа)
        Series sum = this.getSeries(series++);
        sum.setName(Characteristics.SUM_RUB);
        sum.setId(CharacteristicsE.SUM_RUB.toString());
        sum.setType("column");
        sum.setYAxis(0);
        sum.setColor(seriesColor(Characteristics.SUM_RUB));
        sum.getTooltip().setEnabled(true);
        sum.getTooltip().setHeaderFormat("");
        sum.getTooltip().setPointFormat(
                "<span style=\"color:{point.color}\">\u25CF</span><b> " + Characteristics.SUM_RUB + " {point.y} " + UnitOfMeasure.MLN_RUB + "</b>");

        Series count = this.getSeries(series++);
        count.setName(Characteristics.COUNT_ALL);
        count.setId(CharacteristicsE.COUNT_ALL.toString());
        count.setType("column");
        count.setYAxis(1);
        count.setColor(seriesColor(Characteristics.COUNT_ALL));

        count.getTooltip().setEnabled(true);
        count.getTooltip().setHeaderFormat("");
        count.getTooltip().setPointFormat(
                "<span style=\"color:{point.color}\">\u25CF</span><b> " + Characteristics.COUNT_ALL + " {point.y} " + UnitOfMeasure.PIECE + "</b>");

        if (!isLimit) {
            Series margin = this.getSeries(series++);
            margin.setName(Characteristics.WAV_MARGIN);
            margin.setId(CharacteristicsE.WAV_MARGIN.toString());
            margin.setType("column");
            margin.setYAxis(2);
            margin.setColor(seriesColor(Characteristics.WAV_MARGIN));
            margin.getTooltip().setEnabled(true);
            margin.getTooltip().setHeaderFormat("");
            margin.getTooltip().setPointFormat(
                    "<span style=\"color:{point.color}\">\u25CF</span><b> " + Characteristics.WAV_MARGIN + " {point.y} " + UnitOfMeasure.PERCENT
                            + "</b>");
        }

        getExporting().setEnabled(false);
        getLegend().setEnabled(false);

        Options options = getOptions();
        if (options == null) options = new Options();
        Lang lang = options.getLang();
        if (lang == null) lang = new Lang();
        lang.setNoData("Не выбраны атрибуты для просмотра");
        options.setLang(lang);
        setOptions(options);
    }

    @Override
    public void setModel(ChartsModel model) {
        super.setModel(model);
    }

    public static String seriesColor(String seriesName) {
        if (Characteristics.WAV_MARGIN.equals(seriesName))
            return "#0a78cc";
        if (Characteristics.COUNT_ALL.equals(seriesName))
            return "#7dabce";
        if (Characteristics.SUM_RUB.equals(seriesName))
            return "#c2c9ce";

        return null;
    }
}
