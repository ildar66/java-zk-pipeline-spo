package ru.masterdm.spo.dashboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Panel;

import ru.masterdm.spo.dashboard.domain.SummaryData;

/**
 * @author pmasalov
 */
public class SummaryFigurePanel extends Panel {

    private SummaryData summaryData;

    /**
     * Returns .
     * @return
     */
    public SummaryData getSummaryData() {
        return summaryData;
    }

    /**
     * Sets .
     * @param summaryData
     */
    public void setSummaryData(SummaryData summaryData) {
        this.summaryData = summaryData;
    }

    @Override
    public void onClose() {
        setVisible(false);
        //event.stopPropagation();

        String menuCheckId = "#summaryMenu-" + summaryData.getSummaryFigure().getCode();
        Menuitem mi = (Menuitem) query(menuCheckId);
        if (mi != null)
            mi.setChecked(false);
    }
}
