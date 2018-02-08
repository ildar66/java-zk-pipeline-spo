package ru.masterdm.spo.dashboard;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Panel;

/**
 * @author pmasalov
 */
@Deprecated
public class SummaryPanel extends Panel {

    public SummaryPanel() {
        super();

        setAttribute("org.zkoss.zk.ui.updateByClient", true);
        setWidgetListener("onMinimize", "event = new zk.Event(zk.Widget.$(this), 'onMinimize', {toServer:true});zAu.send(event);zAu.sendNow(event.target.desktop);");
        //onMinimize
        addEventListener("onMinimize", new EventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                System.out.println("onMinimize - " + event);
            }
        });
    }
}
