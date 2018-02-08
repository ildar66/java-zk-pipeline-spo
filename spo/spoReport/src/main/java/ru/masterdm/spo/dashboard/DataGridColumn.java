package ru.masterdm.spo.dashboard;

import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import ru.masterdm.spo.dashboard.model.metadata.GridColumnMetadata;

/**
 * @author pmasalov
 */
public class DataGridColumn extends org.zkoss.zul.Column {

    public static final String ON_SHOW_EVENT = "onShow";
    public static final String ON_HIDE_EVENT = "onHide";

    private static final Logger LOGGER = LoggerFactory.getLogger(DataGridColumn.class);
    private GridColumnMetadata metadata;

    public DataGridColumn() {
        super();

        setAttribute("org.zkoss.zk.ui.updateByClient", true);
        setWidgetListener(ON_HIDE_EVENT, "this.setSclass('md-z-column-content-hidden');this.smartUpdate('visible', this._visible);"
                + "event = new zk.Event(zk.Widget.$(this), '" + ON_HIDE_EVENT + "', {toServer:true});zAu.send(event);zAu.sendNow(event.target.desktop);");
        setWidgetListener(ON_SHOW_EVENT, "this.setSclass('md-z-column-content'); this.smartUpdate('visible', this._visible);"
                + "event = new zk.Event(zk.Widget.$(this), '" + ON_SHOW_EVENT + "', {toServer:true});zAu.send(event);zAu.sendNow(event.target.desktop);");
    }

    /**
     * Returns .
     * @return
     */
    public GridColumnMetadata getMetadata() {
        return metadata;
    }

    /**
     * Sets .
     * @param metadata
     */
    public void setMetadata(GridColumnMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean setVisible(boolean visible) {
        //System.out.println("DataGridColumn.setVisible " + visible);
        if (metadata != null)
            metadata.setVisible(visible);

        if (visible) {
            if (!"md-z-column-content".equals(getSclass()))
                setSclass("md-z-column-content");
        } else {
            if (!"md-z-column-content-hidden".equals(getSclass()))
                setSclass("md-z-column-content-hidden");
        }
        return super.setVisible(visible);
    }

}
