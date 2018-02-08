package ru.masterdm.spo.dashboard;

import java.util.List;
import java.util.Set;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Lang;
import org.zkoss.chart.Options;
import org.zkoss.chart.Series;
import org.zkoss.chart.model.ChartsModel;
import org.zkoss.json.JavaScriptValue;

import ru.md.domain.dashboard.TaskTypeStatus;

import ru.masterdm.spo.utils.SBeanLocator;

/**
 * @author Andrey Pavlenko
 */
public class TrinityLineCharts extends TrinityAbstractCharts {

    protected String taskType;
    private Set<String> invisibleSeriesId;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public TrinityLineCharts() {
        init();
    }

    private void init() {
        JavaScriptValue emptyJsFormatter = new JavaScriptValue("function() { return this.value; }");
        getYAxis().setMin(0.0);
        getXAxis().getLabels().setStyle("color: '#606060';font-size: 8px;");
        getYAxis().getLabels().setFormatter(emptyJsFormatter);
        getYAxis().setTitle("");
        getExporting().setEnabled(false);

        initStatuses();

        Options options = getOptions();
        if (options == null) options = new Options();
        Lang lang = options.getLang();
        if (lang == null) lang = new Lang();
        lang.setNoData("Не выбраны статусы для просмотра");
        options.setLang(lang);
        setOptions(options);

        getLegend().setEnabled(false);
    }

    @Override
    public void setModel(ChartsModel model) {
        super.setModel(model);
        initStatuses();
    }

    @Override
    public void setSeriesInvisible(Set<String> invisibleSeriesId) {
        this.invisibleSeriesId = invisibleSeriesId;
        initStatuses();
    }

    private void initStatuses() {
        List<TaskTypeStatus> statuses = SBeanLocator.getDashboardService().getTaskTypeStatusesInOrder(taskType);
        int series = 0;
        for (TaskTypeStatus s : statuses) {
            Series ser = this.getSeries(series++);
            ser.setId(Integer.toString(s.getIdStatus()));
            ser.setName(s.getStatus());
            ser.setColor(seriesColor(s.getStatus()));
            if (invisibleSeriesId == null || invisibleSeriesId.size() == 0) {
                ser.setVisible(true);
            } else {
                ser.setVisible(!invisibleSeriesId.contains(ser.getId()));
            }
        }
    }

    public static String seriesColor(String seriesName) {
        if (seriesName.startsWith("Новые"))
            return "#0a78cc";
        if (seriesName.startsWith("Выборка") || seriesName.equals("В работе"))
            return "#4693ce";
        if (seriesName.startsWith("Одобр"))
            return "#7dabce";
        if (seriesName.startsWith("Отказ"))
            return "#c2c9ce";
        return "#339900";
    }

}
