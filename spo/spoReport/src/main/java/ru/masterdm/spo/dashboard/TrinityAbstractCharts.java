package ru.masterdm.spo.dashboard;

import java.util.Set;

import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.Series;
import org.zkoss.chart.model.ChartsModel;
import org.zkoss.zul.Popup;

/**
 * @author pmasalov
 */
public abstract class TrinityAbstractCharts extends Charts {

    protected int idStatus;
    protected String taskType;

    /**
     * Returns .
     * @return
     */
    public int getIdStatus() {
        return idStatus;
    }

    /**
     * Sets .
     * @param idStatus
     */
    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    /**
     * Returns .
     * @return
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets .
     * @param taskType
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setSeriesInvisible(Set<String> invisibleSeriesId) {
        int sz = getSeriesSize();
        for (int i = 0; i < sz; i++) {
            Series s = getSeries(i);
            if (invisibleSeriesId == null || invisibleSeriesId.size() == 0) {
                s.setVisible(true);
            } else {
                s.setVisible(!invisibleSeriesId.contains(s.getId()));
            }
        }
    }

/*    @Override
    public void setModel(ChartsModel model) {
        super.setModel(model);
        //setupChartModel(model);
    }*/

    public void onPlotClick(ChartsEvent event) {
        Popup detailReportSwitch = (Popup) query("#detailReportSwitch");
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.STATUS_ID, new Integer(idStatus));
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.CATEGORY, event.getCategory().toString());
        detailReportSwitch.setAttribute(PipelineConstants.DetailReportAttribute.BRANCH, null);

        detailReportSwitch.open(event.getTarget(), "at_pointer");
    }
}
